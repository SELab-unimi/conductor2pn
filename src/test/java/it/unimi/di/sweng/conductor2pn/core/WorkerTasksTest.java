package it.unimi.di.sweng.conductor2pn.core;

import it.unimi.di.sweng.conductor2pn.data.Place;
import it.unimi.di.sweng.conductor2pn.data.TBNet;
import it.unimi.di.sweng.conductor2pn.data.Transition;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import it.unimi.di.sweng.conductor2pn.core.ConductorToPn.ConductorToPnBuilder;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.Timeout;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class WorkerTasksTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void ConductorToPnBuilderTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPnBuilder()
                .setWorkflowPath("src/main/resources/kitchensink.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .build();
        assertNull(conductor2PnEngine);
    }

    @Test@Ignore
    public void outputTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/worker_timeout_wf.json")
                .setWorkflowPath("src/main/resources/kitchensink.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .build();
        try {
            conductor2PnEngine.createOutputModel(new OutputStreamWriter(System.out));
            assertTrue(systemOutRule.getLog().contains("TO DO"));
        } catch (IOException e) {
            fail("Exception was thrown.");
        }
    }

    @Test
    public void workerAlertOnlyTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/worker_alert_only.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(4, model.getPlaces().size());
        assertEquals(3, model.getTransitions().size());
        assertEquals(2, model.getStrongTransitions().size());
        assertEquals(1, model.getWeakTransitions().size());

        Transition t = model.getTransition("encode_task_p2t");
        assertNotNull(t);
        assertEquals("enab+1200", t.getMaxTime());
    }

    @Test
    public void workerTimeoutWorkflowTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/worker_timeout_wf.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(9, model.getPlaces().size());
        assertEquals(8, model.getTransitions().size());
    }

    @Test
    public void workerRetryFixedTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/worker_retry_fixed.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(5, model.getPlaces().size());
        assertEquals(4, model.getTransitions().size());

        Place retryCountPlace = model.getPlace("encode_task_retrycount");
        assertNotNull(retryCountPlace);
        assertEquals(3, retryCountPlace.getTokens().size());

        Transition tr2p = model.getTransition("encode_task_tr2p");
        assertNotNull(tr2p);
        assertEquals("enab+600", tr2p.getMaxTime());
    }

    @Test
    public void workerRetryExponentialBackoffTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPnBuilder()
                .setWorkerTasksPath("src/main/resources/worker_retry_exponential_backoff.json")
                .setWorkerGenerator(new TBWorkerGenerator())
                .build();

        assertNotNull(conductor2PnEngine);
        TBNet model = conductor2PnEngine.getModel();

        assertEquals(5, model.getPlaces().size());
        assertEquals(4, model.getTransitions().size());

        Place retryCountPlace = model.getPlace("encode_task_retrycount");
        assertNotNull(retryCountPlace);
        assertEquals(3, retryCountPlace.getTokens().size());

        Transition tr2p = model.getTransition("encode_task_tr2p");
        assertNotNull(tr2p);
        assertEquals("enab+600 * (3 - #(encode_task_retrycount))", tr2p.getMaxTime());
    }
}
