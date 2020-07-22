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
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;

public class HistoryUITest {

    @Test
    public void startServerOnPort7777() throws Exception {
        HollowHistory history = createHistory();

        HollowHistoryUIServer server = new HollowHistoryUIServer(history, 7777);
        server.start();
        server.join();
    }


    private HollowWriteStateEngine stateEngine;
    private HollowObjectSchema schema;
    private HollowObjectSchema bSchema;

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
