package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonElement;
import it.unimi.di.sweng.conductor2pn.data.*;

import java.util.ArrayList;
import java.util.List;

public class TBWorkflowGenerator extends WorkflowGenerator {

    @Override
    protected List<String> simpleTask(List<String> inputElements, JsonElement workerElement, TBNet net) {
        String workerName = workerElement.getAsJsonObject().get(NAME).getAsString();
        Place schedulePlace = net.getPlace(WorkerGenerator.schedulePlaceName(workerName));

        if(!inputElements.isEmpty()) {
            List<Place> inputPlaces = new ArrayList<>();
            List<Transition> inputTransitions = new ArrayList<>();
            for(String element: inputElements) {
                Place p = net.getPlace(element);
                Transition t = net.getTransition(element);
                if (p != null)
                    inputPlaces.add(p);
                else if (t != null)
                    inputTransitions.add(t);
            }

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

        List<Place> inputPlaces = new ArrayList<>();
        for(String element: inputElements) {
            Place p = net.getPlace(element);
            if(p != null)
                inputPlaces.add(p);
        }

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
    protected List<String> joinTask(List<String> inputElements, JsonElement currentElement, TBNet net) {
        String joinName = currentElement.getAsJsonObject().get(NAME).getAsString();
        List<Place> inputPlaces = new ArrayList<>();
        for(String element: inputElements) {
            Place p = net.getPlace(element);
            if(p != null)
                inputPlaces.add(p);
        }

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
}
