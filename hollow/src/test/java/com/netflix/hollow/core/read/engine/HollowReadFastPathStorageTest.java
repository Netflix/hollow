package com.netflix.hollow.core.read.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.encoding.ContiguousFixedLengthData;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class HollowReadFastPathStorageTest extends AbstractStateEngineTest {
    @Override
    protected void initializeTypeStates() {
        new HollowObjectMapper(writeStateEngine).initializeTypeState(Record.class);
    }

    @Test
    public void storageChoiceSurvivesFilteringDeltasReshardingAndHistory() throws Exception {
        readFilter = new HollowFilterConfig(true);
        readFilter.addField("Record", "text");
        populate(0);
        roundTripSnapshot();
        assertEquals(-1, ((com.netflix.hollow.core.schema.HollowObjectSchema)
                readStateEngine.getTypeState("Record").getSchema()).getPosition("text"));
        assertEngineStorage();
        populate(10);
        roundTripDelta();
        assertEngineStorage();
        for(HollowTypeReadState state : readStateEngine.getTypeStates()) {
            int shards = state.getShardsVolatile().getShards().length;
            HollowTypeReshardingStrategy strategy = HollowTypeReshardingStrategy.getInstance(state);
            strategy.reshard(state, shards, shards * 2);
            assertStorage(state, shardReadFastPaths && !recycling);
            strategy.reshard(state, shards * 2, shards);
            assertStorage(state, shardReadFastPaths && !recycling);

            // Historical states deliberately use a wasteful recycler, but retain the opt-in.
            Class<?> creatorClass = Class.forName(state.getClass().getName().replace("TypeReadState", "DeltaHistoricalStateCreator"));
            Object creator = creatorClass.getConstructor(state.getClass(), boolean.class).newInstance(state, false);
            creatorClass.getMethod("populateHistory").invoke(creator);
            Method create = creatorClass.getMethod("createHistoricalTypeReadState");
            assertStorage((HollowTypeReadState)create.invoke(creator), shardReadFastPaths);
        }
    }

    private void populate(int start) {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        for(int i = start; i < start + 8; i++)
            mapper.add(new Record(i));
    }

    private void assertEngineStorage() throws IllegalAccessException {
        for(HollowTypeReadState state : readStateEngine.getTypeStates())
            assertStorage(state, shardReadFastPaths && !recycling);
    }

    private void assertStorage(HollowTypeReadState state, boolean contiguous) throws IllegalAccessException {
        for(HollowTypeReadStateShard shard : state.getShardsVolatile().getShards()) {
            HollowTypeDataElements data = shard.getDataElements();
            assertEquals(shardReadFastPaths, data.useShardReadFastPaths);
            for(Field field : data.getClass().getDeclaredFields()) {
                if(field.getType() == FixedLengthData.class) {
                    field.setAccessible(true);
                    Object storage = field.get(data);
                    assertTrue(state.getSchema().getName() + "." + field.getName(), contiguous
                            ? storage instanceof ContiguousFixedLengthData : storage instanceof FixedLengthElementArray);
                }
            }
        }
    }

    private static class Record {
        int value;
        String text;
        List<Integer> list;
        Set<Integer> set;
        Map<Integer, Integer> map;

        Record(int value) {
            this.value = value;
            this.text = "value " + value;
            this.list = Arrays.asList(value, value + 1);
            this.set = new HashSet<>(list);
            this.map = Collections.singletonMap(value, value + 1);
        }
    }
}
