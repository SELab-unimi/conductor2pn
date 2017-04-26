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
}