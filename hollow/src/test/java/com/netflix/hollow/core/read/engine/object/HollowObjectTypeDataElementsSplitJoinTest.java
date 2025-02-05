package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Test;

public class HollowObjectTypeDataElementsSplitJoinTest extends AbstractHollowObjectTypeDataElementsSplitJoinTest {

    @Test
    public void simpleRepro() throws IOException {
        HollowObjectTypeReadState typeReadState = populateTypeStateWithRepro();
        assertEquals(1, typeReadState.numShards());

        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 2);
        HollowObjectTypeDataElements[] splitElements = splitter.split();

        HollowObjectTypeDataElementsSplitter splitter0 = new HollowObjectTypeDataElementsSplitter(splitElements[0], 2);
        HollowObjectTypeDataElementsSplitter splitter1 = new HollowObjectTypeDataElementsSplitter(splitElements[1], 2);
        HollowObjectTypeDataElements[] splitElements0 = splitter0.split();
        HollowObjectTypeDataElements[] splitElements1 = splitter1.split();

        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner(new HollowObjectTypeDataElements[] {splitElements0[0], splitElements1[0], splitElements0[1], splitElements1[1]});
        HollowObjectTypeDataElements joinedElements = joiner.join();
        HollowObjectTypeReadState resultTypeReadState = new HollowObjectTypeReadState(typeReadState.getSchema(), joinedElements);

        assertChecksumUnchanged(resultTypeReadState, typeReadState, typeReadState.getPopulatedOrdinals());
    }

    @Test
    public void testSplitThenJoin() throws IOException {
        for (int numRecords=0;numRecords<1*1000;numRecords++) {
            HollowObjectTypeReadState typeReadState = populateTypeStateWith(numRecords);
            assertEquals(1, typeReadState.numShards());
            assertDataUnchanged(typeReadState, numRecords);

            for (int numSplits : new int[]{2}) {  // 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024
                HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter(typeReadState.currentDataElements()[0], numSplits);
                HollowObjectTypeDataElements[] splitElements = splitter.split();
                HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner(splitElements);
                HollowObjectTypeDataElements joinedElements = joiner.join();
                HollowObjectTypeReadState resultTypeReadState = new HollowObjectTypeReadState(typeReadState.getSchema(), joinedElements);

                assertDataUnchanged(resultTypeReadState, numRecords);
                assertChecksumUnchanged(resultTypeReadState, typeReadState, typeReadState.getPopulatedOrdinals());
            }
        }
    }

    private void assertChecksumUnchanged(HollowObjectTypeReadState newTypeState, HollowObjectTypeReadState origTypeState, BitSet populatedOrdinals) {
        HollowChecksum origCksum = new HollowChecksum();
        HollowChecksum newCksum = new HollowChecksum();

        for(int i=0;i<origTypeState.numShards();i++) {
            origTypeState.shardsVolatile.shards[i].applyShardToChecksum(origCksum, origTypeState.getSchema(), populatedOrdinals, i, origTypeState.shardsVolatile.shardNumberMask);
        }

        for(int i=0;i<newTypeState.numShards();i++) {
            newTypeState.shardsVolatile.shards[i].applyShardToChecksum(newCksum, newTypeState.getSchema(), populatedOrdinals, i, newTypeState.shardsVolatile.shardNumberMask);
        }

        assertEquals(newCksum, origCksum);
    }

    @Test
    public void testSplitThenJoinWithFilter() throws IOException {
        int numSplits = 2;
        for (int numRecords=0;numRecords<1*1000;numRecords++) {
            HollowObjectTypeReadState typeReadState = populateTypeStateWithFilter(numRecords);
            assertEquals(1, typeReadState.numShards());
            assertDataUnchanged(typeReadState, numRecords);

            HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter(typeReadState.currentDataElements()[0], numSplits);
            HollowObjectTypeDataElements[] splitElements = splitter.split();
            HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner(splitElements);
            HollowObjectTypeDataElements joinedElements = joiner.join();
            HollowObjectTypeReadState resultTypeReadState = new HollowObjectTypeReadState(typeReadState.getSchema(), joinedElements);

            assertDataUnchanged(resultTypeReadState, numRecords);
            assertChecksumUnchanged(resultTypeReadState, typeReadState, typeReadState.getPopulatedOrdinals());
        }
    }

    @Test
    public void testSplitThenJoinWithEmptyJoin() throws IOException {

        HollowObjectTypeReadState typeReadState = populateTypeStateWith(1);
        assertEquals(1, typeReadState.numShards());

        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 4);
        HollowObjectTypeDataElements[] splitBy4 = splitter.split();
        assertEquals(-1, splitBy4[1].maxOrdinal);
        assertEquals(-1, splitBy4[3].maxOrdinal);

        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner(new HollowObjectTypeDataElements[]{splitBy4[1], splitBy4[3]});
        HollowObjectTypeDataElements joined = joiner.join();

        assertEquals(-1, joined.maxOrdinal);
    }

    @Test
    public void testSplitThenJoinWithNullAndSpecialValues() throws IOException {
        initWriteStateEngine();
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for(int i=0;i<10;i++) {
            rec.reset();
            rec.setLong("longField", i);
            // other fields will be null
            writeStateEngine.add("TestObject", rec);
        }
        for(int i=10;i<20;i++) {
            rec.reset();
            rec.setLong("longField", Long.MIN_VALUE);
            rec.setString("stringField", "");
            rec.setInt("intField", i);
            rec.setDouble("doubleField", Double.NaN);
            writeStateEngine.add("TestObject", rec);
        }

        roundTripSnapshot();
        HollowObjectTypeReadState typeReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
        assertEquals(1, typeReadState.numShards());

        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 4);
        HollowObjectTypeDataElements[] splitBy4 = splitter.split();

        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner(splitBy4);
        HollowObjectTypeDataElements joined = joiner.join();

        HollowObjectTypeReadState joinedTypeReadState = new HollowObjectTypeReadState(typeReadState.getSchema(), joined);
        assertChecksumUnchanged(typeReadState, joinedTypeReadState, typeReadState.getPopulatedOrdinals());
    }
}
