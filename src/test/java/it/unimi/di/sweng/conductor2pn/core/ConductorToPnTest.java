package it.unimi.di.sweng.conductor2pn.core;

import org.junit.Test;
import static org.junit.Assert.*;

import it.unimi.di.sweng.conductor2pn.core.ConductorToPn.ConductorToPnBuilder;

public class ConductorToPnTest {

    @Test public void testSomeLibraryMethod() {
        ConductorToPn conductor2PnEngine = new ConductorToPnBuilder().build();
        assertNull(conductor2PnEngine);
    }
}
