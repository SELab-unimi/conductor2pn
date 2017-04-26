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
    protected static final String START_EVENT = "_generation";
    protected static final String EVENT_GENERATED = "_generated";
    protected static final String EVENT_TO_BE_HANDLED = "_to_be_handled";

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
                    outputTask = eventTask(inputTask, currentElement, net);
                    break;
                case "FORK_JOIN":
                    outputTask = forkTask(inputTask, currentElement, net);
                    break;
                case "DYNAMIC":
                    //createWorkflowTimeoutWorker(workerElement, net);
                    break;
                case "DECISION":
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

    protected abstract String eventTask(String inputTask, JsonElement workerElement, TBNet net);

    protected String forkTask(String inputTask, JsonElement workerElement, TBNet net) {
        return null;
    }

    protected static String startTaskTransitionName(String workerName) {
        return START_TASK + workerName;
    }

    protected static String startEventTransitionName(String workerName) {
        return workerName + START_EVENT;
    }

    protected static String eventPlaceName(String workerName) {
        return workerName + EVENT_GENERATED;
    }

    protected static String eventToBeHandledName(String workerName) {
        return workerName + EVENT_TO_BE_HANDLED;
    }

}
