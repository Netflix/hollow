package com.netflix.hollow.diffview;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigyFactory;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.model.HistoryStateTypeChanges;
import com.netflix.hollow.history.ui.model.RecordDiff;
import com.netflix.hollow.history.ui.naming.HollowHistoryRecordNamer;
import com.netflix.hollow.test.consumer.TestBlob;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility to help create a delta chain (with reverse deltas) containing some history UI friendly fake data,
 * and utilities to help compare outputs of HollowHistoryUIs for parity.
 */
public class FakeHollowHistoryUtil {
    private static final String CUSTOM_VERSION_TAG = "myVersion";

    public static void createDeltaChain(TestBlobRetriever testBlobRetriever) throws IOException {

        HollowObjectSchema movieSchema = new HollowObjectSchema("Movie", 2, "id");
        movieSchema.addField("id", HollowObjectSchema.FieldType.INT);
        movieSchema.addField("name", HollowObjectSchema.FieldType.STRING);

        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        stateEngine.addTypeState(new HollowObjectTypeWriteState(movieSchema));

        // v1
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v1");
        addMovie(stateEngine, 1, "movie1-added-in-v1");
        addMovie(stateEngine, 2, "movie2-added-in-v1");
        addMovie(stateEngine, 3, "movie3-added-in-v1-removed-in-v2");
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v1 = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        writer.writeSnapshot(baos_v1);
        testBlobRetriever.addSnapshot(1, new TestBlob(1, new ByteArrayInputStream(baos_v1.toByteArray())));

        // v2
        stateEngine.prepareForNextCycle();
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v2");
        addMovie(stateEngine, 1, "movie1-added-in-v1");
        addMovie(stateEngine, 2, "movie2-added-in-v1-modified-in-v2-removed-in-v5");
        addMovie(stateEngine, 4, "movie4-added-in-v2");
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v1_to_v2 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v2_to_v1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v2 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v2);
        writer.writeDelta(baos_v1_to_v2);
        writer.writeReverseDelta(baos_v2_to_v1);
        testBlobRetriever.addSnapshot(2, new TestBlob(2, new ByteArrayInputStream(baos_v2.toByteArray())));
        testBlobRetriever.addDelta(1, new TestBlob(1, 2, new ByteArrayInputStream(baos_v1_to_v2.toByteArray())));
        testBlobRetriever.addReverseDelta(2, new TestBlob(2, 1, new ByteArrayInputStream(baos_v2_to_v1.toByteArray())));

        // v3
        stateEngine.prepareForNextCycle();
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v3");
        addMovie(stateEngine, 1, "movie1-added-in-v1-modified-in-v3-removed-in-v4");
        addMovie(stateEngine, 2, "movie2-added-in-v1-modified-in-v2-removed-in-v5");
        addMovie(stateEngine, 4, "movie4-added-in-v2");
        addMovie(stateEngine, 5, "movie5-added-in-v3-removed-in-v5");
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v2_to_v3 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v3_to_v2 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v3 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v3);
        writer.writeDelta(baos_v2_to_v3);
        writer.writeReverseDelta(baos_v3_to_v2);
        testBlobRetriever.addSnapshot(3, new TestBlob(3, new ByteArrayInputStream(baos_v3.toByteArray())));
        testBlobRetriever.addDelta(2, new TestBlob(2, 3, new ByteArrayInputStream(baos_v2_to_v3.toByteArray())));
        testBlobRetriever.addReverseDelta(3, new TestBlob(3, 2, new ByteArrayInputStream(baos_v3_to_v2.toByteArray())));

        // v4
        stateEngine.prepareForNextCycle();
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v4");
        addMovie(stateEngine, 2, "movie2-added-in-v1-modified-in-v2-removed-in-v5");
        addMovie(stateEngine, 4, "movie4-added-in-v2-modified-in-v4");
        addMovie(stateEngine, 5, "movie5-added-in-v3-removed-in-v5");
        addMovie(stateEngine, 6, "movie6-added-in-v4");

        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v3_to_v4 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v4_to_v3 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v4);
        writer.writeDelta(baos_v3_to_v4);
        writer.writeReverseDelta(baos_v4_to_v3);
        testBlobRetriever.addSnapshot(4, new TestBlob(4, new ByteArrayInputStream(baos_v4.toByteArray())));
        testBlobRetriever.addDelta(3, new TestBlob(3, 4, new ByteArrayInputStream(baos_v3_to_v4.toByteArray())));
        testBlobRetriever.addReverseDelta(4, new TestBlob(4, 3, new ByteArrayInputStream(baos_v4_to_v3.toByteArray())));

        // v5
        stateEngine.prepareForNextCycle();
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v5");
        addMovie(stateEngine, 4, "movie4-added-in-v2-modified-in-v4");
        addMovie(stateEngine, 6, "movie6-added-in-v4-modified-in-v5");
        addMovie(stateEngine, 7, "movie7-added-in-v5-removed-in-v6");
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v5 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v4_to_v5 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v5_to_v4 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v5);
        writer.writeDelta(baos_v4_to_v5);
        writer.writeReverseDelta(baos_v5_to_v4);
        testBlobRetriever.addSnapshot(5, new TestBlob(5, new ByteArrayInputStream(baos_v5.toByteArray())));
        testBlobRetriever.addDelta(4, new TestBlob(4, 5, new ByteArrayInputStream(baos_v4_to_v5.toByteArray())));
        testBlobRetriever.addReverseDelta(5, new TestBlob(5, 4, new ByteArrayInputStream(baos_v5_to_v4.toByteArray())));

        // v6 - only snapshot artifact, also contains new type in schema- to test double snapshots
        stateEngine.prepareForNextCycle();
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v6");
        addMovie(stateEngine, 4, "movie4-added-in-v2-modified-in-v4-also-modified-in-v6");
        addMovie(stateEngine, 8, "movie8-added-in-v6");
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v6 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v6);
        testBlobRetriever.addSnapshot(6, new TestBlob(6, new ByteArrayInputStream(baos_v6.toByteArray())));


        // v7 - introduces schema change
        stateEngine.prepareForNextCycle();
        HollowObjectSchema actorSchema = new HollowObjectSchema("Actor", 1, "id");
        actorSchema.addField("id", HollowObjectSchema.FieldType.INT);
        stateEngine.addTypeState(new HollowObjectTypeWriteState(actorSchema));
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v7");
        addMovie(stateEngine, 4, "movie4-added-in-v2-modified-in-v4-also-modified-in-v6");
        addActor(stateEngine, 1);
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v7 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v6_to_v7 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v7_to_v6 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v7);
        writer.writeDelta(baos_v6_to_v7);
        writer.writeReverseDelta(baos_v7_to_v6);
        testBlobRetriever.addSnapshot(7, new TestBlob(7, new ByteArrayInputStream(baos_v7.toByteArray())));
        testBlobRetriever.addDelta(6, new TestBlob(6, 7, new ByteArrayInputStream(baos_v6_to_v7.toByteArray())));
        testBlobRetriever.addReverseDelta(7, new TestBlob(7, 6, new ByteArrayInputStream(baos_v7_to_v6.toByteArray())));

        // v0 - snapshot only - just to test that double snapshot can not be applied in reverse direction
        HollowWriteStateEngine stateEngineV0 = new HollowWriteStateEngine();
        stateEngineV0.addTypeState(new HollowObjectTypeWriteState(movieSchema));
        addMovie(stateEngineV0, 0, "movie0-never-shows-up-in-ui");
        stateEngineV0.prepareForWrite();
        ByteArrayOutputStream baos_v0 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v0);
        testBlobRetriever.addSnapshot(0, new TestBlob(0, new ByteArrayInputStream(baos_v0.toByteArray())));
    }

    public static void assertUiParity(HollowHistoryUI hui1, HollowHistoryUI hui2) {
        HollowHistory h1 = hui1.getHistory();
        HollowHistory h2 = hui2.getHistory();
        List<RecordDiff> addedDiffs1, addedDiffs2, removedDiffs1, removedDiffs2, modifiedDiffs1, modifiedDiffs2;
        HollowHistoricalState state1, state2;

        //OverviewPage
        assertEquals("Should have same number of Historical States", h1.getHistoricalStates().length, h2.getHistoricalStates().length);
        for(int j = 0; j < h1.getHistoricalStates().length; j++) {
            state1 = h1.getHistoricalStates()[j];
            state2 = h2.getHistoricalStates()[j];

            // make sure traversal is in the right order
            assertEquals("Prev state should be the same", getPreviousStateVersion(state1, h1), getPreviousStateVersion(state2, h2));
            assertEquals("Next state should be the same", getNextStateVersion(state1), getNextStateVersion(state2));
            assertEquals("Same size of type mappings for historical state", state1.getKeyOrdinalMapping().getTypeMappings().size(), state2.getKeyOrdinalMapping().getTypeMappings().size());
            assertEquals("Not same key set of type mappings for historical state", state1.getKeyOrdinalMapping().getTypeMappings().keySet(), state2.getKeyOrdinalMapping().getTypeMappings().keySet());

            Map<String, String> headerTags1 = state1.getHeaderEntries();
            Map<String, String> headerTags2 = state2.getHeaderEntries();
            assertEquals(headerTags1, headerTags2);

            for(String key : state2.getKeyOrdinalMapping().getTypeMappings().keySet()) {

                HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMapping1 = state1.getKeyOrdinalMapping().getTypeMappings().get(key);
                HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMapping2 = state2.getKeyOrdinalMapping().getTypeMappings().get(key);

                assertEquals("No. of added records", typeKeyMapping1.getNumberOfNewRecords(), typeKeyMapping2.getNumberOfNewRecords());
                assertEquals("No. of removed records", typeKeyMapping1.getNumberOfRemovedRecords(), typeKeyMapping2.getNumberOfRemovedRecords());
                assertEquals("No. of modified records", typeKeyMapping1.getNumberOfModifiedRecords(), typeKeyMapping2.getNumberOfModifiedRecords());

                HistoryStateTypeChanges typeChanges1 = new HistoryStateTypeChanges(state1, key, HollowHistoryRecordNamer.DEFAULT_RECORD_NAMER, new String[0]);
                HistoryStateTypeChanges typeChanges2 = new HistoryStateTypeChanges(state2, key, HollowHistoryRecordNamer.DEFAULT_RECORD_NAMER, new String[0]);

                addedDiffs1 = typeChanges1.getAddedRecords().getRecordDiffs();
                addedDiffs2 = typeChanges2.getAddedRecords().getRecordDiffs();
                removedDiffs1 = typeChanges1.getRemovedRecords().getRecordDiffs();
                removedDiffs2 = typeChanges2.getRemovedRecords().getRecordDiffs();
                modifiedDiffs1 = typeChanges1.getModifiedRecords().getRecordDiffs();
                modifiedDiffs2 = typeChanges2.getModifiedRecords().getRecordDiffs();

                assertEquals("Add Diffs size", addedDiffs1.size(), addedDiffs2.size());
                assertEquals("Remove Diffs size", removedDiffs1.size(), removedDiffs2.size());
                assertEquals("Modified Diffs size", modifiedDiffs1.size(), modifiedDiffs2.size());

                assertEquals("Added subgroups (if any)", typeChanges1.getAddedRecords().hasSubGroups(), typeChanges2.getAddedRecords().hasSubGroups());
                assertEquals("Removed subgroups (if any)", typeChanges1.getRemovedRecords().hasSubGroups(), typeChanges2.getRemovedRecords().hasSubGroups());
                assertEquals("Added subgroups (if any)", typeChanges1.getModifiedRecords().hasSubGroups(), typeChanges2.getModifiedRecords().hasSubGroups());
                HollowEffigyFactory effigyFactory = new HollowEffigyFactory();

                Set<HollowEffigy> addedEffigies1 = new HashSet<>();
                Set<HollowEffigy> addedEffigies2 = new HashSet<>();
                if(!typeChanges1.getAddedRecords().isEmpty()) {
                    addedEffigies1 = toEffigies(addedDiffs1, effigyFactory, state1);
                    addedEffigies2 = toEffigies(addedDiffs2, effigyFactory, state2);
                }
                assertEquals(addedEffigies1, addedEffigies2);

                Set<HollowEffigy> modifiedFromEffigies1 = new HashSet<>();
                Set<HollowEffigy> modifiedToEffigies1 = new HashSet<>();
                Set<HollowEffigy> modifiedFromEffigies2 = new HashSet<>();
                Set<HollowEffigy> modifiedToEffigies2 = new HashSet<>();
                if(!typeChanges1.getModifiedRecords().isEmpty()) {
                    modifiedFromEffigies1 = fromEffigies(modifiedDiffs1, effigyFactory, state1);
                    modifiedFromEffigies2 = fromEffigies(modifiedDiffs2, effigyFactory, state2);

                    modifiedToEffigies1 = toEffigies(modifiedDiffs1, effigyFactory, state1);
                    modifiedToEffigies2 = toEffigies(modifiedDiffs2, effigyFactory, state2);
                }
                assertEquals(modifiedFromEffigies1, modifiedFromEffigies2);
                assertEquals(modifiedToEffigies1, modifiedToEffigies2);

                Set<HollowEffigy> removedEffigies1 = new HashSet<>();
                Set<HollowEffigy> removedEffigies2 = new HashSet<>();
                if(!typeChanges1.getRemovedRecords().isEmpty()) {
                    removedEffigies1 = fromEffigies(removedDiffs1, effigyFactory, state1);
                    removedEffigies2 = fromEffigies(removedDiffs2, effigyFactory, state2);
                }
                assertEquals(removedEffigies1, removedEffigies2);
            }
        }
    }

    private static Set<HollowEffigy> fromEffigies(List<RecordDiff> recordDiffs, HollowEffigyFactory effigyFactory, HollowHistoricalState historicalState) {
        Set<HollowEffigy> fromEffigies = new HashSet<>();
        for(int i = 0; i < recordDiffs.size(); i++) {
            RecordDiff recordDiff = recordDiffs.get(i);
            HollowEffigy fromEffigy = effigyFactory.effigy(historicalState.getDataAccess(),
                    "Movie", recordDiff.getFromOrdinal());
            fromEffigies.add(fromEffigy);
        }
        return fromEffigies;
    }

    private static Set<HollowEffigy> toEffigies(List<RecordDiff> recordDiffs, HollowEffigyFactory effigyFactory, HollowHistoricalState historicalState) {
        Set<HollowEffigy> toEffigies = new HashSet<>();
        for(int i = 0; i < recordDiffs.size(); i++) {
            RecordDiff recordDiff = recordDiffs.get(i);
            HollowEffigy toEffigy = effigyFactory.effigy(historicalState.getDataAccess(),
                    "Movie", recordDiff.getToOrdinal());
            toEffigies.add(toEffigy);
        }
        return toEffigies;
    }

    private static long getNextStateVersion(HollowHistoricalState currentHistoricalState) {
        if(currentHistoricalState.getNextState() != null)
            return currentHistoricalState.getNextState().getVersion();
        return -1;
    }

    private static long getPreviousStateVersion(HollowHistoricalState currentHistoricalState, HollowHistory history) {
        for(HollowHistoricalState state : history.getHistoricalStates()) {
            if(state.getNextState() == currentHistoricalState) {
                return state.getVersion();
            }
        }
        return -1;
    }

    private static void addMovie(HollowWriteStateEngine stateEngine, int id, String name) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) stateEngine.getSchema("Movie"));
        rec.setInt("id", id);
        rec.setString("name", name);
        stateEngine.add("Movie", rec);
    }

    private static void addActor(HollowWriteStateEngine stateEngine, int id) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) stateEngine.getSchema("Actor"));
        rec.setInt("id", id);
        stateEngine.add("Actor", rec);
    }
}
