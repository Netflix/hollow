package com.netflix.hollow.diffview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.diffview.effigy.pairer.exact.HistoryExactRecordMatcher;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.model.HistoryStateTypeChanges;
import com.netflix.hollow.history.ui.model.RecordDiff;
import com.netflix.hollow.history.ui.model.RecordDiffTreeNode;
import com.netflix.hollow.history.ui.naming.HollowHistoryRecordNamer;
import com.netflix.hollow.test.consumer.TestBlob;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        testBlobRetriever.addSnapshot(1, new TestBlob(1,new ByteArrayInputStream(baos_v1.toByteArray())));

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
        testBlobRetriever.addSnapshot(2, new TestBlob(2,new ByteArrayInputStream(baos_v2.toByteArray())));
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
        testBlobRetriever.addSnapshot(3, new TestBlob(3,new ByteArrayInputStream(baos_v3.toByteArray())));
        testBlobRetriever.addDelta(2, new TestBlob(2, 2, new ByteArrayInputStream(baos_v2_to_v3.toByteArray())));
        testBlobRetriever.addReverseDelta(3, new TestBlob(3, 2, new ByteArrayInputStream(baos_v3_to_v2.toByteArray())));

        // v4
        stateEngine.prepareForNextCycle();
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v4");
        addMovie(stateEngine, 2, "movie2-added-in-v1-modified-in-v2-removed-in-v5");
        addMovie(stateEngine, 4, "movie4-added-in-v2-modified-in-v4");
        addMovie(stateEngine, 5, "movie5-added-in-v3-removed-in-v5");
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v3_to_v4 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v4_to_v3 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v4);
        writer.writeDelta(baos_v3_to_v4);
        writer.writeReverseDelta(baos_v4_to_v3);
        testBlobRetriever.addSnapshot(4, new TestBlob(4,new ByteArrayInputStream(baos_v4.toByteArray())));
        testBlobRetriever.addDelta(3, new TestBlob(3, 4, new ByteArrayInputStream(baos_v3_to_v4.toByteArray())));
        testBlobRetriever.addReverseDelta(4, new TestBlob(4, 3, new ByteArrayInputStream(baos_v4_to_v3.toByteArray())));

        // v5
        stateEngine.prepareForNextCycle();
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v5");
        addMovie(stateEngine, 4, "movie4-added-in-v2-modified-in-v4");
        addMovie(stateEngine, 6, "movie6-added-in-v6");
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v5 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v4_to_v5 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v5_to_v4 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v5);
        writer.writeDelta(baos_v4_to_v5);
        writer.writeReverseDelta(baos_v5_to_v4);
        testBlobRetriever.addSnapshot(5, new TestBlob(5,new ByteArrayInputStream(baos_v5.toByteArray())));
        testBlobRetriever.addDelta(4, new TestBlob(4, 5, new ByteArrayInputStream(baos_v4_to_v5.toByteArray())));
        testBlobRetriever.addReverseDelta(5, new TestBlob(5, 4, new ByteArrayInputStream(baos_v5_to_v4.toByteArray())));
    }

    public static void assertUiParity(HollowHistoryUI hui1, HollowHistoryUI hui2) {
        HollowHistory h1 = hui1.getHistory();
        HollowHistory h2 = hui2.getHistory();
        HollowHistoryUI ui1 = new HollowHistoryUI("", h1);
        HollowHistoryUI ui2 = new HollowHistoryUI("", h2);
        long currentRandomizedTag1 = h1.getLatestState().getCurrentRandomizedTag();
        long currentRandomizedTag2 = h2.getLatestState().getCurrentRandomizedTag();
        String str1, str2, msg;
        List<RecordDiff> addedDiffs1;
        List<RecordDiff> addedDiffs2;
        List<RecordDiff> removedDiffs1;
        List<RecordDiff> removedDiffs2;
        List<RecordDiff> modifiedDiffs1;
        List<RecordDiff> modifiedDiffs2;
        HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMapping1;
        HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMapping2;
        HollowHistoricalState state1, state2;
        HashMap<String, String> displayMapDiffView1, displayMapDiffView2;

        //OverviewPage
        assertEquals("Should have same number of Historical States", h1.getHistoricalStates().length, h2.getHistoricalStates().length);
        long prev1, next1, prev2, next2, ver1, ver2;
        for (int j = 0; j < h1.getHistoricalStates().length; j++) {
            state1 = h1.getHistoricalStates()[j];
            state2 = h2.getHistoricalStates()[j];

            next1 = getNextStateVersion(state1);
            prev1 = getPreviousStateVersion(state1, h1);
            next2 = getNextStateVersion(state2);
            prev2 = getPreviousStateVersion(state2, h2);

            //make sure traversal is in the right order
            assertEquals("Prev state should be the same", prev1, prev2);
            assertEquals("Next state should be the same", next1, next2);
            assertEquals("Same size of type mappings for historical state", state1.getKeyOrdinalMapping().getTypeMappings().size(), state2.getKeyOrdinalMapping().getTypeMappings().size());
            assertEquals("Not same key set of type mappings for historical state", state1.getKeyOrdinalMapping().getTypeMappings().keySet(), state2.getKeyOrdinalMapping().getTypeMappings().keySet());

            Map<String, String> headerTags1 = state1.getHeaderEntries();
            Map<String, String> headerTags2 = state2.getHeaderEntries();
            assertEquals(headerTags1, headerTags2);

            for (String key : state2.getKeyOrdinalMapping().getTypeMappings().keySet()) {

                typeKeyMapping1 = state1.getKeyOrdinalMapping().getTypeMappings().get(key);
                typeKeyMapping2 = state2.getKeyOrdinalMapping().getTypeMappings().get(key);

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

                assertEquals("Added subgroups does not match", typeChanges1.getAddedRecords().hasSubGroups(), typeChanges2.getAddedRecords().hasSubGroups());
                assertEquals("Removed subgroups does not match", typeChanges1.getRemovedRecords().hasSubGroups(), typeChanges2.getRemovedRecords().hasSubGroups());
                assertEquals("Added subgroups does not match", typeChanges1.getModifiedRecords().hasSubGroups(), typeChanges2.getModifiedRecords().hasSubGroups());

                if (!typeChanges1.getAddedRecords().isEmpty()) {
                    if (!typeChanges1.getAddedRecords().hasSubGroups()) {
                        displayMapDiffView1 = new HashMap<>();
                        displayMapDiffView2= new HashMap<>();
                        for (int i = 0; i < addedDiffs1.size(); i++) {
                            str1 = getDiffViewOutput(state2, key, addedDiffs1.get(i), currentRandomizedTag1, ui1);
                            str2 = getDiffViewOutput(state1, key, addedDiffs2.get(i), currentRandomizedTag2, ui2);
                            displayMapDiffView1.put(addedDiffs1.get(i).getIdentifierString(), str1);
                            displayMapDiffView2.put(addedDiffs2.get(i).getIdentifierString(), str2);
                        }
                        assertEquals(displayMapDiffView1, displayMapDiffView2);
                    } else {    // SNAP: TODO: to test with subgroups, or remove
                        assertEquals("Added records of sub groups size", typeChanges1.getAddedRecords().getSubGroups().size(),
                                typeChanges2.getAddedRecords().getSubGroups().size());
                        for (int i = 0; i < typeChanges1.getAddedRecords().getSubGroups().size(); i++) {
                            assertEquals("Added Record group name",
                                    ((RecordDiffTreeNode) typeChanges1.getAddedRecords().getSubGroups().toArray()[i]).getGroupName(),
                                    ((RecordDiffTreeNode) typeChanges2.getAddedRecords().getSubGroups().toArray()[i]).getGroupName());
                            assertEquals("Added Record group name",
                                    ((RecordDiffTreeNode) typeChanges1.getAddedRecords().getSubGroups().toArray()[i]).getDiffCount(),
                                    ((RecordDiffTreeNode) typeChanges2.getAddedRecords().getSubGroups().toArray()[i]).getDiffCount());
                            assertEquals("Added Record group name",
                                    ((RecordDiffTreeNode) typeChanges1.getAddedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName(),
                                    ((RecordDiffTreeNode) typeChanges2.getAddedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName());
                        }
                    }
                }

                if (!typeChanges1.getModifiedRecords().isEmpty()) {
                    if (!typeChanges1.getModifiedRecords().hasSubGroups()) {
                        displayMapDiffView1 = new HashMap<>();
                        displayMapDiffView2= new HashMap<>();
                        for (int i = 0; i < modifiedDiffs1.size(); i++) {
                            str1 = getDiffViewOutput(state1, key, modifiedDiffs1.get(i), currentRandomizedTag1, ui1);
                            str2 = getDiffViewOutput(state2, key, modifiedDiffs2.get(i), currentRandomizedTag2, ui2);
                            displayMapDiffView1.put(modifiedDiffs1.get(i).getIdentifierString(), str1);
                            displayMapDiffView2.put(modifiedDiffs2.get(i).getIdentifierString(), str2);
                        }
                        assertEquals(displayMapDiffView1, displayMapDiffView2);
                    } else {
                        assertEquals("Modified records of sub groups size", typeChanges1.getModifiedRecords().getSubGroups().size(),
                                typeChanges2.getModifiedRecords().getSubGroups().size());
                        for (int i = 0; i < typeChanges1.getModifiedRecords().getSubGroups().size(); i++) {
                            assertEquals("Modified Record group name",
                                    ((RecordDiffTreeNode) typeChanges1.getModifiedRecords().getSubGroups().toArray()[i]).getGroupName(),
                                    ((RecordDiffTreeNode) typeChanges2.getModifiedRecords().getSubGroups().toArray()[i]).getGroupName());
                            assertEquals("Modified Record group name",
                                    ((RecordDiffTreeNode) typeChanges1.getModifiedRecords().getSubGroups().toArray()[i]).getDiffCount(),
                                    ((RecordDiffTreeNode) typeChanges2.getModifiedRecords().getSubGroups().toArray()[i]).getDiffCount());
                            assertEquals("Modified Record group name",
                                    ((RecordDiffTreeNode) typeChanges1.getModifiedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName(),
                                    ((RecordDiffTreeNode) typeChanges2.getModifiedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName());
                        }
                    }
                }

                if (!typeChanges1.getRemovedRecords().isEmpty()) {
                    if (!typeChanges1.getRemovedRecords().hasSubGroups()) {
                        displayMapDiffView1 = new HashMap<>();
                        displayMapDiffView2= new HashMap<>();
                        for (int i = 0; i < removedDiffs1.size(); i++) {
                            str1 = getDiffViewOutput(state1, key, removedDiffs1.get(i), currentRandomizedTag1, ui1);
                            str2 = getDiffViewOutput(state2, key, removedDiffs2.get(i), currentRandomizedTag2, ui2);
                            displayMapDiffView1.put(removedDiffs1.get(i).getIdentifierString(), str1);
                            displayMapDiffView2.put(removedDiffs1.get(i).getIdentifierString(), str2);
                        }
                        assertEquals(displayMapDiffView1, displayMapDiffView2);
                    } else {
                        assertEquals("Removed records of sub groups size", typeChanges1.getRemovedRecords().getSubGroups().size(),
                                typeChanges2.getRemovedRecords().getSubGroups().size());
                        for (int i = 0; i < typeChanges1.getRemovedRecords().getSubGroups().size(); i++) {
                            assertEquals("Removed Record group name",
                                    ((RecordDiffTreeNode) typeChanges1.getRemovedRecords().getSubGroups().toArray()[i]).getGroupName(),
                                    ((RecordDiffTreeNode) typeChanges2.getRemovedRecords().getSubGroups().toArray()[i]).getGroupName());
                            assertEquals("Removed Record group name",
                                    ((RecordDiffTreeNode) typeChanges1.getRemovedRecords().getSubGroups().toArray()[i]).getDiffCount(),
                                    ((RecordDiffTreeNode) typeChanges2.getRemovedRecords().getSubGroups().toArray()[i]).getDiffCount());
                            assertEquals("Removed Record group name",
                                    ((RecordDiffTreeNode) typeChanges1.getRemovedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName(),
                                    ((RecordDiffTreeNode) typeChanges2.getRemovedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName());
                        }
                    }
                }
            }
        }
    }

    private static long getNextStateVersion(HollowHistoricalState currentHistoricalState) {
        if (currentHistoricalState.getNextState() != null)
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

    public static String getDiffViewOutput(HollowHistoricalState stateD, String key, RecordDiff addedDiff, long currentRandomizedTagD, HollowHistoryUI historyUI) {
        HollowDiffViewRow rootRowD = new HollowObjectDiffViewGenerator(stateD.getDataAccess(), stateD.getDataAccess(),
                historyUI, key,
                addedDiff.getFromOrdinal(),
                addedDiff.getToOrdinal()).getHollowDiffViewRows();
        HollowHistoryView objectViewD = new HollowHistoryView(stateD.getVersion(), key,
                addedDiff.getKeyOrdinal(), currentRandomizedTagD,
                rootRowD, HistoryExactRecordMatcher.INSTANCE);
        objectViewD.resetView();

        String diffViewOutputD = null;
        try {
            StringWriter writer = new StringWriter();
            DiffViewOutputGenerator.buildChildRowDisplayData(objectViewD.getRootRow(), writer);
            diffViewOutputD = writer.toString();
        } catch(IOException unexpected) {
            throw new RuntimeException(unexpected);
        }
        return diffViewOutputD;
    }

    private static void addMovie(HollowWriteStateEngine stateEngine, int id, String name) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) stateEngine.getSchema("Movie"));
        rec.setInt("id", id);
        rec.setString("name", name);
        stateEngine.add("Movie", rec);
    }

    private void exploreOrdinals(HollowReadStateEngine readStateEngine, long v) {
        System.out.println("readStateEngine= " + readStateEngine + ", v= " + v);
        for (HollowTypeReadState typeReadState : readStateEngine.getTypeStates()) {
            BitSet populatedOrdinals = typeReadState.getPopulatedOrdinals();
            System.out.println("Type= " + typeReadState.getSchema().getName() + ", PopulatedOrdinals= " + populatedOrdinals);
            int ordinal = populatedOrdinals.nextSetBit(0);
            while (ordinal != -1) {

                HollowObjectTypeReadState o = (HollowObjectTypeReadState) typeReadState;
                // SNAP: System.out.println(String.format("%s: %s, %s", ordinal, o.readInt(ordinal, 0), o.readInt(ordinal, 1)));
                ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
            }
        }
    }
}
