package com.netflix.hollow.diff.ui;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.tools.history.HollowHistory;
import java.io.ByteArrayOutputStream;
import org.junit.Test;

public class Buggy {
    private static final String CUSTOM_VERSION_TAG = "myVersion";

    private HollowObjectSchema schema;
    private HollowHistory historyFwd;
    private HollowHistory historyRev;

    @Test
    public void startServerOnPorts7777And7778() throws Exception {
        initFwdAndRevHistory();

        HollowHistoryUIServer serverFwd = new HollowHistoryUIServer(historyFwd, 7777);
        serverFwd.start();

        HollowHistoryUIServer serverRev = new HollowHistoryUIServer(historyRev, 7778);
        serverRev.start();

        serverFwd.join();
    }

    private void initFwdAndRevHistory() throws Exception {
        HollowReadStateEngine readStateEngineFwd, readStateEngineRev;
        HollowBlobReader readerFwd, readerRev;
        HollowWriteStateEngine stateEngine;

        // initialize scheam and attach to write state engine
        schema = new HollowObjectSchema("TypeA", 2);
        stateEngine = new HollowWriteStateEngine();
        schema.addField("a1", FieldType.INT);
        schema.addField("a2", FieldType.INT);
        stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));

        // v1
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v1");
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 4 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 5 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });

        // write snapshot, prep for next cycle
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v1 = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        writer.writeSnapshot(baos_v1);
        stateEngine.prepareForNextCycle();

        // v2
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v2");
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 8 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 9 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });

        // write blobs, prep for next cycle
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v1_to_v2 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v2_to_v1 = new ByteArrayOutputStream();
        writer.writeDelta(baos_v1_to_v2);
        writer.writeReverseDelta(baos_v2_to_v1);
        stateEngine.prepareForNextCycle();

        // v3
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v3");
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 11 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 13 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });

        // write blobs, prep for next cycle
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v2_to_v3 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v3_to_v2 = new ByteArrayOutputStream();
        writer.writeDelta(baos_v2_to_v3);
        writer.writeReverseDelta(baos_v3_to_v2);
        stateEngine.prepareForNextCycle();

        // v4
        stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v4");
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 18 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 19 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 13 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 18, 10 });
        addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 28, 90 });

        // write blobs, prep for next cycle
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos_v3_to_v4 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v4_to_v3 = new ByteArrayOutputStream();
        writer.writeDelta(baos_v3_to_v4);
        writer.writeReverseDelta(baos_v4_to_v3);
        ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v4);

        // Build history in fwd
        readStateEngineFwd = new HollowReadStateEngine();
        readerFwd = new HollowBlobReader(readStateEngineFwd);
        readerFwd.readSnapshot(HollowBlobInput.serial(baos_v1.toByteArray()));
        historyFwd = new HollowHistory(readStateEngineFwd, 1l, 10);
        historyFwd.getKeyIndex().addTypeIndex("TypeA", "a1");
        readerFwd.applyDelta(HollowBlobInput.serial(baos_v1_to_v2.toByteArray()));
        historyFwd.deltaOccurred(2l);
        readerFwd.applyDelta(HollowBlobInput.serial(baos_v2_to_v3.toByteArray()));
        historyFwd.deltaOccurred(3l);
        readerFwd.applyDelta(HollowBlobInput.serial(baos_v3_to_v4.toByteArray()));
        historyFwd.deltaOccurred(4l);


        // Build history in rev
        readStateEngineRev = new HollowReadStateEngine();
        readerRev = new HollowBlobReader(readStateEngineRev);
        readerRev.readSnapshot(HollowBlobInput.serial(baos_v4.toByteArray()));
        historyRev = new HollowHistory(readStateEngineFwd, readStateEngineRev, 4l, 4l, 10, true);
        historyRev.getKeyIndex().addTypeIndex("TypeA", "a1");
        readerRev.applyDelta(HollowBlobInput.serial(baos_v4_to_v3.toByteArray()));
        historyRev.reverseDeltaOccurred(3l);
        readerRev.applyDelta(HollowBlobInput.serial(baos_v3_to_v2.toByteArray()));
        historyRev.reverseDeltaOccurred(2l);
        readerRev.applyDelta(HollowBlobInput.serial(baos_v2_to_v1.toByteArray()));
        historyRev.reverseDeltaOccurred(1l);
    }

    private static void addRec(HollowWriteStateEngine stateEngine, HollowObjectSchema schema, String[] names, int[] vals) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < names.length; i++) {
            rec.setInt(names[i], vals[i]);
        }
        stateEngine.add(schema.getName(), rec);
    }


}