package com.netflix.hollow.diffview;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.test.consumer.TestBlob;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class FakeHollowHistoryGenerator {
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
