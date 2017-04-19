package it.unimi.di.sweng.conductor2pn.core;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.di.sweng.conductor2pn.data.Arc;
import it.unimi.di.sweng.conductor2pn.data.Place;
import it.unimi.di.sweng.conductor2pn.data.TBNet;
import it.unimi.di.sweng.conductor2pn.data.Transition;

public class TBWorkerGenerator extends WorkerGenerator{

    private boolean globalTimeout = false;

    @Override
    protected void createAlertOnlyWorker(JsonElement workerElement, TBNet net) {
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        basicLifecycle(JsonWorker, net);
    }

    @Override
    protected void createWorkflowTimeoutWorker(JsonElement workerElement, TBNet net) {
        globalTimeout = true;
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        basicLifecycle(JsonWorker, net);
    }

    @Override
    protected void createRetryWorker(JsonElement workerElement, TBNet net) {
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        basicLifecycle(JsonWorker, net);
    }

    private void basicLifecycle(JsonObject JsonWorker, TBNet net) {
        final String workerName = JsonWorker.get(ConductorToPn.WORKER_NAME).getAsString();
        final Integer timeoutMSec = JsonWorker.get(ConductorToPn.TIMEOUT_MSEC).getAsInt();

        Place schedule = new Place(schedulePlaceName(workerName));
        Place progress = new Place(progressPlaceName(workerName));
        Place complete = new Place(completePlaceName(workerName));
        Place timeout = new Place(timeoutPlaceName(workerName));

        Transition scheduleToProgress = new Transition(s2pTransitionName(workerName),
                Transition.ENAB,Transition.ENAB + "+X",false);
        Transition progressToComplete = new Transition(p2cTransitionName(workerName),
                Transition.ENAB, Transition.INF,true);
        Transition progressToTimeout = new Transition(p2tTransitionName(workerName),
                Transition.ENAB, Transition.ENAB + "+" + timeoutMSec,false);

        net.addNode(schedule);
        net.addNode(progress);
        net.addNode(complete);
        net.addNode(timeout);
        net.addNode(scheduleToProgress);
        net.addNode(progressToComplete);
        net.addNode(progressToTimeout);

        net.addArc(new Arc(schedule, scheduleToProgress));
        net.addArc(new Arc(scheduleToProgress, progress));
        net.addArc(new Arc(progress, progressToComplete));
        net.addArc(new Arc(progressToComplete, complete));
        net.addArc(new Arc(progress, progressToTimeout));
        net.addArc(new Arc(progressToTimeout, timeout));

        if(globalTimeout) {

        }
    }
}
