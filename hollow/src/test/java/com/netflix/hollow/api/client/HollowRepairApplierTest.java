package com.netflix.hollow.api.client;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class HollowRepairApplierTest {

    @Test
    public void testRepairNoChangesNeeded() throws Exception {
        // Arrange: Both states are identical
        HollowReadStateEngine snapshotState = new HollowReadStateEngine();
        HollowReadStateEngine consumerState = new HollowReadStateEngine();

        HollowRepairApplier applier = new HollowRepairApplier();

        // Act
        HollowRepairApplier.RepairResult result = applier.repair(consumerState, snapshotState);

        // Assert
        assertTrue("Repair should succeed", result.isSuccess());
        assertEquals("Should repair 0 ordinals", 0, result.getOrdinalsRepaired());
    }

    @Test
    public void testRepairResultStructure() {
        // Arrange & Act
        HollowRepairApplier applier = new HollowRepairApplier();
        HollowReadStateEngine emptySnapshot = new HollowReadStateEngine();
        HollowReadStateEngine emptyConsumer = new HollowReadStateEngine();

        HollowRepairApplier.RepairResult result = applier.repair(emptyConsumer, emptySnapshot);

        // Assert
        assertNotNull("Result should not be null", result);
        assertTrue("Result should indicate success", result.isSuccess());
        assertNotNull("Ordinals repaired by type map should not be null", result.getOrdinalsRepairedByType());
        assertEquals("Empty states should result in 0 repairs", 0, result.getOrdinalsRepaired());
    }

    @Test
    public void testRepairHandlesNullSchemas() {
        // Arrange: Empty state engines (no schemas)
        HollowReadStateEngine snapshotState = new HollowReadStateEngine();
        HollowReadStateEngine consumerState = new HollowReadStateEngine();

        HollowRepairApplier applier = new HollowRepairApplier();

        // Act
        HollowRepairApplier.RepairResult result = applier.repair(consumerState, snapshotState);

        // Assert
        assertTrue("Should succeed with empty schemas", result.isSuccess());
        assertEquals("Should repair 0 ordinals", 0, result.getOrdinalsRepaired());
    }

    @Test
    public void testRepairWithSchemaEvolutionNewTypeAdded() throws IOException {
        HollowReadStateEngine consumerState = createState(new Movie(1, "Inception"));
        HollowReadStateEngine snapshotState = createState(
            new Movie(1, "Inception"),
            new Actor(1, "Leonardo DiCaprio")
        );

        HollowRepairApplier.RepairResult result = new HollowRepairApplier().repair(consumerState, snapshotState);

        assertTrue(result.isSuccess());
        assertNotNull(consumerState.getTypeState("Movie"));
        assertNull(consumerState.getTypeState("Actor"));
        assertNotNull(snapshotState.getTypeState("Actor"));
    }

    @Test
    public void testRepairWithSchemaEvolutionTypeRemoved() throws IOException {
        HollowReadStateEngine consumerState = createState(
            new Movie(1, "Inception"),
            new Actor(1, "Leonardo DiCaprio")
        );
        HollowReadStateEngine snapshotState = createState(new Movie(1, "Inception"));

        HollowRepairApplier.RepairResult result = new HollowRepairApplier().repair(consumerState, snapshotState);

        assertTrue(result.isSuccess());
        assertNotNull(consumerState.getTypeState("Actor"));
        assertNull(snapshotState.getTypeState("Actor"));
    }

    @Test
    public void testRepairWithSchemaModification() throws IOException {
        HollowReadStateEngine consumerState = createState(new Movie(1, "Inception"));
        HollowReadStateEngine snapshotState = createState(new MovieWithYear(1, "Inception", 2010));

        HollowRepairApplier.RepairResult result = new HollowRepairApplier().repair(consumerState, snapshotState);

        assertTrue(result.isSuccess());
        assertNotNull(consumerState.getTypeState("Movie"));
        assertNotNull(snapshotState.getTypeState("MovieWithYear"));
    }

    @Test
    public void testRepairWithMultipleSchemaChanges() throws IOException {
        HollowReadStateEngine consumerState = createState(
            new Movie(1, "Inception"),
            new Actor(1, "Leonardo DiCaprio")
        );
        HollowReadStateEngine snapshotState = createState(
            new MovieWithYear(1, "Inception", 2010),
            new Director(1, "Christopher Nolan")
        );

        HollowRepairApplier.RepairResult result = new HollowRepairApplier().repair(consumerState, snapshotState);

        assertTrue(result.isSuccess());
        assertNotNull(consumerState.getTypeState("Movie"));
        assertNotNull(consumerState.getTypeState("Actor"));
        assertNull(consumerState.getTypeState("MovieWithYear"));
        assertNull(consumerState.getTypeState("Director"));
        assertNull(snapshotState.getTypeState("Movie"));
        assertNull(snapshotState.getTypeState("Actor"));
        assertNotNull(snapshotState.getTypeState("MovieWithYear"));
        assertNotNull(snapshotState.getTypeState("Director"));
    }

    private HollowReadStateEngine createState(Object... objects) throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        for (Object obj : objects) {
            mapper.add(obj);
        }

        writeEngine.prepareForWrite();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new com.netflix.hollow.core.write.HollowBlobWriter(writeEngine).writeSnapshot(baos);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        new com.netflix.hollow.core.read.engine.HollowBlobReader(readEngine)
            .readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        return readEngine;
    }

    // Test data classes
    static class Movie {
        int id;
        String title;

        Movie(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    static class MovieWithYear {
        int id;
        String title;
        int year;

        MovieWithYear(int id, String title, int year) {
            this.id = id;
            this.title = title;
            this.year = year;
        }
    }

    static class Actor {
        int id;
        String name;

        Actor(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class Director {
        int id;
        String name;

        Director(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
