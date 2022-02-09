package com.netflix.hollow.diff.ui;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.diffview.DiffViewOutputGenerator;
import com.netflix.hollow.diffview.HollowDiffViewRow;
import com.netflix.hollow.diffview.HollowHistoryView;
import com.netflix.hollow.diffview.HollowObjectDiffViewGenerator;
import com.netflix.hollow.diffview.effigy.pairer.exact.HistoryExactRecordMatcher;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.history.ui.model.HistoryStateTypeChanges;
import com.netflix.hollow.history.ui.model.RecordDiff;
import com.netflix.hollow.history.ui.model.RecordDiffTreeNode;
import com.netflix.hollow.history.ui.naming.HollowHistoryRecordNamer;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class ReverseDeltaHistoryUITest {

    @Test
    public void startServerOnPort7777() throws Exception {
        HollowHistory history = createHistory();
        HollowHistory historyReverse = createHistoryReverse();

        HollowHistoryUIServer server = new HollowHistoryUIServer(history, 7777);
        HollowHistoryUIServer serverReverse = new HollowHistoryUIServer(historyReverse, 7778);
        server.start();
        serverReverse.start();
        server.join();
        serverReverse.join();
    }

    @Test
    public void matchDeltaWithReverseDelta() throws Exception {
        HollowHistory historyD = createHistory();
        HollowHistory historyR = createHistoryReverse();
        HollowHistoryUI uiD = new HollowHistoryUI("", historyD);
        HollowHistoryUI uiR = new HollowHistoryUI("", historyR);
        long currentRandomizedTagD = historyD.getLatestState().getCurrentRandomizedTag();
        long currentRandomizedTagR = historyD.getLatestState().getCurrentRandomizedTag();
        String strD, strR;
        List<RecordDiff> addedDiffsD;
        List<RecordDiff> addedDiffsR;
        List<RecordDiff> removedDiffsD;
        List<RecordDiff> removedDiffsR;
        List<RecordDiff> modifiedDiffsD;
        List<RecordDiff> modifiedDiffsR;
        int fromOrdinalD;
        int toOrdinalD;
        int fromOrdinalR;
        int toOrdinalR;
        boolean removeIterDSate;
        boolean removeIterRState;
        boolean addedIterDSate;
        boolean addedIterRState;
        HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMappingD;
        HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMappingR;
        IntMap.IntMapEntryIterator removedIterD;
        IntMap.IntMapEntryIterator addedIterD;
        IntMap.IntMapEntryIterator removedIterR;
        IntMap.IntMapEntryIterator addedIterR;
        HollowHistoricalState stateR;
        HollowHistoricalState stateD;
        HistoryStateTypeChanges typeChangesD;
        HistoryStateTypeChanges typeChangesR;

        //OverviewPage
        assertEquals("Should have same number of Historical States",
                historyD.getHistoricalStates().length,
                historyR.getHistoricalStates().length);
        for(int j=0; j<historyR.getHistoricalStates().length; j++) {
            stateR = historyR.getHistoricalStates()[j];
            stateD = historyD.getHistoricalStates()[j];

            assertEquals("Version should match", stateD.getVersion(), stateR.getVersion());
            assertEquals("Not same number of type mappings", stateD.getKeyOrdinalMapping().getTypeMappings().size(),
                    stateR.getKeyOrdinalMapping().getTypeMappings().size());


            //header entries compare

            assertEquals("Not same next version", getNextStateVersion(stateD), getPreviousStateVersion(stateR, historyR));
            assertEquals("Not same next version", getNextStateVersion(stateR), getPreviousStateVersion(stateD, historyD));

            for (String key : stateD.getKeyOrdinalMapping().getTypeMappings().keySet()) {
                removeIterDSate = true;
                removeIterRState = true;
                addedIterDSate = true;
                addedIterRState = true;

                typeKeyMappingD = stateD.getKeyOrdinalMapping().getTypeMappings().get(key);
                typeKeyMappingR = stateR.getKeyOrdinalMapping().getTypeMappings().get(key);

                assertEquals("Added records are not equal", typeKeyMappingD.getNumberOfNewRecords(),
                        typeKeyMappingR.getNumberOfNewRecords());
                assertEquals("Removed records are not equal", typeKeyMappingD.getNumberOfRemovedRecords(),
                        typeKeyMappingR.getNumberOfRemovedRecords());
                assertEquals("Removed records are not equal", typeKeyMappingD.getNumberOfModifiedRecords(),
                        typeKeyMappingR.getNumberOfModifiedRecords());

                removedIterD = typeKeyMappingD.removedOrdinalMappingIterator();
                addedIterD = typeKeyMappingD.addedOrdinalMappingIterator();
                removedIterR = typeKeyMappingR.removedOrdinalMappingIterator();
                addedIterR = typeKeyMappingR.addedOrdinalMappingIterator();

                removeIterDSate = removedIterD.next();
                removeIterRState = removedIterR.next();

                while (removeIterDSate && removeIterRState) {

                    fromOrdinalD = removedIterD.getValue();
                    toOrdinalD = typeKeyMappingD.findAddedOrdinal(removedIterD.getKey());
                    fromOrdinalR = removedIterR.getValue();
                    toOrdinalR = typeKeyMappingR.findAddedOrdinal(removedIterR.getKey());

                    assertEquals("From Ordinals not same", fromOrdinalD, fromOrdinalR);
                    assertEquals("To Ordinals not same", toOrdinalD, toOrdinalR);
                    removeIterDSate = removedIterD.next();
                    removeIterRState = removedIterR.next();
                }

                assertEquals("Not same number of removed ordinals", removeIterDSate, removeIterRState);
                addedIterDSate = addedIterD.next();
                addedIterRState = addedIterR.next();
                while (addedIterDSate && addedIterRState) {
                    if (typeKeyMappingD.findRemovedOrdinal(addedIterD.getKey()) == -1 &&
                            typeKeyMappingR.findRemovedOrdinal(addedIterR.getKey()) == -1) {
                        toOrdinalD = addedIterD.getValue();
                        toOrdinalR = addedIterR.getValue();
                        assertEquals("Not same to ordinal", toOrdinalD, toOrdinalR);
                    }
                    addedIterDSate = addedIterD.next();
                    addedIterRState = addedIterR.next();
                }
                assertEquals("Not same number of added ordinals", addedIterDSate, addedIterRState);

                //for each type in historical state  build state changes
                typeChangesD = new HistoryStateTypeChanges(stateD, key, HollowHistoryRecordNamer.DEFAULT_RECORD_NAMER, new String[0]);
                typeChangesR = new HistoryStateTypeChanges(stateR, key, HollowHistoryRecordNamer.DEFAULT_RECORD_NAMER, new String[0]);

                addedDiffsD = typeChangesD.getAddedRecords().getRecordDiffs();
                addedDiffsR = typeChangesR.getAddedRecords().getRecordDiffs();
                removedDiffsD = typeChangesD.getRemovedRecords().getRecordDiffs();
                removedDiffsR = typeChangesR.getRemovedRecords().getRecordDiffs();
                modifiedDiffsD = typeChangesD.getModifiedRecords().getRecordDiffs();
                modifiedDiffsR = typeChangesR.getModifiedRecords().getRecordDiffs();

                assertEquals("Add Diffs do not match", addedDiffsD.size(), addedDiffsR.size());
                assertEquals("Remove Diffs do not match", removedDiffsD.size(), removedDiffsR.size());
                assertEquals("Modified Diffs do not match", modifiedDiffsD.size(), modifiedDiffsR.size());

                assertEquals("Added subgroups does not match", typeChangesD.getAddedRecords().hasSubGroups(), typeChangesR.getAddedRecords().hasSubGroups());
                assertEquals("Removed subgroups does not match", typeChangesD.getRemovedRecords().hasSubGroups(), typeChangesR.getRemovedRecords().hasSubGroups());
                assertEquals("Added subgroups does not match", typeChangesD.getModifiedRecords().hasSubGroups(), typeChangesR.getModifiedRecords().hasSubGroups());

                if (!typeChangesD.getAddedRecords().isEmpty()) {
                    if (!typeChangesD.getAddedRecords().hasSubGroups()) {
                        for (int i = 0; i < addedDiffsD.size(); i++) {
                            assertEquals("Added Record Diff Identity String does not match", addedDiffsD.get(i).getIdentifierString(),
                                    addedDiffsR.get(i).getIdentifierString());
                            assertEquals("Added Record Diff Key Ordinal does not match", addedDiffsD.get(i).getKeyOrdinal(),
                                    addedDiffsR.get(i).getKeyOrdinal());


                            strD = getDiffViewOutput(stateD, key, addedDiffsD.get(i), currentRandomizedTagD, uiD);
                            strR = getDiffViewOutput(stateR, key, addedDiffsR.get(i), currentRandomizedTagR, uiR);
                            assertEquals("Add Record Diff view does not match", strD, strR);
                        }
                    } else {
                        assertEquals("Added records of sub groups does not match", typeChangesD.getAddedRecords().getSubGroups().size(),
                                typeChangesR.getAddedRecords().getSubGroups().size());
                        for (int i = 0; i < typeChangesD.getAddedRecords().getSubGroups().size(); i++) {
                            assertEquals("Added Record group name does not match",
                                    ((RecordDiffTreeNode) typeChangesD.getAddedRecords().getSubGroups().toArray()[i]).getGroupName(),
                                    ((RecordDiffTreeNode) typeChangesR.getAddedRecords().getSubGroups().toArray()[i]).getGroupName());
                            assertEquals("Added Record group name does not match",
                                    ((RecordDiffTreeNode) typeChangesD.getAddedRecords().getSubGroups().toArray()[i]).getDiffCount(),
                                    ((RecordDiffTreeNode) typeChangesR.getAddedRecords().getSubGroups().toArray()[i]).getDiffCount());
                            assertEquals("Added Record group name does not match",
                                    ((RecordDiffTreeNode) typeChangesD.getAddedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName(),
                                    ((RecordDiffTreeNode) typeChangesR.getAddedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName());
                        }
                    }
                }

                if (!typeChangesD.getModifiedRecords().isEmpty()) {
                    if (!typeChangesD.getModifiedRecords().hasSubGroups()) {
                        for (int i = 0; i < modifiedDiffsD.size(); i++) {
                            assertEquals("Modified Record Diff Identity String does not match", modifiedDiffsD.get(i).getIdentifierString(),
                                    modifiedDiffsR.get(i).getIdentifierString());
                            assertEquals("Modified Record Diff Key Ordinal does not match", modifiedDiffsD.get(i).getKeyOrdinal(),
                                    modifiedDiffsR.get(i).getKeyOrdinal());
                            strD = getDiffViewOutput(stateD, key, modifiedDiffsD.get(i), currentRandomizedTagD, uiD);
                            strR = getDiffViewOutput(stateR, key, modifiedDiffsR.get(i), currentRandomizedTagR, uiR);
                            assertEquals("Modified Record Diff view does not match", strD, strR);
                        }
                    } else {
                        assertEquals("Modified records of sub groups does not match", typeChangesD.getModifiedRecords().getSubGroups().size(),
                                typeChangesR.getModifiedRecords().getSubGroups().size());
                        for (int i = 0; i < typeChangesD.getModifiedRecords().getSubGroups().size(); i++) {
                            assertEquals("Modified Record group name does not match",
                                    ((RecordDiffTreeNode) typeChangesD.getModifiedRecords().getSubGroups().toArray()[i]).getGroupName(),
                                    ((RecordDiffTreeNode) typeChangesR.getModifiedRecords().getSubGroups().toArray()[i]).getGroupName());
                            assertEquals("Modified Record group name does not match",
                                    ((RecordDiffTreeNode) typeChangesD.getModifiedRecords().getSubGroups().toArray()[i]).getDiffCount(),
                                    ((RecordDiffTreeNode) typeChangesR.getModifiedRecords().getSubGroups().toArray()[i]).getDiffCount());
                            assertEquals("Modified Record group name does not match",
                                    ((RecordDiffTreeNode) typeChangesD.getModifiedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName(),
                                    ((RecordDiffTreeNode) typeChangesR.getModifiedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName());
                        }
                    }
                }

                if (!typeChangesD.getRemovedRecords().isEmpty()) {
                    if (!typeChangesD.getRemovedRecords().hasSubGroups()) {
                        for (int i = 0; i < removedDiffsD.size(); i++) {
                            assertEquals("Removed Record Diff Identity String does not match", removedDiffsD.get(i).getIdentifierString(),
                                    removedDiffsR.get(i).getIdentifierString());
                            assertEquals("Removed Record Diff Key Ordinal does not match", removedDiffsD.get(i).getKeyOrdinal(),
                                    removedDiffsR.get(i).getKeyOrdinal());
                            strD = getDiffViewOutput(stateD, key, removedDiffsD.get(i), currentRandomizedTagD, uiD);
                            strR = getDiffViewOutput(stateR, key, removedDiffsR.get(i), currentRandomizedTagR, uiR);
                            assertEquals("Removed Record Diff view does not match", strD, strR);
                        }
                    } else {
                        assertEquals("Removed records of sub groups does not match", typeChangesD.getRemovedRecords().getSubGroups().size(),
                                typeChangesR.getRemovedRecords().getSubGroups().size());
                        for (int i = 0; i < typeChangesD.getRemovedRecords().getSubGroups().size(); i++) {
                            assertEquals("Removed Record group name does not match",
                                    ((RecordDiffTreeNode) typeChangesD.getRemovedRecords().getSubGroups().toArray()[i]).getGroupName(),
                                    ((RecordDiffTreeNode) typeChangesR.getRemovedRecords().getSubGroups().toArray()[i]).getGroupName());
                            assertEquals("Removed Record group name does not match",
                                    ((RecordDiffTreeNode) typeChangesD.getRemovedRecords().getSubGroups().toArray()[i]).getDiffCount(),
                                    ((RecordDiffTreeNode) typeChangesR.getRemovedRecords().getSubGroups().toArray()[i]).getDiffCount());
                            assertEquals("Removed Record group name does not match",
                                    ((RecordDiffTreeNode) typeChangesD.getRemovedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName(),
                                    ((RecordDiffTreeNode) typeChangesR.getRemovedRecords().getSubGroups().toArray()[i]).getHierarchicalFieldName());
                        }
                    }
                }
            }
        }
    }


    public String getDiffViewOutput(HollowHistoricalState stateD, String key, RecordDiff addedDiff, long currentRandomizedTagD, HollowHistoryUI historyUI) {
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

    private long getNextStateVersion(HollowHistoricalState currentHistoricalState) {
        if (currentHistoricalState.getNextState() != null)
            return currentHistoricalState.getNextState().getVersion();
        return -1;
    }

    private long getPreviousStateVersion(HollowHistoricalState currentHistoricalState, HollowHistory history) {
        for(HollowHistoricalState state : history.getHistoricalStates()) {
            if(state.getNextState() == currentHistoricalState) {
                return state.getVersion();
            }
        }
        return -1;
    }

    private HollowWriteStateEngine stateEngine;
    private HollowObjectSchema schema;
    private HollowObjectSchema bSchema;

    private HollowHistory createHistory() throws IOException {
        //setup producer
        stateEngine = new HollowWriteStateEngine();

        //initalalize schema
        schema = new HollowObjectSchema("Movie", 1);
        //add columns/fields to initialized schema
        schema.addField("id", HollowObjectSchema.FieldType.INT);

        HollowTypeWriteState writeState = new HollowObjectTypeWriteState(schema);

        //attach schema to write state engine
        stateEngine.addTypeState(writeState);

        //1) add records
        addRecord(0);

        //add rec to write phase
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        //write snapshot to output stream
        writer.writeSnapshot(baos);

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        //load snapshot from output stream to read state engine
        reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
        HollowHistory history = new HollowHistory(readStateEngine, 0, 10);
        //setup key index
        history.getKeyIndex().addTypeIndex("Movie", "id");

        applyDeltaState(history, Arrays.asList(0, 1), 1L, writer, reader);
        applyDeltaState(history, Arrays.asList(0, 1, 2), 2L, writer, reader);
        applyDeltaState(history, Arrays.asList(0, 1, 2, 3), 3L, writer, reader);
        applyDeltaState(history, Arrays.asList(0, 1, 2), 4L, writer, reader);
        applyDeltaState(history, Arrays.asList(0, 1), 5L, writer, reader);


        return history;
    }

    private HollowHistory createHistoryReverse() throws IOException, Exception {
        //setup producer
        stateEngine = new HollowWriteStateEngine();

        //initalalize schema
        schema = new HollowObjectSchema("Movie", 1);
        //add columns/fields to initialized schema
        schema.addField("id", HollowObjectSchema.FieldType.INT);

        HollowTypeWriteState writeState = new HollowObjectTypeWriteState(schema);

        //attach schema to write state engine
        stateEngine.addTypeState(writeState);

        //1) add records    - corresponding to v5 of createHistory()
        addRecord(0);

        //add rec to write phase
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        Map<Long, ByteArrayOutputStream> reverseDeltaBaos = new HashMap<>();    // a map version v -> Baos containing reverse delta to get to version v
        createReverseDeltaStateFor(Arrays.asList(0, 1), 0L, 1L, reverseDeltaBaos, writer);
        createReverseDeltaStateFor(Arrays.asList(0, 1, 2), 1L, 2L, reverseDeltaBaos, writer);
        createReverseDeltaStateFor(Arrays.asList(0, 1, 2, 3), 2L, 3L, reverseDeltaBaos, writer);
        createReverseDeltaStateFor(Arrays.asList(0, 1, 2), 3L, 4L,  reverseDeltaBaos, writer);
        createReverseDeltaStateFor(Arrays.asList(0, 1), 4L, 5L, reverseDeltaBaos, writer);

        ByteArrayOutputStream baos_v5 = new ByteArrayOutputStream();
        //write snapshot to output stream
        writer.writeSnapshot(baos_v5);

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        //load snapshot from output stream to read state engine
        reader.readSnapshot(HollowBlobInput.serial(baos_v5.toByteArray()));
        //>>>do not init history with the snapshot
        HollowHistory history = new HollowHistory(readStateEngine, 5L, 10);
        history.getKeyIndex().addTypeIndex("Movie", "id");

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(4L).toByteArray()));
        history.reverseDeltaOccurred(5L);

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(3L).toByteArray()));
        history.reverseDeltaOccurred(4L);

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(2L).toByteArray()));
        history.reverseDeltaOccurred(3L);

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(1L).toByteArray()));
        history.reverseDeltaOccurred(2L);

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(0L).toByteArray()));
        history.reverseDeltaOccurred(1L);

        return history;
    }

    private void createReverseDeltaStateFor(List<Integer> values, long fromVersion, long toVersion, Map<Long, ByteArrayOutputStream> reverseDeltaBaosMap, HollowBlobWriter writer) throws IOException {
        //write phase to add rec phase
        stateEngine.prepareForNextCycle();

        //2) add new set of records
        for (int v : values) {
            addRecord(v);
        }

        //add rec to write phase
        stateEngine.prepareForWrite();

        //reinit output stream
        ByteArrayOutputStream baosDelta = new ByteArrayOutputStream();
        ByteArrayOutputStream baosReverseDelta = new ByteArrayOutputStream();
        //write delta based on new records
        writer.writeDelta(baosDelta);
        writer.writeReverseDelta(baosReverseDelta);
        reverseDeltaBaosMap.put(fromVersion, baosReverseDelta);
    }

    private void applyDeltaState(HollowHistory history, List<Integer> values, long toVersion, HollowBlobWriter writer, HollowBlobReader reader) throws IOException {
        //write phase to add rec phase
        stateEngine.prepareForNextCycle();

        //2) add new set of records
        for (int v : values) {
            addRecord(v);
        }

        //add rec to write phase
        stateEngine.prepareForWrite();

        //reinit output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //write delta based on new records
        writer.writeDelta(baos);

        //apply delta to snapshot generate in 1
        reader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));

        history.deltaOccurred(toVersion);
    }

    private void addRecord(int id) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("id", id);
        stateEngine.add("Movie", rec);
    }
}
