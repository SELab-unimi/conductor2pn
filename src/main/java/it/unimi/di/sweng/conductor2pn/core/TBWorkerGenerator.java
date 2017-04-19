package it.unimi.di.sweng.conductor2pn.core;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.di.sweng.conductor2pn.data.*;

public class TBWorkerGenerator extends WorkerGenerator{

    private boolean globalTimeout = false;
    private Place globalTimeoutPlace = null;
    private Place workerAbortedPlace = null;

    @Override
    protected void createAlertOnlyWorker(JsonElement workerElement, TBNet net) {
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        basicLifecycle(JsonWorker, net, false);
    }

    @Override
    protected void createWorkflowTimeoutWorker(JsonElement workerElement, TBNet net) {
        globalTimeout = true;
        workerAbortedPlace = new Place("worker_aborted");
        net.addNode(workerAbortedPlace);
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        basicLifecycle(JsonWorker, net, true);

        final String timeoutWorkerName = JsonWorker.get(ConductorToPn.WORKER_NAME).getAsString();
        for(NetNode node: net.getPlaces()) {
            Place place = (Place) node;
            if(!place.getName().startsWith(timeoutWorkerName) &&
                    (place.getName().endsWith("_schedule") || place.getName().endsWith("_progress")))
                connectGlobalTimeout(place, net);
        }
    }

    @Override
    protected void createRetryWorker(JsonElement workerElement, TBNet net) {
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        basicLifecycle(JsonWorker, net, false);
    }

    private void basicLifecycle(JsonObject JsonWorker, TBNet net, boolean timeoutWorkflowWorker) {
        final String workerName = JsonWorker.get(ConductorToPn.WORKER_NAME).getAsString();
        final Integer timeoutMSec = JsonWorker.get(ConductorToPn.TIMEOUT_MSEC).getAsInt();

        Place schedule = new Place(schedulePlaceName(workerName));
        Place progress = new Place(progressPlaceName(workerName));
        Place complete = new Place(completePlaceName(workerName));
        Place timeout = new Place(timeoutPlaceName(workerName));

        if(globalTimeout)
            globalTimeoutPlace = timeout;

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

        if(globalTimeout && !timeoutWorkflowWorker) {
            connectGlobalTimeout(schedule, net);
            connectGlobalTimeout(progress, net);
        }
    }

    private void connectGlobalTimeout(Place place, TBNet net) {
        Transition placeToAbort = new Transition(s2aTransitionName(workerNameFromPlace(place)),
                Transition.ENAB, Transition.ENAB + "+Y",false);
        net.addNode(placeToAbort);
        net.addArc(new Arc(globalTimeoutPlace, placeToAbort));
        net.addArc(new Arc(placeToAbort, globalTimeoutPlace));
        net.addArc(new Arc(place, placeToAbort));
        net.addArc(new Arc(placeToAbort, workerAbortedPlace));
    }

    private String workerNameFromPlace(Place place) {
        return place.getName().substring(0, place.getName().lastIndexOf("_"));
    }
}
