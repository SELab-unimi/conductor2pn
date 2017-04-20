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
        workerAbortedPlace = new Place(WORKER_ABORTED);
        net.addNode(workerAbortedPlace);
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        basicLifecycle(JsonWorker, net, true);

        final String timeoutWorkerName = JsonWorker.get(WORKER_NAME).getAsString();
        for(NetNode node: net.getPlaces()) {
            Place place = (Place) node;
            if(!place.getName().startsWith(timeoutWorkerName) &&
                    (place.getName().endsWith(SCHEDULE) || place.getName().endsWith(PROGRESS)))
                connectToGlobalTimeout(place, net);
        }
    }

    @Override
    protected void createRetryWorker(JsonElement workerElement, TBNet net) {
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        basicLifecycle(JsonWorker, net, false);

        final String workerName = JsonWorker.get(WORKER_NAME).getAsString();
        final int retryCount = JsonWorker.get(RETRY_COUNT).getAsInt();

        Place timeoutPlace = net.getPlace(timeoutPlaceName(workerName));
        Place retryCountPlace = new Place(retryCountPlaceName(workerName));
        retryCountPlace.putTokens(retryCount, "TA");

        final String retryLogic = JsonWorker.get(RETRY_LOGIC).getAsString();
        final int retryDelaySec = JsonWorker.get(RETRY_DELAY_SECONDS).getAsInt();
        String tmax = Transition.ENAB;
        if(retryLogic.equals(FIXED))
            tmax +=  "+" + retryDelaySec;
        else if(retryLogic.equals(EXPONENTIAL_BACKOFF))
            tmax +=  "+" + retryDelaySec + " * (" + retryCount + " - #(" + retryCountPlace.getName() + "))";

        Transition timeoutAndRetryToProgress = new Transition(tr2pTransitionName(workerName),
                Transition.ENAB, tmax,false);

        net.addNode(retryCountPlace);
        net.addNode(timeoutAndRetryToProgress);

        net.addArc(new Arc(timeoutPlace, timeoutAndRetryToProgress));
        net.addArc(new Arc(retryCountPlace, timeoutAndRetryToProgress));
        net.addArc(new Arc(timeoutAndRetryToProgress, net.getPlace(progressPlaceName(workerName))));
    }

    private void basicLifecycle(JsonObject JsonWorker, TBNet net, boolean timeoutWorkflowWorker) {
        final String workerName = JsonWorker.get(WORKER_NAME).getAsString();
        final Integer timeoutMSec = JsonWorker.get(TIMEOUT_MSEC).getAsInt();

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
            connectToGlobalTimeout(schedule, net);
            connectToGlobalTimeout(progress, net);
        }
    }

    private void connectToGlobalTimeout(Place place, TBNet net) {
        String transitionName = s2aTransitionName(workerNameFromPlace(place));
        if(place.getName().endsWith(PROGRESS))
            transitionName = p2aTransitionName(workerNameFromPlace(place));

        Transition placeToAbort = new Transition(transitionName,
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
