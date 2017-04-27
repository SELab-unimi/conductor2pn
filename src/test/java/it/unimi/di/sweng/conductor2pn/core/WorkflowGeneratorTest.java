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
}