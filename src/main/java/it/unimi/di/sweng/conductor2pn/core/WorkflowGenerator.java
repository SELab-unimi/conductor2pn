package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.unimi.di.sweng.conductor2pn.data.TBNet;

import java.util.ArrayList;
import java.util.List;

public abstract class WorkflowGenerator {

    // JSON elements
    protected static final String TYPE = "type";
    protected static final String TASKS = "tasks";
    protected static final String NAME = "name";
    protected static final String FORK_TASKS = "forkTasks";

    // PN elements naming
    protected static final String START_TASK = "start_";
    protected static final String START_EVENT = "_generation";
    protected static final String EVENT_GENERATED = "_generated";
    protected static final String EVENT_TO_BE_HANDLED = "_to_be_handled";
    protected static final String FORK = "_fork";
    protected static final String JOIN = "_join";

    public void createWorkflow(JsonElement workflowElement, TBNet net) {
        createWorkflow(new ArrayList<>(), workflowElement.getAsJsonObject().get(TASKS), net);
    }

    protected List<String> createWorkflow(List<String> inputElements, JsonElement workflowElement, TBNet net) {
        List<String> outputTasks = null;
        JsonArray JsonWorkflow = workflowElement.getAsJsonArray();
        for(JsonElement currentElement: JsonWorkflow) {
            final String type = currentElement.getAsJsonObject().get(TYPE).getAsString();
            switch(type){
                case "SIMPLE":
                    outputTasks = simpleTask(inputElements, currentElement, net);
                    break;
                case "EVENT":
                    outputTasks = eventTask(inputElements, currentElement, net);
                    break;
                case "FORK_JOIN":
                    outputTasks = forkTask(inputElements, currentElement, net);
                    break;
                case "JOIN":
                    outputTasks = joinTask(outputTasks, currentElement, net);
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
            inputElements = outputTasks;
        }
        return outputTasks;
    }

    protected abstract List<String> simpleTask(List<String> inputElements, JsonElement workerElement, TBNet net);

    protected abstract List<String> eventTask(List<String> inputElements, JsonElement workerElement, TBNet net);

    protected abstract List<String> joinTask(List<String> outputTasks, JsonElement workerElement, TBNet net);

    protected abstract List<String> forkTask(List<String> inputElements, JsonElement workflowElement, TBNet net);

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

    protected static String forkTransitionName(String workerName) {
        return workerName + FORK;
    }

    protected static String joinTransitionName(String workerName) {
        return workerName + JOIN;
    }

}
