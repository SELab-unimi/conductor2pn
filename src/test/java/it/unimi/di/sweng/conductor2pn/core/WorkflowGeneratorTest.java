package it.unimi.di.sweng.conductor2pn.core;

import it.unimi.di.sweng.conductor2pn.data.Place;
import it.unimi.di.sweng.conductor2pn.data.TBNet;
import it.unimi.di.sweng.conductor2pn.data.Transition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;


public class WorkflowGeneratorTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);

    @Test
    public void simpleTaskWorkflowTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/workers_mix.json")
                .setWorkflowPath("src/main/resources/workflow_simple.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .setWorkflowGenerator(new TBWorkflowGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(9, model.getPlaces().size());
        assertEquals(8, model.getTransitions().size());

        assertEquals(1, model.getPlace("task_1_schedule").getTokens().size());
        assertEquals(0, model.getPlace("task_2_schedule").getTokens().size());
        assertNotNull(model.getTransition("start_task_2"));
    }

    @Test
    public void eventTaskWorkflowTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/workers_mix.json")
                .setWorkflowPath("src/main/resources/workflow_event.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .setWorkflowGenerator(new TBWorkflowGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(11, model.getPlaces().size());
        assertEquals(9, model.getTransitions().size());

        assertNotNull(model.getTransition("event_0_generation"));
        assertNotNull(model.getTransition("start_task_2"));
        assertNotNull(model.getPlace("event_0_generated"));
        assertNotNull(model.getPlace("event_0_to_be_handled"));
    }

    @Test
    public void forkJoinTaskWorkflowTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/workers_mix_2.json")
                .setWorkflowPath("src/main/resources/workflow_fork_join.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .setWorkflowGenerator(new TBWorkflowGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(17, model.getPlaces().size());
        assertEquals(16, model.getTransitions().size());

        assertNotNull(model.getTransition("fork_join_fork"));
        assertNotNull(model.getTransition("join_join"));
    }

    @Test
    public void dynamicTaskWorkflowTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/workers_mix.json")
                .setWorkflowPath("src/main/resources/workflow_dynamic.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .setWorkflowGenerator(new TBWorkflowGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(10, model.getPlaces().size());
        assertEquals(11, model.getTransitions().size());

        assertNotNull(model.getTransition("dynamic_start_task_1"));
        assertNotNull(model.getTransition("dynamic_start_task_2"));
        assertNotNull(model.getTransition("dynamic_end_task_1"));
        assertNotNull(model.getTransition("dynamic_end_task_2"));
        assertNotNull(model.getPlace("dynamic_task_dynamicTask_ended"));
    }

    @Test
    public void decisionTaskWorkflowTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/workers_mix_2.json")
                .setWorkflowPath("src/main/resources/workflow_decision.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .setWorkflowGenerator(new TBWorkflowGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(18, model.getPlaces().size());
        assertEquals(18, model.getTransitions().size());

        assertNotNull(model.getTransition("Show_case"));
        assertNotNull(model.getTransition("Movie_case"));
        assertNotNull(model.getTransition("task_2_complete_to_decision_end"));
        assertNotNull(model.getTransition("task_3_complete_to_decision_end"));
        assertNotNull(model.getTransition("start_task_2"));
        assertNotNull(model.getPlace("decide_task_decision_end"));
    }

    @Test
    public void dynamicForkTaskWorkflowTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/workers_mix_2.json")
                .setWorkflowPath("src/main/resources/workflow_dynamic_fork_join.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .setWorkflowGenerator(new TBWorkflowGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(23, model.getPlaces().size());
        assertEquals(25, model.getTransitions().size());

        assertNotNull(model.getPlace("dynamic_fork_start_dynamic_fanout"));
        assertNotNull(model.getPlace("task_1_dynamic_choice"));
        assertNotNull(model.getPlace("task_2_dynamic_choice"));
        assertNotNull(model.getPlace("task_3_dynamic_choice"));
        assertNotNull(model.getPlace("dynamic_fanout_active_tasks"));
        assertNotNull(model.getPlace("dynamic_join_active_task_done"));

        assertNotNull(model.getTransition("dynamic_fanout_fork"));
        assertNotNull(model.getTransition("task_1_choose_continue"));
        assertNotNull(model.getTransition("task_1_choose_stop"));
        assertNotNull(model.getTransition("task_2_choose_continue"));
        assertNotNull(model.getTransition("task_2_choose_stop"));
        assertNotNull(model.getTransition("task_3_choose_continue"));
        assertNotNull(model.getTransition("task_3_choose_stop"));
        assertNotNull(model.getTransition("dynamic_fanout_active_tasks_to_active_task_done"));
        assertNotNull(model.getTransition("dynamic_join_dynamic_join"));
    }
}