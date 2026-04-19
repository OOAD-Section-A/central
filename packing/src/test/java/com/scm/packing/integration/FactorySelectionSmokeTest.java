package com.scm.packing.integration;

import com.scm.packing.integration.database.DatabaseLayerFactory;
import com.scm.packing.integration.database.FlatFileDatabaseAdapter;
import com.scm.packing.integration.database.IDatabaseLayer;
import com.scm.packing.integration.database.SCMDatabaseAdapter;
import com.scm.packing.integration.exceptions.ExceptionDispatcherFactory;
import com.scm.packing.integration.exceptions.FallbackConsoleLogger;
import com.scm.packing.integration.exceptions.IExceptionDispatcher;
import com.scm.packing.integration.exceptions.SCMExceptionAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FactorySelectionSmokeTest {

    @AfterEach
    void clearForcedModes() {
        System.clearProperty("packing.integration.database.mode");
        System.clearProperty("packing.integration.exceptions.mode");
    }

    @Test
    void databaseFactoryShouldReturnFallbackWhenForcedFlat() {
        System.setProperty("packing.integration.database.mode", "flat");
        IDatabaseLayer layer = DatabaseLayerFactory.create();
        assertInstanceOf(FlatFileDatabaseAdapter.class, layer);
    }

    @Test
    void databaseFactoryShouldReturnScmAdapterWhenForcedScm() {
        System.setProperty("packing.integration.database.mode", "scm");
        IDatabaseLayer layer = DatabaseLayerFactory.create();
        assertInstanceOf(SCMDatabaseAdapter.class, layer);
    }

    @Test
    void exceptionFactoryShouldReturnFallbackWhenForcedFlat() {
        System.setProperty("packing.integration.exceptions.mode", "flat");
        IExceptionDispatcher dispatcher = ExceptionDispatcherFactory.create();
        assertInstanceOf(FallbackConsoleLogger.class, dispatcher);
    }

    @Test
    void exceptionFactoryShouldReturnScmAdapterWhenForcedScm() {
        System.setProperty("packing.integration.exceptions.mode", "scm");
        IExceptionDispatcher dispatcher = ExceptionDispatcherFactory.create();
        assertInstanceOf(SCMExceptionAdapter.class, dispatcher);
    }
}
