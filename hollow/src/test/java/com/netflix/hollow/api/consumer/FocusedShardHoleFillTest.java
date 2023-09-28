package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.WriteState;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowShardLargeType;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Assert;
import org.junit.Test;

public class FocusedShardHoleFillTest {
    
    @Test
    public void focusChanges() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer producer = HollowProducer.withPublisher(blobStore).withBlobStager(new HollowInMemoryBlobStager()).withFocusHoleFillInFewestShards(true).build();
        long v1 = producer.runCycle(state -> {
            for(int i=1;i<=36;i++) {
                add(state, "val" + i, i);
            }
        });
        
        HollowConsumer consumer = HollowConsumer.newHollowConsumer().withBlobRetriever(blobStore).withSkipTypeShardUpdateWithNoAdditions().build();
        consumer.triggerRefreshTo(v1);
        
        Assert.assertEquals(4, consumer.getStateEngine().getTypeState("TestRec").numShards());
        Assert.assertEquals(4, consumer.getStateEngine().getTypeState("ListOfTestRec").numShards());
        Assert.assertEquals(4, consumer.getStateEngine().getTypeState("SetOfTestRec").numShards());
        Assert.assertEquals(4, consumer.getStateEngine().getTypeState("MapOfTestRecToTestRec").numShards());
        
        /// remove 4 from S0: 13, 21, 25, 29
        /// remove 5 from S1:  6, 14, 18, 26, 30
        /// remove 2 from S2: 11, 19
        /// remove 1 from S3: 24
        Set<Integer> removeSet = new HashSet<>(Arrays.asList(13, 21, 25, 29, 6, 14, 18, 26, 30, 11, 19, 24));
        
        long v2 = producer.runCycle(state -> {
            for(int i=1;i<=36;i++) {
                if(!removeSet.contains(i))
                    add(state, "val" + i, i);
            }
            
            add(state, "newval37", 37);
        });
        
        consumer.triggerRefreshTo(v2);
        
        removeSet.add(5);
       
        long v3 = producer.runCycle(state -> {
            for(int i=1;i<=36;i++) {
                if(!removeSet.contains(i))
                    add(state, "val" + i, i);
            }

            add(state, "newval37", 37);

            for(int i=1000;i<1005;i++) {
                add(state, "bigval"+i, i);
            }
        });
        
        consumer.triggerRefreshTo(v3);
        
        /// all changes focused in shard 1
        assertRecordOrdinal(consumer,  5, "bigval1000", 1000);
        assertRecordOrdinal(consumer, 13, "bigval1001", 1001);
        assertRecordOrdinal(consumer, 17, "bigval1002", 1002);
        assertRecordOrdinal(consumer, 25, "bigval1003", 1003);
        assertRecordOrdinal(consumer, 29, "bigval1004", 1004);
        
        assertRecordOrdinal(consumer, 32, "val33", 33); // shard1
        assertRecordOrdinal(consumer,  9, "val10", 10); // shard2
        assertRecordOrdinal(consumer, 14, "val15", 15);
        assertRecordOrdinal(consumer, 11, "val12", 12);
        
        long v4 = producer.runCycle(state -> {
            for(int i=1;i<=36;i++) {
                if(!removeSet.contains(i))
                    add(state, "val" + i, i);
            }

            add(state, "newval37", 37);

            for(int i=1000;i<1010;i++) {
                add(state, "bigval"+i, i);
            }
        });
        
        consumer.triggerRefreshTo(v4);
        
        /// all changes focused in shard 0
        assertRecordOrdinal(consumer,  4, "bigval1005", 1005);
        assertRecordOrdinal(consumer, 12, "bigval1006", 1006);
        assertRecordOrdinal(consumer, 20, "bigval1007", 1007);
        assertRecordOrdinal(consumer, 24, "bigval1008", 1008);
        assertRecordOrdinal(consumer, 28, "bigval1009", 1009);
        
        assertRecordOrdinal(consumer, 32, "val33", 33); // shard1
        assertRecordOrdinal(consumer,  9, "val10", 10); // shard2
        assertRecordOrdinal(consumer, 14, "val15", 15);
        assertRecordOrdinal(consumer, 11, "val12", 12);
        
        long v5 = producer.runCycle(state -> {
            for(int i=1;i<=36;i++) {
                if(!removeSet.contains(i))
                    add(state, "val" + i, i);
            }

            add(state, "newval37", 37);

            for(int i=1000;i<1013;i++) {
                add(state, "bigval"+i, i);
            }
        });

        consumer.triggerRefreshTo(v5);

        /// all changes focused in shards 2 and 3
        assertRecordOrdinal(consumer, 10, "bigval1010", 1010);
        assertRecordOrdinal(consumer, 18, "bigval1011", 1011);
        assertRecordOrdinal(consumer, 23, "bigval1012", 1012);
        
        assertRecordOrdinal(consumer, 32, "val33", 33); // shard1
        assertRecordOrdinal(consumer,  9, "val10", 10); // shard2
        assertRecordOrdinal(consumer, 14, "val15", 15);
        assertRecordOrdinal(consumer, 11, "val12", 12);
        
        consumer.triggerRefreshTo(v1);
        
        BitSet ordinals = consumer.getStateEngine().getTypeState("TestRec").getPopulatedOrdinals();
        
        for(int i=0;i<36;i++) {
            Assert.assertTrue(ordinals.get(i));
            assertRecordOrdinal(consumer, i, "val"+(i+1), i+1);
        }
        
        Assert.assertEquals(36, ordinals.cardinality());
    }
    
    private void add(WriteState state, String sVal, int iVal) {
        TestRec rec = new TestRec(sVal, iVal);
        state.add(rec);
        state.add(new ListRec(rec));
        state.add(new SetRec(rec));
        state.add(new MapRec(rec));
    }
    
    private void assertRecordOrdinal(HollowConsumer consumer, int ordinal, String sVal, int iVal) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState)consumer.getStateEngine().getTypeState("TestRec");
        
        Assert.assertEquals(iVal, typeState.readInt(ordinal, typeState.getSchema().getPosition("intVal")));
        Assert.assertEquals(sVal, typeState.readString(ordinal, typeState.getSchema().getPosition("strVal")));

        HollowListTypeReadState listState = (HollowListTypeReadState)consumer.getStateEngine().getTypeState("ListOfTestRec");
        HollowOrdinalIterator iter = listState.ordinalIterator(ordinal);
        Assert.assertEquals(ordinal, iter.next());
        Assert.assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());

        HollowSetTypeReadState setState = (HollowSetTypeReadState)consumer.getStateEngine().getTypeState("SetOfTestRec");
        iter = setState.ordinalIterator(ordinal);
        Assert.assertEquals(ordinal, iter.next());
        Assert.assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());
        
        HollowMapTypeReadState mapState = (HollowMapTypeReadState)consumer.getStateEngine().getTypeState("MapOfTestRecToTestRec");
        HollowMapEntryOrdinalIterator mapIter = mapState.ordinalIterator(ordinal);
        Assert.assertTrue(mapIter.next());
        Assert.assertEquals(ordinal, mapIter.getKey());
        Assert.assertEquals(ordinal, mapIter.getValue());
        Assert.assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());

    }
    
    private static class ListRec {
        @HollowShardLargeType(numShards = 4)
        private final List<TestRec> list;
        
        public ListRec(TestRec rec) {
            this.list = Collections.singletonList(rec);
        }
    }
    
    private static class SetRec {
        @HollowShardLargeType(numShards = 4)
        private final Set<TestRec> set;
        
        public SetRec(TestRec rec) {
            this.set = Collections.singleton(rec);
        }
    }
    
    private static class MapRec {
        @HollowShardLargeType(numShards = 4)
        private final Map<TestRec, TestRec> map;
        
        public MapRec(TestRec rec) {
            this.map = Collections.singletonMap(rec, rec);
        }
    }
    
    @HollowShardLargeType(numShards = 4)
    private static class TestRec {
        @HollowInline
        private final String strVal;
        private final int intVal;
        
        public TestRec(String strVal, int intVal) {
            this.strVal = strVal;
            this.intVal = intVal;
        }
    }
    
}
