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
        String joinName = currentElement.getAsJsonObject().get(NAME).getAsString();

        List<Place> inputPlaces = getPlaces(inputElements, net);
        if(inputPlaces.isEmpty())
            throw new IllegalArgumentException();

        Transition joinTransition = new Transition(joinTransitionName(joinName),
                Transition.ENAB, Transition.ENAB + "+J",false);
        net.addNode(joinTransition);
        for(Place inputPlace: inputPlaces) {
            net.addArc(new Arc(inputPlace, joinTransition));
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
