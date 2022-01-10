package com.netflix.hollow.diff.ui;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.history.ui.webserver.HollowHistoryUIServer;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;

public class HistoryUITest {

    @Test
    public void startServerOnPort7777() throws Exception {
        HollowHistory history = createReverseHistory2();
        //HollowHistory history = createHistory();

        HollowHistoryUIServer server = new HollowHistoryUIServer(history, 7777);
        server.start();
        server.join();
    }


    private HollowWriteStateEngine stateEngine;
    private HollowObjectSchema schema;
    private HollowObjectSchema bSchema;

    private HollowHistory createHistory() throws IOException {
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
/*
    private void dump(HollowReadStateEngine typeState){
        BitSet selectedOrdinals = typeState.getPopulatedOrdinals();
        PrimaryKey primaryKey = getPrimaryKey(typeState.getSchema());
        int fieldPathIndexes[][] = getFieldPathIndexes(ui.getStateEngine(), primaryKey);
        List<TypeKey> keys = new ArrayList<>(10);
        int currentOrdinal = selectedOrdinals.nextSetBit(0);
        for(int i = 0; i < pageSize && currentOrdinal != ORDINAL_NONE; i ++) {
            currentOrdinal = selectedOrdinals.nextSetBit(currentOrdinal + 1);
        }
    }
 */

    private HollowHistory createReverseHistory2() throws IOException {
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

            ByteArrayOutputStream baos_v3 = new ByteArrayOutputStream();
            //write snapshot to output stream
            writer.writeSnapshot(baos_v3);

            readStateEngine = new HollowReadStateEngine();
            reader = new HollowBlobReader(readStateEngine);
            //load snapshot from output stream to read state engine
            reader.readSnapshot(HollowBlobInput.serial(baos_v3.toByteArray()));
            //>>>do not init history with the snapshot
            history = new HollowHistory(readStateEngine, 0, 10);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");

            reader.applyDelta(HollowBlobInput.serial(baos_v3_to_v2.toByteArray()));
            history.reverseDeltaOccurred(20001231235959999L);

            reader.applyDelta(HollowBlobInput.serial(baos_v2_to_v1.toByteArray()));
            history.reverseDeltaOccurred(19991231235959999L);
        }

        return history;
    }

    private HollowHistory createReverseHistory1() throws IOException {
        ByteArrayOutputStream baos_first, baos_second;
        HollowHistory history;
        HollowReadStateEngine readStateEngine;
        {
            schema = new HollowObjectSchema("TypeA", 2);

            HollowWriteStateEngine stateEngine2 = new HollowWriteStateEngine();
            schema.addField("a1", FieldType.INT);
            schema.addField("a2", FieldType.INT);

            //attach schema to write state engine
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(schema));
            addRec(stateEngine2, schema, new String[]{"a1", "a2"}, new int[]{1, 1});
            addRec(stateEngine2, schema, new String[]{"a1", "a2"}, new int[]{22, 22});
            //add rec to write phase
            stateEngine2.prepareForWrite();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HollowBlobWriter writer = new HollowBlobWriter(stateEngine2);
            //write snapshot to output stream
            writer.writeSnapshot(baos);

            HollowReadStateEngine readStateEngine2 = new HollowReadStateEngine();
            HollowBlobReader reader = new HollowBlobReader(readStateEngine2);
            //load snapshot from output stream to read state engine
            reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
            //>>>do not init history with the snapshot
            history = new HollowHistory(readStateEngine2, 0, 10);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");
        }

        //>>do not apply double snapshot for now
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
            history.doubleSnapshotOccurred(readStateEngine2, 20021231235959999L);
        }

        //>>>init history with this snapshot
        { // do double snapshot and drop type
            //new producer
            HollowWriteStateEngine stateEngine2 = new HollowWriteStateEngine();
            //attach new and old schema
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(schema));

            //add recs
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 3, 11 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 7, 13 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });

            //new readengine
            readStateEngine = new HollowReadStateEngine();
            //populate new read engine with new write engine
            StateEngineRoundTripper.roundTripSnapshot(stateEngine2, readStateEngine, null);
            //update index
            setupKeyIndex(readStateEngine, history);
            history.doubleSnapshotOccurred(readStateEngine, 20011231235959999L);
        }

        {
            //setup producer
            HollowWriteStateEngine stateEngine2 = new HollowWriteStateEngine();

            //attach schema to write state engine
            stateEngine2.addTypeState(new HollowObjectTypeWriteState(schema));

            //1) add records
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 4, 4 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 5, 5 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });

            //add rec to write phase
            stateEngine2.prepareForWrite();
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            HollowBlobWriter writer2 = new HollowBlobWriter(stateEngine2);
            //write snapshot to output stream
            writer2.writeSnapshot(baos1);


            //HollowReadStateEngine readStateEngine2 = new HollowReadStateEngine();
            //reader = new HollowBlobReader(readStateEngine2);
            //load snapshot from output stream to read state engine
            //reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));

            //write phase to add rec phase
            stateEngine2.prepareForNextCycle();

            //2) add new set of records
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 5, 8 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 7, 9 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });

            //add rec to write phase
            stateEngine2.prepareForWrite();

            //reinit output stream
            baos1 = new ByteArrayOutputStream();
            baos_first = new ByteArrayOutputStream();

            //write delta based on new records
            writer2.writeDelta(baos1);
            writer2.writeReverseDelta(baos_first);
            //apply delta to snapshot generate in 1
            //reader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));

            //write to add rec phase
            stateEngine2.prepareForNextCycle();

            //3) add new recs
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 3, 11 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 7, 13 });
            addRec(stateEngine2, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });

            //add rec to write phase
            stateEngine2.prepareForWrite();

            //reinit output stream
            baos1 = new ByteArrayOutputStream();
            baos_second = new ByteArrayOutputStream();
            //write new delta to output stream
            writer2.writeDelta(baos1);
            writer2.writeReverseDelta(baos_second);
            //apply delta to previous reader
            //reader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));

        }

        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        reader.applyDelta(HollowBlobInput.serial(baos_second.toByteArray()));
        history.deltaOccurred(20001231235959999L);

        reader = new HollowBlobReader(readStateEngine);
        //reader.applyDelta(HollowBlobInput.serial(baos_first.toByteArray()));
        //history.deltaOccurred(19991231235959999L);

        return history;
    }

    private HollowHistory createReverseHistory() throws IOException {
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
