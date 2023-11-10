/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.util;

import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_METRIC_CYCLE_START;
import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_PRODUCER_TO_VERSION;
import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowShardLargeType;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowWriteStateCreatorTest {
    
    @Test
    public void recreatesUsingReadEngine() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();

        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.add(new Integer(1));
        writeEngine.addHeaderTag("CopyTag", "copied");
        writeEngine.addHeaderTag(HEADER_TAG_METRIC_CYCLE_START, String.valueOf(System.currentTimeMillis()));
        String toVersion = String.valueOf(System.currentTimeMillis());
        writeEngine.addHeaderTag(HEADER_TAG_PRODUCER_TO_VERSION, toVersion);
        
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        String cycleStartTime = readEngine.getHeaderTag(HEADER_TAG_METRIC_CYCLE_START);
        String readEngineToVersion = readEngine.getHeaderTag(HEADER_TAG_PRODUCER_TO_VERSION);
        HollowWriteStateEngine recreatedWriteEngine = HollowWriteStateCreator.recreateAndPopulateUsingReadEngine(readEngine);
        assertEquals(cycleStartTime, recreatedWriteEngine.getPreviousHeaderTags().get(HEADER_TAG_METRIC_CYCLE_START));
        assertEquals(readEngineToVersion, recreatedWriteEngine.getPreviousHeaderTags().get(HEADER_TAG_PRODUCER_TO_VERSION));

        HollowReadStateEngine recreatedReadEngine = StateEngineRoundTripper.roundTripSnapshot(recreatedWriteEngine);
        
        assertEquals(HollowChecksum.forStateEngine(readEngine), HollowChecksum.forStateEngine(recreatedReadEngine));
        assertEquals("copied", recreatedReadEngine.getHeaderTag("CopyTag"));
        assertEquals(readEngine.getCurrentRandomizedTag(), recreatedReadEngine.getCurrentRandomizedTag());
    }
    
    @Test
    public void throwsExceptionIfWriteStateIsPopulated() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        
        mapper.add(new Integer(1));
        
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        
        try {
            HollowWriteStateCreator.populateUsingReadEngine(writeEngine, readEngine);
            Assert.fail();
        } catch(IllegalStateException expected) { }
    }
    
    @Test
    public void populatesOnlyPreviouslyExistingFieldsWhenSchemaIsAddedTo() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        
        mapper.add(new Integer(1));
        mapper.add(new Integer(2));
        
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        
        HollowWriteStateEngine repopulatedWriteStateEngine = new HollowWriteStateEngine();
        new HollowObjectMapper(repopulatedWriteStateEngine).initializeTypeState(IntegerWithMoreThanOneField.class);
        
        HollowWriteStateCreator.populateUsingReadEngine(repopulatedWriteStateEngine, readEngine);
        
        repopulatedWriteStateEngine.prepareForNextCycle();
        repopulatedWriteStateEngine.addAllObjectsFromPreviousCycle();
        new HollowObjectMapper(repopulatedWriteStateEngine).add(new IntegerWithMoreThanOneField(3));
        HollowReadStateEngine recreatedReadEngine = StateEngineRoundTripper.roundTripSnapshot(repopulatedWriteStateEngine);
        
        GenericHollowObject one = new GenericHollowObject(recreatedReadEngine, "Integer", 0);
        assertEquals(1, one.getInt("value"));
        Assert.assertNull(one.getString("anotherValue"));
        
        GenericHollowObject two = new GenericHollowObject(recreatedReadEngine, "Integer", 1);
        assertEquals(2, two.getInt("value"));
        Assert.assertNull(two.getString("anotherValue"));
        
        GenericHollowObject three = new GenericHollowObject(recreatedReadEngine, "Integer", 2);
        assertEquals(3, three.getInt("value"));
        assertEquals("3", three.getString("anotherValue"));
    }
    
    @Test
    public void populatesPreviouslyExistingFieldsWhenSchemaFieldsAreRemoved() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        
        mapper.add(new IntegerWithMoreThanOneField(1));
        mapper.add(new IntegerWithMoreThanOneField(2));
        
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        
        HollowWriteStateEngine repopulatedWriteStateEngine = new HollowWriteStateEngine();
        new HollowObjectMapper(repopulatedWriteStateEngine).initializeTypeState(Integer.class);
        
        HollowWriteStateCreator.populateUsingReadEngine(repopulatedWriteStateEngine, readEngine);
        
        repopulatedWriteStateEngine.prepareForNextCycle();
        repopulatedWriteStateEngine.addAllObjectsFromPreviousCycle();
        new HollowObjectMapper(repopulatedWriteStateEngine).add(new Integer(3));
        HollowReadStateEngine recreatedReadEngine = StateEngineRoundTripper.roundTripSnapshot(repopulatedWriteStateEngine);

        HollowObjectSchema schema = (HollowObjectSchema)recreatedReadEngine.getSchema("Integer");
        
        assertEquals(1, schema.numFields());
        assertEquals("value", schema.getFieldName(0));
        
        GenericHollowObject one = new GenericHollowObject(recreatedReadEngine, "Integer", 0);
        assertEquals(1, one.getInt("value"));
        
        GenericHollowObject two = new GenericHollowObject(recreatedReadEngine, "Integer", 1);
        assertEquals(2, two.getInt("value"));
        
        GenericHollowObject three = new GenericHollowObject(recreatedReadEngine, "Integer", 2);
        assertEquals(3, three.getInt("value"));
    }
    
    @Test
    public void repopulationFailsIfShardsAreIncorrectlyPreconfigured() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        
        mapper.add(new Integer(1));
        mapper.add(new Integer(2));
        
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);

        HollowWriteStateEngine repopulatedWriteStateEngine = new HollowWriteStateEngine();
        new HollowObjectMapper(repopulatedWriteStateEngine).initializeTypeState(IntegerWithWrongShardConfiguration.class);
        
        try {
            HollowWriteStateCreator.populateUsingReadEngine(repopulatedWriteStateEngine, readEngine);
            Assert.fail();
        } catch(Exception expected) { }
    }

    @Test
    public void testReadSchemaFileIntoWriteState() throws Exception {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        assertEquals("Should have no type states", 0, engine.getOrderedTypeStates().size());
        HollowWriteStateCreator.readSchemaFileIntoWriteState("schema1.txt", engine);
        assertEquals("Should now have types", 2, engine.getOrderedTypeStates().size());
    }
    
    @SuppressWarnings("unused")
    @HollowTypeName(name="Integer")
    private static class IntegerWithMoreThanOneField {
        private final int value;
        @HollowInline private final String anotherValue; 
        
        public IntegerWithMoreThanOneField(int value) {
            this.value = value;
            this.anotherValue = String.valueOf(value);
        }
    }
    
    @SuppressWarnings("unused")
    @HollowTypeName(name="Integer")
    @HollowShardLargeType(numShards=4)
    private static class IntegerWithWrongShardConfiguration {
        private int value;
    }
}
