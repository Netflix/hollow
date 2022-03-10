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
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
//import com.netflix.hollow.explorer.ui.jetty.HollowExplorerUIServer;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Test;

public class MyHistoryUITest {

    @Test
    public void startServerOnPort7777() throws Exception {

        HollowHistory historyD = createHistoryD();
        HollowHistoryUIServer serverD = new HollowHistoryUIServer(historyD, 7777);
        serverD.start();

        HollowHistory historyR = createReverseHistoryR();
        HollowHistoryUIServer serverR = new HollowHistoryUIServer(historyR, 7778);
        serverR.start();


        //HollowExplorerUIServer ui_revdelta = new HollowExplorerUIServer(historyR.getLatestState(),8889);
        //ui_revdelta.start();

        //HollowExplorerUIServer ui_delta = new HollowExplorerUIServer(historyD.getLatestState(),8888);
        //ui_delta.start();

        //ui_delta.join();
        //ui_revdelta.join();
        serverD.join();
        serverR.join();
    }


    private HollowWriteStateEngine stateEngine;
    private HollowObjectSchema schema;
    private HollowObjectSchema bSchema;

    private HollowHistory createHistory() throws IOException, Exception {
        //setup producer
        stateEngine = new HollowWriteStateEngine();

        //initalalize schema
        schema = new HollowObjectSchema("TypeA", 2);
        //add columns/fields to initialized schema
        schema.addField("a1", FieldType.INT);
        schema.addField("a2", FieldType.INT);

        HollowTypeWriteState writeState = new HollowObjectTypeWriteState(schema);

        //attach schema to write state engine
        stateEngine.addTypeState(writeState);

        //1) add records
        addRecord(1, 1);
        addRecord(2, 2);
        addRecord(3, 3);
        addRecord(4, 4);
        addRecord(5, 5);
        addRecord(6, 6);

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
        history.getKeyIndex().addTypeIndex("TypeA", "a1");

        //write phase to add rec phase
        stateEngine.prepareForNextCycle();

        //2) add new set of records
        addRecord(1, 1);
        addRecord(2, 7);
        addRecord(3, 3);
        addRecord(5, 8);
        addRecord(6, 6);
        addRecord(7, 9);
        addRecord(8, 10);

        //add rec to write phase
        stateEngine.prepareForWrite();

        //reinit output stream
        baos = new ByteArrayOutputStream();
        //write delta based on new records
        writer.writeDelta(baos);
        //apply delta to snapshot generate in 1
        reader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));
        //
        history.deltaOccurred(19991231235959999L);
        //write to add rec phase
        stateEngine.prepareForNextCycle();

        //3) add new recs
        addRecord(1, 1);
        addRecord(2, 7);
        addRecord(3, 11);
        addRecord(6, 12);
        addRecord(7, 13);
        addRecord(8, 10);

        //add rec to write phase
        stateEngine.prepareForWrite();

        //reinit output stream
        baos = new ByteArrayOutputStream();
        //write new delta to output stream
        writer.writeDelta(baos);
        //apply delta to previous reader
        reader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));
        //?
        history.deltaOccurred(20001231235959999L);

        // Double Snapshot
        bSchema = new HollowObjectSchema("TypeB", 2, "b1");
        bSchema.addField("b1", FieldType.INT);
        bSchema.addField("b2", FieldType.INT);
        { // do double snapshot and introduce new type
            //new producer
            HollowWriteStateEngine stateEngine2 = new HollowWriteStateEngine();
            //attach new and old schema
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(schema));
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(bSchema));
            //add recs
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
            addRec(stateEngine2, bSchema, new String[] { "b1", "b2" }, new int[] { 9, 999 });
            //new readengine
            HollowReadStateEngine readStateEngine2 = new HollowReadStateEngine();
            //populate new read engine with new write engine
            StateEngineRoundTripper.roundTripSnapshot(stateEngine2, readStateEngine2, null);
            //update index
            setupKeyIndex(readStateEngine2, history);
            history.doubleSnapshotOccurred(readStateEngine2, 20011231235959999L);
        }

        { // do double snapshot and remove type
            //new producer
            HollowWriteStateEngine stateEngine2 = new HollowWriteStateEngine();
            //use old schema
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(schema));
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 22, 22 });
            //new read engine
            HollowReadStateEngine readStateEngine2 = new HollowReadStateEngine();
            //populate new read engine with new write engine
            StateEngineRoundTripper.roundTripSnapshot(stateEngine2, readStateEngine2, null);
            //update index
            setupKeyIndex(readStateEngine2, history);
            //stitch history with double snap
            history.doubleSnapshotOccurred(readStateEngine2, 20021231235959999L);
        }


        return history;
    }

    private HollowHistory createHistoryD() throws IOException, Exception {
        ByteArrayOutputStream baos_v2_to_v1, baos_v3_to_v2;
        HollowHistory history;
        HollowReadStateEngine readStateEngine;
        HollowBlobReader reader;
        HollowWriteStateEngine stateEngine;

        {
            schema = new HollowObjectSchema("TypeA", 2);

            stateEngine = new HollowWriteStateEngine();
            schema.addField("a1", FieldType.INT);
            schema.addField("a2", FieldType.INT);

            //attach schema to write state engine
            stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));

            stateEngine.addHeaderTag("snapversion", "0");
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
            stateEngine.addHeaderTag("snapversion", "19981231235959999");
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
            baos_v2_to_v1 = new ByteArrayOutputStream();

            //write delta based on new records
            writer.writeDelta(baos_v1_to_v2);
            writer.writeReverseDelta(baos_v2_to_v1);

            stateEngine.prepareForNextCycle();
            stateEngine.addHeaderTag("snapversion", "19991231235959999");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 11 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 13 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });

            stateEngine.prepareForWrite();

            //reinit output stream
            ByteArrayOutputStream baos_v2_to_v3 = new ByteArrayOutputStream();
            baos_v3_to_v2 = new ByteArrayOutputStream();

            //write delta based on new records
            writer.writeDelta(baos_v2_to_v3);
            writer.writeReverseDelta(baos_v3_to_v2);

            //v4
            stateEngine.prepareForNextCycle();
            stateEngine.addHeaderTag("snapversion", "20001231235959999L");
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



            ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
            //write snapshot to output stream
            writer.writeSnapshot(baos_v4);

            readStateEngine = new HollowReadStateEngine();
            reader = new HollowBlobReader(readStateEngine);
            //load snapshot from output stream to read state engine
            reader.readSnapshot(HollowBlobInput.serial(baos_v1.toByteArray()));

            //>>>do not init history with the snapshot
            history = new HollowHistory(readStateEngine, 0, 10);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");

            System.out.println("Delta version 0");
            exploreOrdinals(readStateEngine);


            reader.applyDelta(HollowBlobInput.serial(baos_v1_to_v2.toByteArray()));
            history.deltaOccurred(19981231235959999L);
            System.out.println("Delta version 19981231235959999L");
            exploreOrdinals(readStateEngine);


            reader.applyDelta(HollowBlobInput.serial(baos_v2_to_v3.toByteArray()));
            history.deltaOccurred(19991231235959999L);
            System.out.println("Delta version 19991231235959999L");
            exploreOrdinals(readStateEngine);


            reader.applyDelta(HollowBlobInput.serial(baos_v3_to_v4.toByteArray()));
            System.out.println("Delta version 20001231235959999L");
            history.deltaOccurred(20001231235959999L);
            exploreOrdinals(readStateEngine);


        }

        return history;
    }

    private void exploreOrdinals(HollowReadStateEngine readStateEngine) {
        for (HollowTypeReadState typeReadState : readStateEngine.getTypeStates()) {
            BitSet populatedOrdinals = typeReadState.getPopulatedOrdinals();
            System.out.println("PopulatedOrdinals= " + populatedOrdinals);
            int ordinal = populatedOrdinals.nextSetBit(0);
            while (ordinal != -1) {

                HollowObjectTypeReadState o = (HollowObjectTypeReadState) typeReadState;
                System.out.println(String.format("%s: %s, %s", ordinal, o.readInt(ordinal, 0), o.readInt(ordinal, 1)));
                ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
            }
        }
    }

    private HollowHistory createReverseHistoryR() throws IOException, Exception {
        ByteArrayOutputStream baos_v2_to_v1, baos_v3_to_v2;
        HollowHistory history;
        HollowReadStateEngine readStateEngine;
        HollowBlobReader reader;
        HollowWriteStateEngine stateEngine;
        schema = new HollowObjectSchema("TypeA", 2);
        schema.addField("a1", FieldType.INT);
        schema.addField("a2", FieldType.INT);

        bSchema = new HollowObjectSchema("TypeB", 2, "b1");
        bSchema.addField("b1", FieldType.INT);
        bSchema.addField("b2", FieldType.INT);
/*
        { // do double snapshot and remove type
            //new producer
            HollowWriteStateEngine stateEngine2 = new HollowWriteStateEngine();
            //use old schema
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(schema));
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 22, 22 });
            //new read engine
            HollowReadStateEngine readStateEngine2 = new HollowReadStateEngine();
            //populate new read engine with new write engine
            StateEngineRoundTripper.roundTripSnapshot(stateEngine2, readStateEngine2, null);
            //update index
            //setupKeyIndex(readStateEngine2, history);
            //stitch history with double snap
            //history.doubleSnapshotOccurred(readStateEngine2, 20021231235959999L);
            history = new HollowHistory(readStateEngine2, 20021231235959999L, 10, true, true);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");
        }
        { // do double snapshot and introduce new type
            //new producer
            HollowWriteStateEngine stateEngine2 = new HollowWriteStateEngine();
            //attach new and old schema
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(schema));
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(bSchema));
            //add recs
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
            addRec(stateEngine2, bSchema, new String[] { "b1", "b2" }, new int[] { 9, 999 });
            //new readengine
            HollowReadStateEngine readStateEngine2 = new HollowReadStateEngine();
            //populate new read engine with new write engine
            StateEngineRoundTripper.roundTripSnapshot(stateEngine2, readStateEngine2, null);
            //update index
            setupKeyIndex(readStateEngine2, history);
            history.doubleSnapshotOccurred(readStateEngine2, 20011231235959999L);
        }
*/


        {


            stateEngine = new HollowWriteStateEngine();
            //attach schema to write state engine
            stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
            stateEngine.addHeaderTag("snapversion", "0");
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
            stateEngine.addHeaderTag("snapversion", "19981231235959999L");
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
            baos_v2_to_v1 = new ByteArrayOutputStream();

            //write delta based on new records
            writer.writeDelta(baos_v1_to_v2);
            writer.writeReverseDelta(baos_v2_to_v1);

            stateEngine.prepareForNextCycle();
            stateEngine.addHeaderTag("snapversion", "19991231235959999L");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 11 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 13 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });

            stateEngine.prepareForWrite();

            //reinit output stream
            ByteArrayOutputStream baos_v2_to_v3 = new ByteArrayOutputStream();
            baos_v3_to_v2 = new ByteArrayOutputStream();

            //write delta based on new records
            writer.writeDelta(baos_v2_to_v3);
            writer.writeReverseDelta(baos_v3_to_v2);

            //v4
            stateEngine.prepareForNextCycle();
            stateEngine.addHeaderTag("snapversion", "20001231235959999L");
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



            ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
            //write snapshot to output stream
            writer.writeSnapshot(baos_v4);

            readStateEngine = new HollowReadStateEngine();
            reader = new HollowBlobReader(readStateEngine);
            //load snapshot from output stream to read state engine
            reader.readSnapshot(HollowBlobInput.serial(baos_v4.toByteArray()));
            //StateEngineRoundTripper.roundTripSnapshot(stateEngine2, readStateEngine2, null);
            //update index
            //setupKeyIndex(readStateEngine, history);
            //stitch history with double snap
            //history.doubleSnapshotOccurred(readStateEngine, 20021231235959999L);
            //>>>do not init history with the snapshot
            history = new HollowHistory(readStateEngine, 0, 10, true, true);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");
            System.out.println("RDelta version 0");
            exploreOrdinals(readStateEngine);


            reader.applyDelta(HollowBlobInput.serial(baos_v4_to_v3.toByteArray()));
            history.reverseDeltaOccurred(20001231235959999L);
            System.out.println("RDelta version 19981231235959999L");
            exploreOrdinals(readStateEngine);


            reader.applyDelta(HollowBlobInput.serial(baos_v3_to_v2.toByteArray()));
            history.reverseDeltaOccurred(19991231235959999L);
            System.out.println("Delta version 19991231235959999L");
            exploreOrdinals(readStateEngine);


            reader.applyDelta(HollowBlobInput.serial(baos_v2_to_v1.toByteArray()));
            history.reverseDeltaOccurred(19981231235959999L);
            System.out.println("Delta version 20001231235959999L");
            exploreOrdinals(readStateEngine);


            // history.removeHistoricalStates(1);
            //history.reverseDeltaOccurred(19981231235959999L);
            //HollowExplorerUIServer ui_delta = new HollowExplorerUIServer(readStateEngine, 8888);
            //ui_delta.start();
            //ui_delta.join();
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