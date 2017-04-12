package it.unimi.di.sweng.conductor2pn.core;

import org.junit.Test;
import static org.junit.Assert.*;

import it.unimi.di.sweng.conductor2pn.core.ConductorToPn.ConductorToPnBuilder;

public class ConductorToPnTest {

    @Test
    public void ConductorToPnBuilderTest() {
        ConductorToPn conductor2PnEngine = new ConductorToPnBuilder()
                .setWorkflowPath("src/main/resources/kitchensink.json")
                .build();
        assertNotNull(conductor2PnEngine);
    }
}
