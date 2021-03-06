package it.unimi.di.sweng.conductor2pn.core;

import it.unimi.di.sweng.conductor2pn.data.TBNet;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.Timeout;

import java.io.IOException;
import java.io.OutputStreamWriter;

import static org.junit.Assert.*;


public class WorkflowGeneratorTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();


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

    @Test
    public void httpTaskWorkflowTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/workers_mix.json")
                .setWorkflowPath("src/main/resources/workflow_http.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .setWorkflowGenerator(new TBWorkflowGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(13, model.getPlaces().size());
        assertEquals(11, model.getTransitions().size());

        assertNotNull(model.getPlace("search_http_req"));
        assertNotNull(model.getPlace("search_http_req_complete"));
        assertNotNull(model.getPlace("search_http_req_failed"));

        assertNotNull(model.getTransition("search_to_http_req"));
        assertNotNull(model.getTransition("search_ok_status"));
        assertNotNull(model.getTransition("search_error_status"));
        assertNotNull(model.getTransition("search_http_req_timeout"));
    }

    @Test
    public void waitTaskWorkflowTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/workers_mix.json")
                .setWorkflowPath("src/main/resources/workflow_wait.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .setWorkflowGenerator(new TBWorkflowGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(13, model.getPlaces().size());
        assertEquals(13, model.getTransitions().size());

        assertNotNull(model.getPlace("wait_task_wait_in_progress"));
        assertNotNull(model.getPlace("wait_task_wait_complete"));
        assertNotNull(model.getPlace("wait_task_wait_failed"));
        assertNotNull(model.getPlace("wait_task_external_event_generated"));

        assertNotNull(model.getTransition("to_wait_task"));
        assertNotNull(model.getTransition("wait_task_external_event"));
        assertNotNull(model.getTransition("wait_task_to_wait_complete"));
        assertNotNull(model.getTransition("wait_task_to_wait_failed"));
        assertNotNull(model.getTransition("wait_task_to_wait_timeout"));
        assertNotNull(model.getTransition("start_task_2"));
    }

    @Test@Ignore
    public void outputTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/workers_mix.json")
                .setWorkflowPath("src/main/resources/workflow_wait.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .setWorkflowGenerator(new TBWorkflowGenerator())
                .build();
        try {
            conductor2PnEngine.createOutputModel(new OutputStreamWriter(System.out));
            assertTrue(systemOutRule.getLog().contains("pnml"));
        } catch (IOException e) {
            fail("Exception was thrown.");
        }
    }
}