package com.netflix.hollow.diff.ui;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.tools.history.HollowHistory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Test;

/**
 * A tool to create a simple delta chain with fake data and spin up history UIs
 * that build history in (a) purely fwd direction, and (b) both fwd and reverse
 * directions simultaneously. This is not run as a part of the test suite.
 *
 * Ordinal maps for data used in this test-
 *
 *
 * V0
 *
 * 0: 3, 13
 * 1: 4, 44
 * 2: 15, 150
 * 3: 16, 160
 *
 *
 * V1
 *
 * 4: 1, 1
 * 5: 2, 2
 * 6: 3, 3
 * 7: 4, 4
 * 8: 5, 5
 * 9: 6, 6
 *
 *
 * V2
 *
 * 0: 2, 7
 * 1: 5, 8
 * 2: 7, 9
 * 3: 8, 10
 * 6: 3, 3
 * 9: 6, 6
 *
 *
 * V3
 *
 * 0: 2, 7
 * 3: 8, 10
 * 4: 1, 1
 * 5: 3, 11
 * 7: 6, 12
 * 8: 7, 13
 *
 *
 * V4
 * 0: 2, 7
 * 1: 1, 18
 * 2: 3, 19
 * 3: 8, 10
 * 6: 15, 13
 * 7: 6, 12
 * 9: 18, 10
 * 10: 28, 90
 *
 */
public class HistoryUITest {

    private static final String CUSTOM_VERSION_TAG = "myVersion";
    private final int MAX_STATES = 10;
    private HollowObjectSchema schema;

    @Test
    public void startServerOnPorts7777And7778() throws Exception {

        HollowHistory historyD = createHistoryD();
        HollowHistoryUIServer serverD = new HollowHistoryUIServer(historyD, 7777);
        serverD.start();

        HollowHistory historyR = createHistoryBidirectional();
        HollowHistoryUIServer serverR = new HollowHistoryUIServer(historyR, 7778);
        serverR.start();

        // optionally, test dropping the oldest state
        // historyR.removeHistoricalStates(1);

        serverD.join();
        serverR.join();
    }

    private HollowHistory createHistoryBidirectional() throws IOException {
        HollowHistory history;
        HollowWriteStateEngine stateEngine;

        {
            schema = new HollowObjectSchema("TypeA", 2);
            stateEngine = new HollowWriteStateEngine();
            schema.addField("a1", HollowObjectSchema.FieldType.INT);
            schema.addField("a2", HollowObjectSchema.FieldType.INT);
            stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));

            // v0
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v0");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 13 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 44 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 150 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 16, 160 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v0 = new ByteArrayOutputStream();
            HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
            writer.writeSnapshot(baos_v0);
            stateEngine.prepareForNextCycle();

            // v1
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v1");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 4 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 5 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v1 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v0_to_v1 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1_to_v0 = new ByteArrayOutputStream();
            writer = new HollowBlobWriter(stateEngine);
            writer.writeSnapshot(baos_v1);
            writer.writeDelta(baos_v0_to_v1);
            writer.writeReverseDelta(baos_v1_to_v0);
            stateEngine.prepareForNextCycle();

            // v2
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v2");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 8 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 9 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v2 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1_to_v2 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v2_to_v1 = new ByteArrayOutputStream();
            writer.writeSnapshot(baos_v2);
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
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v2_to_v3 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3_to_v2 = new ByteArrayOutputStream();
            writer.writeDelta(baos_v2_to_v3);
            writer.writeReverseDelta(baos_v3_to_v2);

            // v4
            stateEngine.prepareForNextCycle();
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v4");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 18 });  // 0
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });   // 1
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 19 });  // 2
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });  // 3
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 13 }); // 4
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });  // 5
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 18, 10 }); // 6
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 28, 90 }); // 7
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v4_to_v3 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3_to_v4 = new ByteArrayOutputStream();
            writer.writeSnapshot(baos_v4);
            writer.writeDelta(baos_v3_to_v4);
            writer.writeReverseDelta(baos_v4_to_v3);

            // build history bi-directionally
            HollowReadStateEngine fwdReadStateEngine = new HollowReadStateEngine();
            HollowReadStateEngine revReadStateEngine = new HollowReadStateEngine();
            HollowBlobReader fwdReader = new HollowBlobReader(fwdReadStateEngine);
            HollowBlobReader revReader = new HollowBlobReader(revReadStateEngine);
            fwdReader.readSnapshot(HollowBlobInput.serial(baos_v2.toByteArray()));
            System.out.println("Ordinals populated in fwdReadStateEngine: ");
            exploreOrdinals(fwdReadStateEngine);
            revReader.readSnapshot(HollowBlobInput.serial(baos_v2.toByteArray()));
            System.out.println("Ordinals populated in revReadStateEngine (same as fwdReadStateEngine): ");
            exploreOrdinals(revReadStateEngine);
            history = new HollowHistory(fwdReadStateEngine, 2L, MAX_STATES, true);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");
            history.getKeyIndex().indexTypeField("TypeA", "a1");
            history.initializeReverseStateEngine(revReadStateEngine, 2L);

            fwdReader.applyDelta(HollowBlobInput.serial(baos_v2_to_v3.toByteArray()));
            exploreOrdinals(fwdReadStateEngine);
            history.deltaOccurred(3L);

            revReader.applyDelta(HollowBlobInput.serial(baos_v2_to_v1.toByteArray()));
            exploreOrdinals(revReadStateEngine);
            history.reverseDeltaOccurred(1L);

            fwdReader.applyDelta(HollowBlobInput.serial(baos_v3_to_v4.toByteArray()));
            exploreOrdinals(fwdReadStateEngine);
            history.deltaOccurred(4L);

            revReader.applyDelta(HollowBlobInput.serial(baos_v1_to_v0.toByteArray()));
            exploreOrdinals(revReadStateEngine);
            history.reverseDeltaOccurred(0L);
        }

        return history;
    }

    private void exploreOrdinals(HollowReadStateEngine readStateEngine) {
        System.out.println("CUSTOM_VERSION_TAG= " + readStateEngine.getHeaderTags().get(CUSTOM_VERSION_TAG));
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

    private static void addRec(HollowWriteStateEngine stateEngine, HollowObjectSchema schema, String[] names, int[] vals) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < names.length; i++) {
            rec.setInt(names[i], vals[i]);
        }
        stateEngine.add(schema.getName(), rec);
    }

    private HollowHistory createHistoryD() throws IOException {
        HollowHistory history;
        HollowReadStateEngine readStateEngine;
        HollowBlobReader reader;
        HollowWriteStateEngine stateEngine;

        {
            schema = new HollowObjectSchema("TypeA", 2);
            stateEngine = new HollowWriteStateEngine();
            schema.addField("a1", HollowObjectSchema.FieldType.INT);
            schema.addField("a2", HollowObjectSchema.FieldType.INT);

            //attach schema to write state engine
            stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));

            // v0
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v0");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 13 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 44 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 150 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 16, 160 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v0 = new ByteArrayOutputStream();
            HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
            writer.writeSnapshot(baos_v0);
            stateEngine.prepareForNextCycle();

            // v1
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v1");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 4 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 5 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v0_to_v1 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1_to_v0 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1 = new ByteArrayOutputStream();
            writer = new HollowBlobWriter(stateEngine);
            writer.writeSnapshot(baos_v1);
            writer.writeDelta(baos_v0_to_v1);
            writer.writeReverseDelta(baos_v1_to_v0);

            stateEngine.prepareForNextCycle();

            // v2
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v2");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 8 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 9 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v2 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1_to_v2 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v2_to_v1 = new ByteArrayOutputStream();
            writer.writeSnapshot(baos_v2);
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
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v3 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v2_to_v3 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3_to_v2 = new ByteArrayOutputStream();
            writer.writeSnapshot(baos_v3);
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
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3_to_v4 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v4_to_v3 = new ByteArrayOutputStream();
            writer.writeDelta(baos_v3_to_v4);
            writer.writeReverseDelta(baos_v4_to_v3);
            writer.writeSnapshot(baos_v4);

            // Build history
            readStateEngine = new HollowReadStateEngine();
            reader = new HollowBlobReader(readStateEngine);
            reader.readSnapshot(HollowBlobInput.serial(baos_v0.toByteArray()));
            history = new HollowHistory(readStateEngine, 0L, MAX_STATES);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");
            reader.applyDelta(HollowBlobInput.serial(baos_v0_to_v1.toByteArray()));
            history.deltaOccurred(1L);
            reader.applyDelta(HollowBlobInput.serial(baos_v1_to_v2.toByteArray()));
            history.deltaOccurred(2L);
            reader.applyDelta(HollowBlobInput.serial(baos_v2_to_v3.toByteArray()));
            history.deltaOccurred(3L);
            reader.applyDelta(HollowBlobInput.serial(baos_v3_to_v4.toByteArray()));
            history.deltaOccurred(4L);
        }

        return history;
    }
}
