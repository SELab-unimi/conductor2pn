package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.di.sweng.conductor2pn.data.TBNet;

public abstract class WorkerGenerator {

    // JSON elements
    protected static final String TIMEOUT_POLICY = "timeoutPolicy";
    protected static final String RETRY_LOGIC = "retryLogic";
    protected static final String RETRY_COUNT = "retryCount";
    protected static final String FIXED = "FIXED";
    protected static final String EXPONENTIAL_BACKOFF = "EXPONENTIAL_BACKOFF";
    protected static final String RETRY_DELAY_SECONDS = "retryDelaySeconds";
    protected static final String TIMEOUT_MSEC = "timeoutSeconds";
    protected static final String WORKER_NAME = "name";

    // PN elements naming
    protected static final String WORKER_ABORTED = "worker_aborted";
    protected static final String SCHEDULE = "_schedule";
    protected static final String PROGRESS = "_progress";
    protected static final String COMPLETE = "_complete";
    protected static final String TIMEOUT = "_timeout";
    protected static final String RETRYCOUNT = "_retrycount";
    protected static final String SCHEDULE_TO_PROGRESS = "_s2p";
    protected static final String PROGRESS_TO_COMPLETE = "_p2c";
    protected static final String PROGRESS_TO_TIMEOUT = "_p2t";
    protected static final String PROGRESS_TO_ABORT = "_p2a";
    protected static final String SCHEDULE_TO_ABORT = "_s2a";
    protected static final String TIMEOUT_RETRY_TO_PROGRESS = "_tr2p";

    public void createWorker(JsonElement workerElement, TBNet net) {
        JsonObject JsonWorker = workerElement.getAsJsonObject();
        final String policy = JsonWorker.get(TIMEOUT_POLICY).getAsString();
        switch(policy){
            case "RETRY":
                retryWorker(workerElement, net);
                break;
            case "ALERT_ONLY":
                alertOnlyWorker(workerElement, net);
                break;
            case "TIME_OUT_WF":
                workflowTimeoutWorker(workerElement, net);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    protected abstract void alertOnlyWorker(JsonElement element, TBNet net);
    protected abstract void workflowTimeoutWorker(JsonElement element, TBNet net);
    protected abstract void retryWorker(JsonElement element, TBNet net);

    protected static String schedulePlaceName(String workerName) {
        return workerName + SCHEDULE;
    }

    protected static String progressPlaceName(String workerName) {
        return workerName + PROGRESS;
    }

    protected static String completePlaceName(String workerName) {
        return workerName + COMPLETE;
    }

    protected static String timeoutPlaceName(String workerName) {
        return workerName + TIMEOUT;
    }

    protected static String retryCountPlaceName(String workerName) {
        return workerName + RETRYCOUNT;
    }

    protected static String s2pTransitionName(String workerName) {
        return workerName + SCHEDULE_TO_PROGRESS;
    }

    protected static String p2cTransitionName(String workerName) {
        return workerName + PROGRESS_TO_COMPLETE;
    }

    protected static String p2tTransitionName(String workerName) {
        return workerName + PROGRESS_TO_TIMEOUT;
    }

    protected static String p2aTransitionName(String workerName) {
        return workerName + PROGRESS_TO_ABORT;
    }

    protected static String s2aTransitionName(String workerName) {
        return workerName + SCHEDULE_TO_ABORT;
    }

    protected static String tr2pTransitionName(String workerName) {
        return workerName + TIMEOUT_RETRY_TO_PROGRESS;
    }
}
