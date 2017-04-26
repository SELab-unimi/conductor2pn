package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonElement;
import it.unimi.di.sweng.conductor2pn.data.Arc;
import it.unimi.di.sweng.conductor2pn.data.Place;
import it.unimi.di.sweng.conductor2pn.data.TBNet;
import it.unimi.di.sweng.conductor2pn.data.Transition;

public class TBWorkflowGenerator extends WorkflowGenerator {

    @Override
    protected String simpleTask(String inputTask, JsonElement workerElement, TBNet net) {
        String workerName = workerElement.getAsJsonObject().get(NAME).getAsString();
        Place schedulePlace = net.getPlace(WorkerGenerator.schedulePlaceName(workerName));

        if(inputTask != null) {
            Place inputPlace = net.getPlace(inputTask);
            Transition startTask = new Transition(startTaskTransitionName(workerName),
                    Transition.ENAB, Transition.ENAB + "+Z",false);
            net.addNode(startTask);
            net.addArc(new Arc(inputPlace, startTask));
            net.addArc(new Arc(startTask, schedulePlace));
        }
        else {
            schedulePlace.putTokens(1, "T0");
        }

        return WorkerGenerator.completePlaceName(workerName);
    }
}
