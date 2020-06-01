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
package com.netflix.hollow.tools.filter;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FilteredHollowBlobWriterTest {
    
    private byte[] snapshotData;
    private byte[] deltaData;
    private byte[] removeOnlyDeltaData;
    
    @Before
    public void setUp() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        
        mapper.add(new TypeA(1, "one"));
        mapper.add(new TypeA(2, "two"));
        mapper.add(new TypeA(3, "three"));
        
        mapper.add(new TypeB(1, 1.1f));
        mapper.add(new TypeB(2, 2.2f));
        mapper.add(new TypeB(3, 3.3f));
        
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);
        snapshotData = baos.toByteArray();
        
        writeEngine.prepareForNextCycle();
        
        mapper.add(new TypeA(1, "one"));
        mapper.add(new TypeA(2, "two"));
        mapper.add(new TypeA(3, "four"));
        
        mapper.add(new TypeB(1, 1.1f));
        mapper.add(new TypeB(2, 2.2f));
        mapper.add(new TypeB(3, 4.4f));

        baos.reset();
        writer.writeDelta(baos);
        deltaData = baos.toByteArray();
        
        writeEngine.prepareForNextCycle();
        
        mapper.add(new TypeA(2, "two"));
        mapper.add(new TypeA(3, "four"));
        
        mapper.add(new TypeB(2, 2.2f));
        mapper.add(new TypeB(3, 4.4f));
        
        baos.reset();
        writer.writeDelta(baos);
        removeOnlyDeltaData = baos.toByteArray();
    }
    
    
    @Test
    public void filtersDataFromBlob() throws IOException {
        HollowFilterConfig filterConfig = new HollowFilterConfig(true);
        filterConfig.addType("String");
        filterConfig.addField("TypeA", "value");
        filterConfig.addField("TypeA", "nonexistentField");
        filterConfig.addType("NonexistentType");
        
        FilteredHollowBlobWriter blobWriter = new FilteredHollowBlobWriter(filterConfig);
        ByteArrayOutputStream filteredBlobStream = new ByteArrayOutputStream();
        blobWriter.filterSnapshot(new ByteArrayInputStream(snapshotData), filteredBlobStream);
        
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(HollowBlobInput.serial(filteredBlobStream.toByteArray()));
        
        filteredBlobStream.reset();
        blobWriter.filterDelta(new ByteArrayInputStream(deltaData), filteredBlobStream);
        
        reader.applyDelta(HollowBlobInput.serial(filteredBlobStream.toByteArray()));
        
        Assert.assertEquals(2, readEngine.getSchemas().size());
        Assert.assertEquals(1, ((HollowObjectSchema)readEngine.getSchema("TypeA")).numFields());
        Assert.assertEquals(2, ((HollowObjectSchema)readEngine.getSchema("TypeB")).numFields());
        
        Assert.assertEquals(3, readEngine.getTypeState("TypeA").getPopulatedOrdinals().cardinality());
        Assert.assertEquals(1, new GenericHollowObject(readEngine, "TypeA", 0).getInt("id"));
        Assert.assertEquals(2, new GenericHollowObject(readEngine, "TypeA", 1).getInt("id"));
        Assert.assertEquals(3, new GenericHollowObject(readEngine, "TypeA", 3).getInt("id"));
        
        Assert.assertEquals(3, readEngine.getTypeState("TypeB").getPopulatedOrdinals().cardinality());
        Assert.assertEquals(1, new GenericHollowObject(readEngine, "TypeB", 0).getInt("id"));
        Assert.assertEquals(1.1f, new GenericHollowObject(readEngine, "TypeB", 0).getFloat("value"), 0);
        Assert.assertEquals(2, new GenericHollowObject(readEngine, "TypeB", 1).getInt("id"));
        Assert.assertEquals(2.2f, new GenericHollowObject(readEngine, "TypeB", 1).getFloat("value"), 0);
        Assert.assertEquals(3, new GenericHollowObject(readEngine, "TypeB", 3).getInt("id"));
        Assert.assertEquals(4.4f, new GenericHollowObject(readEngine, "TypeB", 3).getFloat("value"), 0);
        
        filteredBlobStream.reset();
        blobWriter.filterDelta(new ByteArrayInputStream(removeOnlyDeltaData), filteredBlobStream);
        
        reader.applyDelta(HollowBlobInput.serial(filteredBlobStream.toByteArray()));
        
        Assert.assertEquals(2, readEngine.getTypeState("TypeA").getPopulatedOrdinals().cardinality());
        Assert.assertEquals(2, readEngine.getTypeState("TypeB").getPopulatedOrdinals().cardinality());
    }

    @SuppressWarnings("unused")
    private static class TypeA {
        int id;
        String value;
        
        public TypeA(int id, String value) {
            this.id = id;
            this.value = value;
        }
    }
    
    @SuppressWarnings("unused")
    private static class TypeB {
        int id;
        float value;
        
        public TypeB(int id, float value) {
            this.id = id;
            this.value = value;
        }
    }

}
