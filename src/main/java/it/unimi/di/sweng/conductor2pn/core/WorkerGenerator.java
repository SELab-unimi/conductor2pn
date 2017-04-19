package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.di.sweng.conductor2pn.data.TBNet;

public abstract class WorkerGenerator {

    public void createWorker(JsonElement workerElement, TBNet net) {
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        final String policy = JsonWorker.get(ConductorToPn.TIMEOUT_POLICY).getAsString();
        switch(policy){
            case "RETRY":
                createRetryWorker(workerElement, net);
                break;
            case "ALERT_ONLY":
                createAlertOnlyWorker(workerElement, net);
                break;
            case "TIME_OUT_WF":
                createWorkflowTimeoutWorker(workerElement, net);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    protected abstract void createAlertOnlyWorker(JsonElement element, TBNet net);
    protected abstract void createWorkflowTimeoutWorker(JsonElement element, TBNet net);
    protected abstract void createRetryWorker(JsonElement element, TBNet net);

    protected String schedulePlaceName(String workerName) {
        return workerName + "_schedule";
    }

    protected String progressPlaceName(String workerName) {
        return workerName + "_progress";
    }

    protected String completePlaceName(String workerName) {
        return workerName + "_complete";
    }

    protected String timeoutPlaceName(String workerName) {
        return workerName + "_timeout";
    }

    protected String s2pTransitionName(String workerName) {
        return workerName + "_s2p"; // schedule to progress
    }
    protected String p2cTransitionName(String workerName) {
        return workerName + "_p2c"; // progress to complete
    }

    protected String p2tTransitionName(String workerName) {
        return workerName + "_p2t"; // progress to timeout
    }

}
