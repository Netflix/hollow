package com.netflix.hollow.diff.ui;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
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
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HistoryUITest {

    private HollowWriteStateEngine stateEngine;
    private HollowObjectSchema schema;
    private HollowObjectSchema bSchema;
    private static final String DELTA_KEY = "delta";
    private static final String REVERSE_DELTA_KEY = "reverse_delta";


    @Test
    public void startServerOnPorts7777And7778() throws Exception {
        HashMap<String, HollowHistory> histories = createSimpleHistoryDeltaAndReverse();
        HollowHistory historyD = histories.get(DELTA_KEY);
        HollowHistoryUIServer serverD = new HollowHistoryUIServer(historyD, 7777);
        serverD.start();

        HollowHistory historyR = histories.get(REVERSE_DELTA_KEY);
        HollowHistoryUIServer serverR = new HollowHistoryUIServer(historyR, 7778);
        serverR.start();
/*
        HollowExplorerUIServer ui_revdelta = new HollowExplorerUIServer(historyR.getLatestState(),8889);
        ui_revdelta.start();

        HollowExplorerUIServer ui_delta = new HollowExplorerUIServer(historyD.getLatestState(),8888);
        ui_delta.start();

        ui_delta.join();
        ui_revdelta.join();
 */

        serverD.join();
        serverR.join();
    }

    @Test
    public void startServerOnPort7777() throws Exception {
        HollowHistory historyD = createHistory();
        HollowHistoryUIServer serverD = new HollowHistoryUIServer(historyD, 7777);
        serverD.start();
/*
        HollowExplorerUIServer ui_delta = new HollowExplorerUIServer(historyD.getLatestState(),8888);
        ui_delta.start();

        ui_delta.join();
 */
        serverD.join();
    }

    private HashMap<String, HollowHistory> createSimpleHistoryDeltaAndReverse() throws IOException, Exception {
        HollowHistory historyD;
        HollowReadStateEngine readStateEngineD;
        HollowBlobReader readerD;
        HollowHistory historyR;
        HollowReadStateEngine readStateEngineR;
        HollowBlobReader readerR;
        HollowWriteStateEngine stateEngine;
        HashMap<String, HollowHistory> histories = new HashMap<String, HollowHistory>();


        schema = new HollowObjectSchema("TypeA", 2);

        stateEngine = new HollowWriteStateEngine();
        schema.addField("a1", FieldType.INT);
        schema.addField("a2", FieldType.INT);

        //attach schema to write state engine
        stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
        stateEngine.addHeaderTag("snapversion", "v1");

        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 4 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 5 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });

        //add rec to write phase
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v1 = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        //write snapshot to output stream
        writer.writeSnapshot(baos_v1);

        stateEngine.prepareForNextCycle();

        //2) add new set of records
        stateEngine.addHeaderTag("snapversion", "v2");
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 8 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 9 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });

        //add rec to write phase
        stateEngine.prepareForWrite();

        //reinit output stream
        ByteArrayOutputStream baos_v1_to_v2 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v2_to_v1 = new ByteArrayOutputStream();

        //write delta based on new records
        writer.writeDelta(baos_v1_to_v2);
        writer.writeReverseDelta(baos_v2_to_v1);

        stateEngine.prepareForNextCycle();
        stateEngine.addHeaderTag("snapversion", "v3");
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 11 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 13 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });

        stateEngine.prepareForWrite();

        //reinit output stream
        ByteArrayOutputStream baos_v2_to_v3 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v3_to_v2 = new ByteArrayOutputStream();

        //write delta based on new records
        writer.writeDelta(baos_v2_to_v3);
        writer.writeReverseDelta(baos_v3_to_v2);

        //v4
        stateEngine.prepareForNextCycle();
        stateEngine.addHeaderTag("snapversion", "v4");
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 18 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 19 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 13 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 18, 10 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 28, 90 });

        stateEngine.prepareForWrite();

        //reinit output stream
        ByteArrayOutputStream baos_v3_to_v4 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v4_to_v3 = new ByteArrayOutputStream();

        //write delta based on new records
        writer.writeDelta(baos_v3_to_v4);
        writer.writeReverseDelta(baos_v4_to_v3);

        //write snapshot to output stream
        ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v4);

        readStateEngineD = new HollowReadStateEngine();
        readerD = new HollowBlobReader(readStateEngineD);
        //load snapshot from output stream to read state engine
        readerD.readSnapshot(HollowBlobInput.serial(baos_v1.toByteArray()));
        //>>>do not init history with the snapshot
        historyD = new HollowHistory(readStateEngineD, 1L, 10);
        historyD.getKeyIndex().addTypeIndex("TypeA", "a1");
        readerD.applyDelta(HollowBlobInput.serial(baos_v1_to_v2.toByteArray()));
        historyD.deltaOccurred(2L);
        readerD.applyDelta(HollowBlobInput.serial(baos_v2_to_v3.toByteArray()));
        historyD.deltaOccurred(3L);
        readerD.applyDelta(HollowBlobInput.serial(baos_v3_to_v4.toByteArray()));
        historyD.deltaOccurred(4L);


        readStateEngineR = new HollowReadStateEngine();
        readerR = new HollowBlobReader(readStateEngineR);
        //load snapshot from output stream to read state engine
        readerR.readSnapshot(HollowBlobInput.serial(baos_v4.toByteArray()));
        //>>>do not init history with the snapshot
        historyR = new HollowHistory(readStateEngineR, 4L, 10, true, true);
        historyR.getKeyIndex().addTypeIndex("TypeA", "a1");
        readerR.applyDelta(HollowBlobInput.serial(baos_v4_to_v3.toByteArray()));
        historyR.reverseDeltaOccurred(3L);
        readerR.applyDelta(HollowBlobInput.serial(baos_v3_to_v2.toByteArray()));
        historyR.reverseDeltaOccurred(2L);
        readerR.applyDelta(HollowBlobInput.serial(baos_v2_to_v1.toByteArray()));
        historyR.reverseDeltaOccurred(1L);

        histories.put(DELTA_KEY, historyD);
        histories.put(REVERSE_DELTA_KEY, historyR);

        return histories;
    }

    private void printUnMatchedStr(String msg, String str1, String str2){
        int len = str1.length();
        if(len>str2.length()){
            len = str2.length();
        }
        int i=0;
        while(i<len){
            if(str1.charAt(i)!=str2.charAt(i))
                break;
            i++;
        }
        if(i==len){
            return;
        }
        System.out.println(msg+" does not match str1 len "+str1.length()+" str2 len "+str2.length());
        if(i<str1.length()){
            System.out.println(msg+" does not match strD : "+str1.substring(i));
        }
        if(i<str2.length()){
            System.out.println(msg+" does not match strR : "+str2.substring(i));
        }
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

    @Test
    public void matchDeltaWithReverseDelta() throws Exception {
        HashMap<String, HollowHistory> histories = createSimpleHistoryDeltaAndReverse();
        HollowHistory historyD = histories.get(DELTA_KEY);
        HollowHistory historyR = histories.get(REVERSE_DELTA_KEY);
        HollowHistoryUI uiD = new HollowHistoryUI("", historyD);
        HollowHistoryUI uiR = new HollowHistoryUI("", historyR);
        long currentRandomizedTagD = historyD.getLatestState().getCurrentRandomizedTag();
        long currentRandomizedTagR = historyR.getLatestState().getCurrentRandomizedTag();
        String strD, strR, msg;
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
        HashMap<String, String> displayMapDiffViewD;
        HashMap<String, String> displayMapDiffViewR;

        //OverviewPage
        assertEquals("Should have same number of Historical States",
                historyD.getHistoricalStates().length,
                historyR.getHistoricalStates().length);
        long prevD, nextD, prevR, nextR, verD, verR;
        for(int j=0; j<historyR.getHistoricalStates().length; j++) {
            stateR = historyR.getHistoricalStates()[j];
            stateD = historyD.getHistoricalStates()[j];
            verD = stateD.getVersion();
            verR = stateR.getVersion();

            prevD = getPreviousStateVersion(stateD, historyD);
            prevR = getPreviousStateVersion(stateR, historyR);
            nextD = getNextStateVersion(stateD);
            nextR = getNextStateVersion(stateR);

            //make sure traversing in the right order
            if(prevD != -1){
                assertEquals("Not same next prevD", verR, prevD);
            }

            if(prevR != -1){
                assertEquals("Not same next prevD", verD, prevR);
            }

            assertEquals("Not same number of type mappings ", stateD.getKeyOrdinalMapping().getTypeMappings().size(),
                    stateR.getKeyOrdinalMapping().getTypeMappings().size());

            assertEquals("Not same key set of type mappings", stateD.getKeyOrdinalMapping().getTypeMappings().keySet(),
                    stateR.getKeyOrdinalMapping().getTypeMappings().keySet());

            List<String> entriesD = getHeaderEntries(historyD, stateD);
            List<String> entriesR = getHeaderEntries(historyR, stateR);

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
                assertEquals("Modified records are not equal", typeKeyMappingD.getNumberOfModifiedRecords(),
                        typeKeyMappingR.getNumberOfModifiedRecords());

                //for each type in historical state  build state changes
                typeChangesD = new HistoryStateTypeChanges(stateD, key, HollowHistoryRecordNamer.DEFAULT_RECORD_NAMER, new String[0]);
                typeChangesR = new HistoryStateTypeChanges(stateR, key, HollowHistoryRecordNamer.DEFAULT_RECORD_NAMER, new String[0]);

                addedDiffsD = typeChangesD.getAddedRecords().getRecordDiffs();
                addedDiffsR = typeChangesR.getAddedRecords().getRecordDiffs();
                removedDiffsD = typeChangesD.getRemovedRecords().getRecordDiffs();
                removedDiffsR = typeChangesR.getRemovedRecords().getRecordDiffs();
                modifiedDiffsD = typeChangesD.getModifiedRecords().getRecordDiffs();
                modifiedDiffsR = typeChangesR.getModifiedRecords().getRecordDiffs();

                if(addedDiffsD.size() == 0 && addedDiffsR.size() == 0 &&
                        removedDiffsD.size() == 0 && removedDiffsR.size() == 0 &&
                        modifiedDiffsD.size() == 0 && modifiedDiffsR.size() == 0){
                    continue;
                }

                assertEquals("Add Diffs do not match", addedDiffsD.size(), addedDiffsR.size());
                assertEquals("Remove Diffs do not match", removedDiffsD.size(), removedDiffsR.size());
                assertEquals("Modified Diffs do not match", modifiedDiffsD.size(), modifiedDiffsR.size());

                assertEquals("Added subgroups does not match", typeChangesD.getAddedRecords().hasSubGroups(), typeChangesR.getAddedRecords().hasSubGroups());
                assertEquals("Removed subgroups does not match", typeChangesD.getRemovedRecords().hasSubGroups(), typeChangesR.getRemovedRecords().hasSubGroups());
                assertEquals("Added subgroups does not match", typeChangesD.getModifiedRecords().hasSubGroups(), typeChangesR.getModifiedRecords().hasSubGroups());

                if (!typeChangesD.getAddedRecords().isEmpty()) {
                    if (!typeChangesD.getAddedRecords().hasSubGroups()) {
                        displayMapDiffViewD = new HashMap<>();
                        displayMapDiffViewR= new HashMap<>();
                        for (int i = 0; i < addedDiffsD.size(); i++) {
                            strD = getDiffViewOutput(stateD, key, addedDiffsD.get(i), currentRandomizedTagD, uiD);
                            strR = getDiffViewOutput(stateR, key, addedDiffsR.get(i), currentRandomizedTagR, uiR);
                            displayMapDiffViewD.put(addedDiffsD.get(i).getIdentifierString(), strD);
                            displayMapDiffViewR.put(addedDiffsR.get(i).getIdentifierString(), strR);
                        }
                        assertEquals("Add Record Diff view missing keys "+key+" "+stateD.getVersion()+"<##############> "+ stateR.getVersion(),
                                displayMapDiffViewD.keySet(), displayMapDiffViewR.keySet());

                        for (String idKey: displayMapDiffViewR.keySet()) {
                            msg = "Add Record Diff view key list does not match "+key+" "+stateD.getVersion()+"<##############> "+ stateR.getVersion();
                            assertTrue(msg,
                                    displayMapDiffViewD.containsKey(idKey) && displayMapDiffViewD.get(idKey) != null);

                            strD = displayMapDiffViewD.get(idKey);
                            strR = displayMapDiffViewR.get(idKey);

                            printUnMatchedStr("Delta-"+verD+" Reverse-"+verR+" key-"+key+" idKey-"+idKey+" Add Record Diff view ", strD, strR);
                            assertEquals("Add Record Diff view len does not match", strD.length(), strR.length());
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
                        displayMapDiffViewD = new HashMap<>();
                        displayMapDiffViewR= new HashMap<>();
                        for (int i = 0; i < modifiedDiffsD.size(); i++) {
                            strD = getDiffViewOutput(stateD, key, modifiedDiffsD.get(i), currentRandomizedTagD, uiD);
                            strR = getDiffViewOutput(stateR, key, modifiedDiffsR.get(i), currentRandomizedTagR, uiR);
                            displayMapDiffViewD.put(modifiedDiffsD.get(i).getIdentifierString(), strD);
                            displayMapDiffViewR.put(modifiedDiffsR.get(i).getIdentifierString(), strR);
                        }
                        assertEquals("Modified Record Diff view missing keys "+key+" "+stateD.getVersion()+"<##############> "+ stateR.getVersion(),
                                displayMapDiffViewD.keySet(), displayMapDiffViewR.keySet());

                        for (String idKey: displayMapDiffViewR.keySet()) {
                            msg = "Modified Record Diff view key list does not match "+key+" "+stateD.getVersion()+"<##############> "+ stateR.getVersion();
                            assertTrue(msg,
                                    displayMapDiffViewD.containsKey(idKey) && displayMapDiffViewD.get(idKey) != null);

                            strD = displayMapDiffViewD.get(idKey);
                            strR = displayMapDiffViewR.get(idKey);

                            printUnMatchedStr("Delta-"+verD+" Reverse-"+verR+" key-"+key+" idKey-"+idKey+" Modified Record Diff view ", strD, strR);
                            assertEquals("Modified Record Diff view len does not match", strD.length(), strR.length());
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
                        displayMapDiffViewD = new HashMap<>();
                        displayMapDiffViewR= new HashMap<>();
                        for (int i = 0; i < removedDiffsD.size(); i++) {
                            strD = getDiffViewOutput(stateD, key, removedDiffsD.get(i), currentRandomizedTagD, uiD);
                            strR = getDiffViewOutput(stateR, key, removedDiffsR.get(i), currentRandomizedTagR, uiR);
                            displayMapDiffViewD.put(removedDiffsD.get(i).getIdentifierString(), strD);
                            displayMapDiffViewR.put(removedDiffsD.get(i).getIdentifierString(), strR);
                        }

                        assertEquals("Removed Record Diff view missing keys "+key+" "+stateD.getVersion()+"<##############> "+ stateR.getVersion(),
                                displayMapDiffViewD.keySet(), displayMapDiffViewR.keySet());
                        for (String idKey: displayMapDiffViewR.keySet()) {
                            msg = "Removed Record Diff view key list does not match "+key+" "+stateD.getVersion()+"<##############> "+ stateR.getVersion();
                            assertTrue(msg,
                                    displayMapDiffViewD.containsKey(idKey) && displayMapDiffViewD.get(idKey) != null);

                            strD = displayMapDiffViewD.get(idKey);
                            strR = displayMapDiffViewR.get(idKey);

                            printUnMatchedStr("Delta-"+verD+" Reverse-"+verR+" key-"+key+" idKey-"+idKey+" Removed Record Diff view ", strD, strR);
                            assertEquals("Removed Record Diff view len does not match", strD.length(), strR.length());
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
            DiffViewOutputGenerator.buildChildRowDisplayDataSimple(objectViewD.getRootRow(), writer, true);
            diffViewOutputD = writer.toString();
        } catch(IOException unexpected) {
            throw new RuntimeException(unexpected);
        }
        return diffViewOutputD;
    }

    protected List<String> getHeaderEntries(HollowHistory hist, HollowHistoricalState state) {
        Map<String, String> fromTags;
        Map<String, String> toTags;
        if(state.IsReverseDelta()){
            toTags = state.getHeaderEntries();
            fromTags = hist.getLatestState().getHeaderTags();
        }else{
            fromTags = state.getHeaderEntries();
            toTags = hist.getLatestState().getHeaderTags();
        }

        if(state.getNextState() != null) {
            if(state.IsReverseDelta()) {
                fromTags = state.getNextState().getHeaderEntries();
            }else{
                toTags = state.getNextState().getHeaderEntries();
            }
        }

        Set<String> allKeys = new HashSet<String>();
        allKeys.addAll(fromTags.keySet());
        allKeys.addAll(toTags.keySet());

        List<String> entries = new ArrayList<String>();

        int i=0;

        for(String key : allKeys) {
            entries.add(" "+(i++)+" "+ key+" "+ fromTags.get(key)+" "+ toTags.get(key));
        }

        return entries;
    }

    private void exploreOrdinals(HollowReadStateEngine readStateEngine) {
        System.out.println("snapversion= " + readStateEngine.getHeaderTags().get("snapversion"));
        for (HollowTypeReadState typeReadState : readStateEngine.getTypeStates()) {
            BitSet populatedOrdinals = typeReadState.getPopulatedOrdinals();
            System.out.println("SNAP: PopulatedOrdinals= " + populatedOrdinals);
            int ordinal = populatedOrdinals.nextSetBit(0);
            while (ordinal != -1) {
                HollowObjectTypeReadState o = (HollowObjectTypeReadState) typeReadState;
                System.out.println(String.format("%s: %s, %s", ordinal, o.readInt(ordinal, 0), o.readInt(ordinal, 1)));
                ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
            }
        }
    }

    private HollowHistory createHistory() throws IOException {
        stateEngine = new HollowWriteStateEngine();

        schema = new HollowObjectSchema("TypeA", 2);
        schema.addField("a1", FieldType.INT);
        schema.addField("a2", FieldType.INT);

        HollowTypeWriteState writeState = new HollowObjectTypeWriteState(schema);

        stateEngine.addTypeState(writeState);

        addRecord(1, 1);
        addRecord(2, 2);
        addRecord(3, 3);
        addRecord(4, 4);
        addRecord(5, 5);
        addRecord(6, 6);

        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        writer.writeSnapshot(baos);


        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
        HollowHistory history = new HollowHistory(readStateEngine, 0, 10);
        history.getKeyIndex().addTypeIndex("TypeA", "a1");

        stateEngine.prepareForNextCycle();

        addRecord(1, 1);
        addRecord(2, 7);
        addRecord(3, 3);
        addRecord(5, 8);
        addRecord(6, 6);
        addRecord(7, 9);
        addRecord(8, 10);

        stateEngine.prepareForWrite();

        baos = new ByteArrayOutputStream();
        writer.writeDelta(baos);
        reader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));
        history.deltaOccurred(19991231235959999L);

        stateEngine.prepareForNextCycle();

        addRecord(1, 1);
        addRecord(2, 7);
        addRecord(3, 11);
        addRecord(6, 12);
        addRecord(7, 13);
        addRecord(8, 10);

        stateEngine.prepareForWrite();

        baos = new ByteArrayOutputStream();
        writer.writeDelta(baos);
        reader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));
        history.deltaOccurred(20001231235959999L);

        // Double Snapshot
        bSchema = new HollowObjectSchema("TypeB", 2, "b1");
        bSchema.addField("b1", FieldType.INT);
        bSchema.addField("b2", FieldType.INT);
        { // do double snapshot and introduce new type
            HollowWriteStateEngine stateEngine2 = new HollowWriteStateEngine();
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(schema));
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(bSchema));
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
            addRec(stateEngine2, bSchema, new String[] { "b1", "b2" }, new int[] { 9, 999 });

            HollowReadStateEngine readStateEngine2 = new HollowReadStateEngine();
            StateEngineRoundTripper.roundTripSnapshot(stateEngine2, readStateEngine2, null);
            setupKeyIndex(readStateEngine2, history);
            history.doubleSnapshotOccurred(readStateEngine2, 20011231235959999L);
        }

        { // do double snapshot and remove type
            HollowWriteStateEngine stateEngine2 = new HollowWriteStateEngine();
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(schema));
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 22, 22 });

            HollowReadStateEngine readStateEngine2 = new HollowReadStateEngine();
            StateEngineRoundTripper.roundTripSnapshot(stateEngine2, readStateEngine2, null);
            setupKeyIndex(readStateEngine2, history);
            history.doubleSnapshotOccurred(readStateEngine2, 20021231235959999L);
        }

        return history;
    }

    private void addRecord(int a1, int a2) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("a1", a1);
        rec.setInt("a2", a2);
        stateEngine.add("TypeA", rec);
    }

    private void setupKeyIndex(HollowReadStateEngine stateEngine, HollowHistory history) {
        HollowHistoryKeyIndex keyIndex = history.getKeyIndex();
        for (String type : stateEngine.getAllTypes()) {

            HollowTypeReadState typeState = stateEngine.getTypeState(type);
            HollowSchema schema = typeState.getSchema();
            if (schema instanceof HollowObjectSchema) {
                HollowObjectSchema oSchema = (HollowObjectSchema) schema;
                PrimaryKey pKey = oSchema.getPrimaryKey();
                if (pKey == null) continue;

                keyIndex.indexTypeField(pKey, stateEngine);
                System.out.println("Setup KeyIndex: type=" + type + "\t" + pKey);
            }
        }
    }

    private static void addRec(HollowWriteStateEngine stateEngine, HollowObjectSchema schema, String[] names, int[] vals) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < names.length; i++) {
            rec.setInt(names[i], vals[i]);
        }
        stateEngine.add(schema.getName(), rec);
    }


}
