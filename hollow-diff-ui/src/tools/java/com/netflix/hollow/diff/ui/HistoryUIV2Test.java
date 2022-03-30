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

public class HistoryUIV2Test {

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

        serverD.join();
        serverR.join();
    }

    private HollowHistory createHistoryBidirectional() throws IOException {
        ByteArrayOutputStream baos_v2_to_v1, baos_v3_to_v2;
        HollowHistory history;
        HollowWriteStateEngine stateEngine;

        {
            schema = new HollowObjectSchema("TypeA", 2);

            stateEngine = new HollowWriteStateEngine();
            schema.addField("a1", HollowObjectSchema.FieldType.INT);
            schema.addField("a2", HollowObjectSchema.FieldType.INT);

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
            baos_v2_to_v1 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v2 = new ByteArrayOutputStream();

            //write based on new records
            writer.writeSnapshot(baos_v2);
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
            baos_v3_to_v2 = new ByteArrayOutputStream();

            //write delta based on new records
            writer.writeDelta(baos_v2_to_v3);
            writer.writeReverseDelta(baos_v3_to_v2);

            //v4
            stateEngine.prepareForNextCycle();
            stateEngine.addHeaderTag("snapversion", "v4");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 18 });  // 0
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });   // 1
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 19 });  // 2
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });  // 3
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 13 }); // 4
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });  // 5
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 18, 10 }); // 6
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 28, 90 }); // 7

            stateEngine.prepareForWrite();

            //reinit output stream
            ByteArrayOutputStream baos_v4_to_v3 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3_to_v4 = new ByteArrayOutputStream();

            //write snapshot to output stream
            ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
            writer.writeSnapshot(baos_v4);
            writer.writeDelta(baos_v3_to_v4);
            writer.writeReverseDelta(baos_v4_to_v3);


            HollowReadStateEngine fwdReadStateEngine = new HollowReadStateEngine();
            HollowReadStateEngine revReadStateEngine = new HollowReadStateEngine();

            HollowBlobReader fwdReader = new HollowBlobReader(fwdReadStateEngine);
            HollowBlobReader revReader = new HollowBlobReader(revReadStateEngine);

            //load snapshot from output stream to read state engine
            fwdReader.readSnapshot(HollowBlobInput.serial(baos_v2.toByteArray()));
            System.out.println("-- Ordinals on fwdReadStateEngine");
            exploreOrdinals(fwdReadStateEngine);

            revReader.readSnapshot(HollowBlobInput.serial(baos_v2.toByteArray()));
            System.out.println("-- Ordinals on revReadStateEngine");
            exploreOrdinals(revReadStateEngine);

            history = new HollowHistory(fwdReadStateEngine, 2L, MAX_STATES, true);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");
            history.initializeReverseStateEngine(revReadStateEngine, 2L);

            fwdReader.applyDelta(HollowBlobInput.serial(baos_v2_to_v3.toByteArray()));
            exploreOrdinals(fwdReadStateEngine);
            try {
                history.deltaOccurred(3L);
            } catch (IllegalStateException e) {
                throw e;
            }

            revReader.applyDelta(HollowBlobInput.serial(baos_v2_to_v1.toByteArray()));
            exploreOrdinals(revReadStateEngine);
            try {
                history.reverseDeltaOccurred(1L);
            } catch (IllegalStateException e) {
                throw e;
            }

            fwdReader.applyDelta(HollowBlobInput.serial(baos_v3_to_v4.toByteArray()));
            try {
                history.deltaOccurred(4L);
            } catch (IllegalStateException e) {
                throw e;
            }
        }

        return history;
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

    private static void addRec(HollowWriteStateEngine stateEngine, HollowObjectSchema schema, String[] names, int[] vals) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < names.length; i++) {
            rec.setInt(names[i], vals[i]);
        }
        stateEngine.add(schema.getName(), rec);
    }

    private HollowHistory createHistoryD() throws IOException {
        ByteArrayOutputStream baos_v2_to_v1, baos_v3_to_v2;
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
            baos_v2_to_v1 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v2 = new ByteArrayOutputStream();

            //write delta based on new records
            writer.writeSnapshot(baos_v2);
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
            baos_v3_to_v2 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3 = new ByteArrayOutputStream();

            //write delta based on new records
            writer.writeSnapshot(baos_v3);
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



            ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
            //write snapshot to output stream
            writer.writeSnapshot(baos_v4);

            readStateEngine = new HollowReadStateEngine();
            reader = new HollowBlobReader(readStateEngine);
            //load snapshot from output stream to read state engine
            reader.readSnapshot(HollowBlobInput.serial(baos_v1.toByteArray()));
            //>>>do not init history with the snapshot
            history = new HollowHistory(readStateEngine, 1L, MAX_STATES);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");
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
