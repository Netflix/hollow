package com.netflix.hollow.core.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowShardLargeType;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.util.Arrays;
import org.junit.Test;

public class HollowObjectTypeWriteStateTest {

    @Test
    public void testCalcMaxShardOrdinal() {
        HollowObjectSchema testSchema = new HollowObjectSchema("Test", 1);
        testSchema.addField("test1", HollowObjectSchema.FieldType.INT);
        HollowObjectTypeWriteState testState = new HollowObjectTypeWriteState(testSchema);

        assertTrue(Arrays.equals(new int[] {-1, -1, -1, -1}, testState.calcMaxShardOrdinal(-1, 4)));
        assertTrue(Arrays.equals(new int[] {0, -1, -1, -1}, testState.calcMaxShardOrdinal(0, 4)));
        assertTrue(Arrays.equals(new int[] {0, 0, -1, -1}, testState.calcMaxShardOrdinal(1, 4)));
        assertTrue(Arrays.equals(new int[] {0, 0, 0, 0}, testState.calcMaxShardOrdinal(3, 4)));
        assertTrue(Arrays.equals(new int[] {1, 1, 1, 1}, testState.calcMaxShardOrdinal(7, 4)));
    }
}
