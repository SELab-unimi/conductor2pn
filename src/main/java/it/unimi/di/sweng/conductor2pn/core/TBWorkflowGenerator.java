package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.di.sweng.conductor2pn.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TBWorkflowGenerator extends WorkflowGenerator {

    @Override
    protected List<String> simpleTask(List<String> inputElements, JsonElement workerElement, TBNet net) {
        String workerName = workerElement.getAsJsonObject().get(NAME).getAsString();
        Place schedulePlace = net.getPlace(WorkerGenerator.schedulePlaceName(workerName));

        if(!inputElements.isEmpty()) {
            List<Place> inputPlaces = getPlaces(inputElements, net);
            List<Transition> inputTransitions = getTransitions(inputElements, net);

            if(!inputPlaces.isEmpty() && !inputTransitions.isEmpty())
                throw new IllegalArgumentException();

            if(!inputPlaces.isEmpty()) {
                Transition startTask = new Transition(startTaskTransitionName(workerName),
                        Transition.ENAB, Transition.ENAB + "+Z",false);
                net.addNode(startTask);
                for(Place inputPlace: inputPlaces) {
                    net.addArc(new Arc(inputPlace, startTask));
                    net.addArc(new Arc(startTask, schedulePlace));
                }
            }
            else {
                for(Transition inputTransition: inputTransitions)
                    net.addArc(new Arc(inputTransition, schedulePlace));
            }
        }
        else {
            schedulePlace.putTokens(1, "T0");
        }

        List<String> result = new ArrayList<>();
        result.add(WorkerGenerator.completePlaceName(workerName));
        return result;
    }

    @Override
    protected List<String> eventTask(List<String> inputElements, JsonElement workerElement, TBNet net) {
        String eventName = workerElement.getAsJsonObject().get(NAME).getAsString();

        List<Place> inputPlaces = getPlaces(inputElements, net);
        if(inputPlaces.isEmpty())
            throw new IllegalArgumentException();

        Transition startEventTransition = new Transition(startEventTransitionName(eventName),
                Transition.ENAB, Transition.ENAB + "+E",false);
        Place eventGeneratedPlace = new Place(eventPlaceName(eventName));
        Place eventToBeHandledPlace = new Place(eventToBeHandledName(eventName));
        net.addNode(startEventTransition);
        net.addNode(eventGeneratedPlace);
        net.addNode(eventToBeHandledPlace);
        net.addArc(new Arc(startEventTransition, eventGeneratedPlace));
        net.addArc(new Arc(startEventTransition, eventToBeHandledPlace));

        for(Place inputPlace: inputPlaces) {
            net.addArc(new Arc(inputPlace, startEventTransition));
        }

        List<String> result = new ArrayList<>();
        result.add(eventGeneratedPlace.getName());
        return result;
    }

    @Override
    protected List<String> forkTask(List<String> inputElements, JsonElement currentElement, TBNet net) {
        String forkName = currentElement.getAsJsonObject().get(NAME).getAsString();
        JsonArray forkTasks = currentElement.getAsJsonObject().get(FORK_TASKS).getAsJsonArray();

        List<Place> inputPlaces = getPlaces(inputElements, net);
        if(inputPlaces.isEmpty())
            throw new IllegalArgumentException();

        Transition forkTransition = new Transition(forkTransitionName(forkName),
                Transition.ENAB, Transition.ENAB + "+J",false);
        net.addNode(forkTransition);
        for(Place inputPlace: inputPlaces) {
            net.addArc(new Arc(inputPlace, forkTransition));
        }
        inputElements = new ArrayList<>();
        inputElements.add(forkTransition.getName());

        List<String> result = new ArrayList<>();
        for(JsonElement element: forkTasks)
            result.addAll(createWorkflow(inputElements, element, net));
        return result;
    }

    @Override
    protected List<String> joinTask(List<String> inputElements, JsonElement currentElement, TBNet net) {
        String taskName = currentElement.getAsJsonObject().get(NAME).getAsString();

        Transition joinTransition = null;
        if(!taskName.contains(DYNAMIC)) {
            List<Place> inputPlaces = getPlaces(inputElements, net);
            if (inputPlaces.isEmpty())
                throw new IllegalArgumentException();

            joinTransition = new Transition(joinTransitionName(taskName),
                    Transition.ENAB, Transition.ENAB + "+J", false);
            net.addNode(joinTransition);
            for (Place inputPlace : inputPlaces) {
                net.addArc(new Arc(inputPlace, joinTransition));
            }
        }
        else {
            Place activeTaskDone = new Place(dynamicForkActiveTaskDonePlaceName(taskName));
            net.addNode(activeTaskDone);

            Place activeTasks = null;
            List<Place> inputPlaces = getPlaces(inputElements, net);
            for (Place p: inputPlaces)
                if(p.getName().contains(ACTIVE_TASKS))
                    activeTasks = p;
            for (Place p: inputPlaces)
                if(!p.equals(activeTasks)) {
                    Transition completeTaskTransition = new Transition(completeTaskTransitionName(p.getName()),
                            Transition.ENAB, Transition.ENAB,false);
                    net.addNode(completeTaskTransition);
                    net.addArc(new Arc(p, completeTaskTransition));
                    net.addArc(new Arc(activeTasks, completeTaskTransition));
                    net.addArc(new Arc(completeTaskTransition, activeTaskDone));
                }

            Transition completeTaskToDone = new Transition(toActiveTaskDoneTransitionName(activeTasks.getName()),
                    Transition.ENAB, Transition.ENAB,false);
            joinTransition = new Transition(dynamicJoinTransitionName(taskName),
                    Transition.ENAB, Transition.ENAB + "+DJ",false);
            net.addNode(completeTaskToDone);
            net.addNode(joinTransition);

            net.addArc(new Arc(activeTaskDone, joinTransition));
            net.addArc(new Arc(activeTaskDone, completeTaskToDone));
            net.addArc(new Arc(activeTasks, completeTaskToDone));
            net.addArc(new Arc(completeTaskToDone, activeTasks));
        }

        List<String> result = new ArrayList<>();
        result.add(joinTransition.getName());
        return result;
    }

    @Override
    protected List<String> dynamicTask(List<String> inputElements, JsonElement currentElement, TBNet net) {
        String dynamicName = currentElement.getAsJsonObject().get(NAME).getAsString();
        JsonArray dynamicTasks = currentElement.getAsJsonObject().get(DYNAMIC_TASKS).getAsJsonArray();

        List<Place> inputPlaces = getPlaces(inputElements, net);
        if(inputPlaces.isEmpty())
            throw new IllegalArgumentException();

        //Place dynamicTaskRunningPlace = new Place(dynamicTaskRunningPlaceName(dynamicName)); // TODO flow_state to avoid side-effect non-determinism
        Place dynamicTaskEndPlace = new Place(dynamicTaskEndPlaceName(dynamicName));
        net.addNode(dynamicTaskEndPlace);
        //net.addNode(dynamicTaskRunningPlace);

        for(JsonElement task: dynamicTasks) {
            String taskName = task.getAsString();
            Transition dynamicTaskStartTransition = new Transition(dynamicTaskStartTransitionName(taskName),
                    Transition.ENAB, Transition.ENAB + "+D",false);
            Transition dynamicTaskEndTransition = new Transition(dynamicTaskEndTransitionName(taskName),
                    Transition.ENAB, Transition.ENAB,false);
            net.addNode(dynamicTaskStartTransition);
            net.addNode(dynamicTaskEndTransition);
            net.addArc(new Arc(dynamicTaskStartTransition, net.getPlace(WorkerGenerator.schedulePlaceName(taskName))));
            //net.addArc(new Arc(dynamicTaskStartTransition, dynamicTaskRunningPlace));
            for(Place p: inputPlaces)
                net.addArc(new Arc(p, dynamicTaskStartTransition));
            net.addArc(new Arc(net.getPlace(WorkerGenerator.completePlaceName(taskName)), dynamicTaskEndTransition));
            //net.addArc(new Arc(dynamicTaskRunningPlace, dynamicTaskEndTransition));
            net.addArc(new Arc(dynamicTaskEndTransition, dynamicTaskEndPlace));

            // TODO time_out place connections
        }

        List<String> result = new ArrayList<>();
        result.add(dynamicTaskEndPlace.getName());
        return result;
    }

    @Override
    protected List<String> decisionTask(List<String> inputElements, JsonElement currentElement, TBNet net) {
        String taskName = currentElement.getAsJsonObject().get(NAME).getAsString();

        List<Place> inputPlaces = getPlaces(inputElements, net);
        List<Transition> inputTransitions = getTransitions(inputElements, net);

        JsonObject decisionCases = currentElement.getAsJsonObject().get(DECISION_CASES).getAsJsonObject();
        List<String> outputElements = new ArrayList<>();
        for(Map.Entry<String, JsonElement> entry: decisionCases.entrySet()) {
            Transition caseTransition = new Transition(caseTransitionName(entry.getKey()),
                    Transition.ENAB, Transition.ENAB + "+C",false);
            net.addNode(caseTransition);
            for(Place p: inputPlaces)
               net.addArc(new Arc(p, caseTransition));
            for(Transition t: inputTransitions) {
                Place p = new Place(toCaseTransitionName(t.getName()));
                net.addNode(p);
                net.addArc(new Arc(p, caseTransition));
            }
            List<String> input = new ArrayList<>();
            input.add(caseTransition.getName());
            outputElements.addAll(createWorkflow(input, entry.getValue(), net));
        }
        Place decisionEndPlace = new Place(decisionEndPlaceName(taskName));
        net.addNode(decisionEndPlace);

        for(Transition t: getTransitions(outputElements, net)) {
            net.addArc(new Arc(t, decisionEndPlace));
        }
        for(Place p: getPlaces(outputElements, net)) {
            Transition toEndPlace = new Transition(toDecisionEndPlaceTransitionName(p.getName()),
                    Transition.ENAB, Transition.ENAB,false);
            net.addNode(toEndPlace);
            net.addArc(new Arc(toEndPlace, decisionEndPlace));
        }

        List<String> result = new ArrayList<>();
        result.add(decisionEndPlace.getName());
        return result;
    }

    @Override
    protected List<String> dynamicForkTask(List<String> inputElements, JsonElement currentElement, TBNet net) {
        List<String> result = new ArrayList<>();
        String taskName = currentElement.getAsJsonObject().get(NAME).getAsString();
        List<Place> inputPlaces = getPlaces(inputElements, net);
        List<Transition> inputTransitions = getTransitions(inputElements, net);

        Place dynamicForkStart = new Place(startDynamicForkPlaceName(taskName));
        Place activeTasks = new Place(dynamicForkActiveTasksPlaceName(taskName));
        net.addNode(dynamicForkStart);
        net.addNode(activeTasks);
        result.add(activeTasks.getName());

        for(Transition t: inputTransitions)
            net.addArc(new Arc(t, dynamicForkStart));
        if(!inputPlaces.isEmpty()) {
            Transition inputPlacesToDynamicFork = new Transition(toDynamicForkTransitionName(taskName),
                    Transition.ENAB, Transition.ENAB + "+DFS",false);
            net.addNode(inputPlacesToDynamicFork);
            net.addArc(new Arc(inputPlacesToDynamicFork, dynamicForkStart));
            for(Place p: inputPlaces)
                net.addArc(new Arc(p, inputPlacesToDynamicFork));
        }

        JsonArray dynamicForkTasks = currentElement.getAsJsonObject().get(DYNAMIC_FORK_TASKS).getAsJsonArray();
        for(JsonElement task: dynamicForkTasks) {
            Place dynamicChoice = new Place(dynamicChoicePlaceName(task.getAsString()));
            dynamicChoice.putTokens(1, "TA");
            net.addNode(dynamicChoice);
            Transition chooseAndContinue = new Transition(chooseAndContinueTransitionName(task.getAsString()),
                    Transition.ENAB, Transition.ENAB,false);
            Transition chooseAndStop = new Transition(chooseAndStopTransitionName(task.getAsString()),
                    Transition.ENAB, Transition.ENAB,false);
            net.addNode(chooseAndContinue);
            net.addNode(chooseAndStop);
            net.addArc(new Arc(dynamicChoice, chooseAndContinue));
            net.addArc(new Arc(dynamicChoice, chooseAndStop));
            net.addArc(new Arc(dynamicForkStart, chooseAndContinue));
            net.addArc(new Arc(chooseAndContinue, dynamicForkStart));
            net.addArc(new Arc(dynamicForkStart, chooseAndStop));

            Place schedulePlace = net.getPlace(WorkerGenerator.schedulePlaceName(task.getAsString()));
            net.addArc(new Arc(chooseAndContinue, schedulePlace));
            net.addArc(new Arc(chooseAndStop, schedulePlace));
            net.addArc(new Arc(chooseAndContinue, activeTasks));
            net.addArc(new Arc(chooseAndStop, activeTasks));

            result.add(WorkerGenerator.completePlaceName(task.getAsString()));
        }

        return result;
    }

    @Override
    protected List<String> httpTask(List<String> inputElements, JsonElement currentElement, TBNet net) {
        String taskName = currentElement.getAsJsonObject().get(NAME).getAsString();

        Place request = new Place(httpReqPlaceName(taskName));
        Place complete = new Place(httpReqCompletePlaceName(taskName));
        Place failed = new Place(httpReqFailedPlaceName(taskName));
        net.addNode(request);
        net.addNode(complete);
        net.addNode(failed);

        Transition toHttpReq = new Transition(toHttpReqTransitionName(taskName),
                Transition.ENAB, Transition.ENAB + "+H",false);
        Transition okStatus = new Transition(okStatusTransitionName(taskName),
                Transition.ENAB, Transition.ENAB + "+" + Transition.INF,true);
        Transition errorStatus = new Transition(errorStatusTransitionName(taskName),
                Transition.ENAB, Transition.ENAB + "+" + Transition.INF,true);
        Transition timeOut = new Transition(httpReqTimeOutTransitionName(taskName),
                Transition.ENAB, Transition.ENAB + "+T",false);

        net.addNode(toHttpReq);
        net.addNode(okStatus);
        net.addNode(errorStatus);
        net.addNode(timeOut);
        net.addArc(new Arc(toHttpReq, request));
        net.addArc(new Arc(request, okStatus));
        net.addArc(new Arc(request, errorStatus));
        net.addArc(new Arc(request, timeOut));
        net.addArc(new Arc(okStatus, complete));
        net.addArc(new Arc(errorStatus, failed));
        net.addArc(new Arc(timeOut, failed));

        for(Place p: getPlaces(inputElements, net))
            net.addArc(new Arc(p, toHttpReq));
        List<Transition> inputTransitions = getTransitions(inputElements, net);
        if(!inputTransitions.isEmpty()) {
            for (Transition t : inputTransitions) {
                Place bridge = new Place(toHttpReqPlaceName(t.getName()));
                net.addNode(bridge);
                net.addArc(new Arc(bridge, toHttpReq));
            }
        }

        JsonArray internalTasks = currentElement.getAsJsonObject().get(TARGET_TASKS).getAsJsonArray();
        if(internalTasks != null) {
            for(JsonElement task: internalTasks) {
                Place canReply = new Place(canReplayPlaceName(task.getAsString()));
                net.addNode(canReply);
                Transition s2p = net.getTransition(WorkerGenerator.s2pTransitionName(task.getAsString()));
                net.addArc(new Arc(s2p, canReply));
                for(NetNode node: net.getPostset(net.getPlace(WorkerGenerator.progressPlaceName(task.getAsString()))))
                    net.addArc(new Arc(canReply, node));
                net.addArc(new Arc(canReply, okStatus));
                net.addArc(new Arc(canReply, errorStatus));
            }
        }

        List<String> result = new ArrayList<>();
        result.add(complete.getName());
        return result;
    }

    private List<Place> getPlaces(List<String> elements, TBNet net) {
        List<Place> result = new ArrayList<>();
        for(String element: elements) {
            Place p = net.getPlace(element);
            if(p != null)
                result.add(p);
        }
        return result;
    }

    private List<Transition> getTransitions(List<String> elements, TBNet net) {
        List<Transition> result = new ArrayList<>();
        for(String element: elements) {
            Transition t = net.getTransition(element);
            if(t != null)
                result.add(t);
        }
        return result;
    }
}
