package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.di.sweng.conductor2pn.data.TBNet;

public abstract class WorkflowGenerator {

    // JSON elements
    protected static final String TYPE = "type";
    protected static final String TASKS = "tasks";
    protected static final String NAME = "name";

    // PN elements naming
    protected static final String START_TASK = "start_";

    public void createWorkflow(JsonElement workflowElement, TBNet net) {
        createWorkflow(null, workflowElement.getAsJsonObject().get(TASKS), net);
    }

    protected String createWorkflow(String inputTask, JsonElement workflowElement, TBNet net) {
        String outputTask = null;
        JsonArray JsonWorkflow = workflowElement.getAsJsonArray();
        for(JsonElement currentElement: JsonWorkflow) {
            final String type = currentElement.getAsJsonObject().get(TYPE).getAsString();
            switch(type){
                case "SIMPLE":
                    outputTask = simpleTask(inputTask, currentElement, net);
                    break;
                case "EVENT":
                    //createAlertOnlyWorker(workerElement, net);
                    break;
                case "DYNAMIC":
                    //createWorkflowTimeoutWorker(workerElement, net);
                    break;
                case "DECISION":
                    //createWorkflowTimeoutWorker(workerElement, net);
                    break;
                case "FORK_JOIN":
                    //createWorkflowTimeoutWorker(workerElement, net);
                    break;
                case "FORK_JOIN_DYNAMIC":
                    //createWorkflowTimeoutWorker(workerElement, net);
                    break;
                case "HTTP":
                    //createWorkflowTimeoutWorker(workerElement, net);
                    break;
                case "WAIT":
                    //createWorkflowTimeoutWorker(workerElement, net);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            inputTask = outputTask;
        }
        return outputTask;
    }

    protected abstract String simpleTask(String inputTask, JsonElement workerElement, TBNet net);

    protected static String startTaskTransitionName(String workerName) {
        return START_TASK + workerName;
    }
}
