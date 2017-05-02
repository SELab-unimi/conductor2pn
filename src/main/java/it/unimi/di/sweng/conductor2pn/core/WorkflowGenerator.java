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
    protected static final String DYNAMIC_TASKS = "dynamicTasks";
    protected static final String DYNAMIC_FORK_TASKS = "dynamicForkTasks";
    protected static final String DECISION_CASES = "decisionCases";
    protected static final String DYNAMIC = "dynamic";

    // PN elements naming
    protected static final String START_TASK = "start_";
    protected static final String START_DYNAMIC_TASK = "dynamic_start_";
    protected static final String END_DYNAMIC_TASK = "dynamic_end_";
    protected static final String DYNAMIC_TASK_ENDED = "_dynamicTask_ended";
    protected static final String DYNAMIC_TASK_RUNNING = "_dynamicTask_running";
    protected static final String START_EVENT = "_generation";
    protected static final String EVENT_GENERATED = "_generated";
    protected static final String EVENT_TO_BE_HANDLED = "_to_be_handled";
    protected static final String FORK = "_fork";
    protected static final String JOIN = "_join";
    protected static final String CASE = "_case";
    protected static final String TO_CASE = "_to_case";
    protected static final String DECISION_END = "_decision_end";
    protected static final String TO_DECISION_END = "_to_decision_end";
    protected static final String START_DYNAMIC_FORK = "dynamic_fork_start_";
    protected static final String TO_DYNAMIC_FORK = "_fork";
    protected static final String DYNAMIC_CHOICE = "_dynamic_choice";
    protected static final String CHOOSE_CONTINUE = "_choose_continue";
    protected static final String CHOOSE_STOP = "_choose_stop";
    protected static final String ACTIVE_TASKS = "_active_tasks";
    protected static final String ACTIVE_TASK_DONE = "_active_task_done";
    protected static final String DYNAMIC_FORK_END = "_dynamic_fork_end";
    protected static final String TO_ACTIVE_TASK_DONE = "_to_active_task_done";
    protected static final String DYNAMIC_JOIN = "_dynamic_join";
    protected static final String DYNAMIC_TASK_COMPLETE = "_dynamic_task_complete";

    public void createWorkflow(JsonElement workflowElement, TBNet net) {
        createWorkflow(new ArrayList<>(), workflowElement.getAsJsonObject().get(TASKS), net);
    }

    protected List<String> createWorkflow(List<String> inputElements, JsonElement workflowElement, TBNet net) {
        List<String> outputTasks = inputElements;
        JsonArray JsonWorkflow = workflowElement.getAsJsonArray();
        for(JsonElement currentElement: JsonWorkflow) {
            final String type = currentElement.getAsJsonObject().get(TYPE).getAsString();
            switch(type){
                case "SIMPLE":
                    outputTasks = simpleTask(outputTasks, currentElement, net);
                    break;
                case "EVENT":
                    outputTasks = eventTask(outputTasks, currentElement, net);
                    break;
                case "FORK_JOIN":
                    outputTasks = forkTask(outputTasks, currentElement, net);
                    break;
                case "JOIN":
                    outputTasks = joinTask(outputTasks, currentElement, net);
                    break;
                case "DYNAMIC":
                    outputTasks = dynamicTask(outputTasks, currentElement, net);
                    break;
                case "DECISION":
                    outputTasks = decisionTask(outputTasks, currentElement, net);
                    break;
                case "FORK_JOIN_DYNAMIC":
                    outputTasks = dynamicForkTask(outputTasks, currentElement, net);
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

    protected abstract List<String> dynamicTask(List<String> inputElements, JsonElement workflowElement, TBNet net);

    protected abstract List<String> decisionTask(List<String> inputElements, JsonElement workflowElement, TBNet net);

    protected abstract List<String> dynamicForkTask(List<String> inputElements, JsonElement workflowElement, TBNet net);

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

    protected static String dynamicTaskStartTransitionName(String workerName) {
        return START_DYNAMIC_TASK + workerName;
    }

    protected static String dynamicTaskEndTransitionName(String workerName) {
        return END_DYNAMIC_TASK + workerName;
    }

    protected static String dynamicTaskEndPlaceName(String workerName) {
        return workerName + DYNAMIC_TASK_ENDED;
    }

    protected static String dynamicTaskRunningPlaceName(String workerName) {
        return workerName + DYNAMIC_TASK_RUNNING;
    }

    protected static String caseTransitionName(String workerName) {
        return workerName + CASE;
    }

    protected static String toCaseTransitionName(String workerName) {
        return workerName + TO_CASE;
    }

    protected static String decisionEndPlaceName(String workerName) {
        return workerName + DECISION_END;
    }

    protected static String toDecisionEndPlaceTransitionName(String workerName) {
        return workerName + TO_DECISION_END;
    }

    protected static String startDynamicForkPlaceName(String workerName) {
        return START_DYNAMIC_FORK + workerName;
    }

    protected static String toDynamicForkTransitionName(String workerName) {
        return workerName + TO_DYNAMIC_FORK;
    }

    protected static String dynamicChoicePlaceName(String workerName) {
        return workerName + DYNAMIC_CHOICE;
    }

    protected static String chooseAndContinueTransitionName(String workerName) {
        return workerName + CHOOSE_CONTINUE;
    }

    protected static String chooseAndStopTransitionName(String workerName) {
        return workerName + CHOOSE_STOP;
    }

    protected static String dynamicForkActiveTasksPlaceName(String workerName) {
        return workerName + ACTIVE_TASKS;
    }

    protected static String dynamicForkActiveTaskDonePlaceName(String workerName) {
        return workerName + ACTIVE_TASK_DONE;
    }

    protected static String dynamicForkEndPlaceName(String workerName) {
        return workerName + DYNAMIC_FORK_END;
    }

    protected static String toActiveTaskDoneTransitionName(String workerName) {
        return workerName + TO_ACTIVE_TASK_DONE;
    }

    protected static String dynamicJoinTransitionName(String workerName) {
        return workerName + DYNAMIC_JOIN;
    }

    protected static String completeTaskTransitionName(String workerName) {
        return workerName + DYNAMIC_TASK_COMPLETE;
    }
}
