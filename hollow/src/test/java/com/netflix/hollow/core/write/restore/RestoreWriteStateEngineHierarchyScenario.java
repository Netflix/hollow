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
package com.netflix.hollow.core.write.restore;

import com.netflix.hollow.api.objects.generic.GenericHollowList;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("unused")
public class RestoreWriteStateEngineHierarchyScenario {

    @HollowTypeName(name="TypeA")
    private static class TypeA1 {
        int id;
        List<TypeB1> b = Collections.singletonList(new TypeB1());
    }
    
    @HollowTypeName(name="TypeB")
    private static class TypeB1 {
        String val;
        float removedData;
    }
    
    @HollowTypeName(name="TypeA")
    private static class TypeA2 {
        int id;
        int idEcho;
        List<TypeB2> b = Collections.singletonList(new TypeB2());
    }
    
    @HollowTypeName(name="TypeB")
    private static class TypeB2 {
        String val;
        int idEcho;
    }
    
    @Test
    public void testAddition() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        
        TypeA1 a11 = new TypeA1();
        a11.id = 1;
        a11.b.get(0).val = "A";
        a11.b.get(0).removedData = 1.0f;
        mapper.add(a11);
        
        TypeA1 a12 = new TypeA1();
        a12.id = 2;
        a12.b.get(0).val = "B";
        a12.b.get(0).removedData = 2.0f;
        mapper.add(a12);
        
        TypeA1 a13 = new TypeA1();
        a13.id = 3;
        a13.b.get(0).val = "A";
        a13.b.get(0).removedData = 1.0f;
        mapper.add(a13);
        
        HollowReadStateEngine readStateEngine = StateEngineRoundTripper.roundTripSnapshot(writeStateEngine);
        
        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
        
        writeStateEngine = new HollowWriteStateEngine();
        mapper = new HollowObjectMapper(writeStateEngine);
        mapper.initializeTypeState(TypeA2.class);
        mapper.initializeTypeState(TypeB2.class);
        writeStateEngine.restoreFrom(readStateEngine);
        
        TypeA2 a22 = new TypeA2();
        a22.id = 2;
        a22.idEcho = 2;
        a22.b.get(0).val = "B";
        a22.b.get(0).idEcho = 2;
        mapper.add(a22);

        TypeA2 a23 = new TypeA2();
        a23.id = 3;
        a23.idEcho = 3;
        a23.b.get(0).val = "B";
        a23.b.get(0).idEcho = 3;
        mapper.add(a23);

        TypeA2 a21 = new TypeA2();
        a21.id = 1;
        a21.idEcho = 1;
        a21.b.get(0).val = "A";
        a21.b.get(0).idEcho = 1;
        mapper.add(a21);

        ///reset after restore
        writeStateEngine.resetToLastPrepareForNextCycle();
        
        a22 = new TypeA2();
        a22.id = 2;
        a22.idEcho = 2;
        a22.b.get(0).val = "B";
        a22.b.get(0).idEcho = 2;
        mapper.add(a22);

        a23 = new TypeA2();
        a23.id = 3;
        a23.idEcho = 3;
        a23.b.get(0).val = "A";
        a23.b.get(0).idEcho = 3;
        mapper.add(a23);

        a21 = new TypeA2();
        a21.id = 1;
        a21.idEcho = 1;
        a21.b.get(0).val = "A";
        a21.b.get(0).idEcho = 1;
        mapper.add(a21);

        writeStateEngine.prepareForWrite();
        HollowBlobWriter writer = new HollowBlobWriter(writeStateEngine);
        
        ByteArrayOutputStream delta = new ByteArrayOutputStream();
        ByteArrayOutputStream reverseDelta = new ByteArrayOutputStream();
        ByteArrayOutputStream snapshot = new ByteArrayOutputStream();
        
        writer.writeDelta(delta);
        writer.writeReverseDelta(reverseDelta);
        writer.writeSnapshot(snapshot);
        
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        reader.applyDelta(HollowBlobInput.serial(delta.toByteArray()));

        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
        
        reader.applyDelta(HollowBlobInput.serial(reverseDelta.toByteArray()));

        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
        
        readStateEngine = new HollowReadStateEngine();
        reader = new HollowBlobReader(readStateEngine);
        reader.readSnapshot(HollowBlobInput.serial(snapshot.toByteArray()));

        assertA(readStateEngine, 1, "A", true);
        assertA(readStateEngine, 2, "B", true);
        assertA(readStateEngine, 3, "A", true);
        
        reader.applyDelta(HollowBlobInput.serial(reverseDelta.toByteArray()));
        
        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
        
        reader.applyDelta(HollowBlobInput.serial(delta.toByteArray()));

        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
    }
    
    @Test
    public void testRemoval() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        TypeA2 a22 = new TypeA2();
        a22.id = 2;
        a22.idEcho = 2;
        a22.b.get(0).val = "B";
        a22.b.get(0).idEcho = 2;
        mapper.add(a22);

        TypeA2 a23 = new TypeA2();
        a23.id = 3;
        a23.idEcho = 3;
        a23.b.get(0).val = "A";
        a23.b.get(0).idEcho = 3;
        mapper.add(a23);

        TypeA2 a21 = new TypeA2();
        a21.id = 1;
        a21.idEcho = 1;
        a21.b.get(0).val = "A";
        a21.b.get(0).idEcho = 1;
        mapper.add(a21);
        
        HollowReadStateEngine readStateEngine = StateEngineRoundTripper.roundTripSnapshot(writeStateEngine);

        assertA(readStateEngine, 1, "A", true);
        assertA(readStateEngine, 2, "B", true);
        assertA(readStateEngine, 3, "A", true);
        
        writeStateEngine = new HollowWriteStateEngine();
        mapper = new HollowObjectMapper(writeStateEngine);
        mapper.initializeTypeState(TypeA1.class);
        mapper.initializeTypeState(TypeB1.class);
        writeStateEngine.restoreFrom(readStateEngine);
        
        TypeA1 a11 = new TypeA1();
        a11.id = 1;
        a11.b.get(0).val = "A";
        a11.b.get(0).removedData = 1.0f;
        mapper.add(a11);
        
        TypeA1 a12 = new TypeA1();
        a12.id = 2;
        a12.b.get(0).val = "B";
        a12.b.get(0).removedData = 2.0f;
        mapper.add(a12);
        
        TypeA1 a13 = new TypeA1();
        a13.id = 3;
        a13.b.get(0).val = "A";
        a13.b.get(0).removedData = 3.0f;
        mapper.add(a13);
        
        writeStateEngine.prepareForWrite();
        writeStateEngine.resetToLastPrepareForNextCycle();

        a11 = new TypeA1();
        a11.id = 1;
        a11.b.get(0).val = "A";
        a11.b.get(0).removedData = 1.0f;
        mapper.add(a11);
        
        a12 = new TypeA1();
        a12.id = 2;
        a12.b.get(0).val = "B";
        a12.b.get(0).removedData = 2.0f;
        mapper.add(a12);
        
        a13 = new TypeA1();
        a13.id = 3;
        a13.b.get(0).val = "A";
        a13.b.get(0).removedData = 1.0f;
        mapper.add(a13);

        //writeStateEngine.prepareForWrite();
        HollowBlobWriter writer = new HollowBlobWriter(writeStateEngine);
        
        ByteArrayOutputStream delta = new ByteArrayOutputStream();
        ByteArrayOutputStream reverseDelta = new ByteArrayOutputStream();
        ByteArrayOutputStream snapshot = new ByteArrayOutputStream();
        
        writer.writeDelta(delta);
        writer.writeReverseDelta(reverseDelta);
        writer.writeSnapshot(snapshot);
        
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        reader.applyDelta(HollowBlobInput.serial(delta.toByteArray()));

        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
        
        reader.applyDelta(HollowBlobInput.serial(reverseDelta.toByteArray()));

        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
        
        readStateEngine = new HollowReadStateEngine();
        reader = new HollowBlobReader(readStateEngine);
        reader.readSnapshot(HollowBlobInput.serial(snapshot.toByteArray()));

        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
        
        reader.applyDelta(HollowBlobInput.serial(reverseDelta.toByteArray()));
        
        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
        
        reader.applyDelta(HollowBlobInput.serial(delta.toByteArray()));

        assertA(readStateEngine, 1, "A", false);
        assertA(readStateEngine, 2, "B", false);
        assertA(readStateEngine, 3, "A", false);
    }
    
    private void assertA(HollowReadStateEngine stateEngine, int id, String val, boolean verifyEchoes) {
        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(stateEngine, "TypeA", "id");
        int ordinal = idx.getMatchingOrdinal(id);
        
        GenericHollowObject a = (GenericHollowObject) GenericHollowRecordHelper.instantiate(stateEngine, "TypeA", ordinal);

        GenericHollowList bList = (GenericHollowList)a.getReferencedGenericRecord("b");
        
        GenericHollowObject b = (GenericHollowObject)bList.get(0);

        GenericHollowObject str = (GenericHollowObject)b.getReferencedGenericRecord("val");
        Assert.assertEquals(val, str.getString("value"));
        
        if(verifyEchoes) {
            Assert.assertEquals(id, a.getInt("idEcho"));
            Assert.assertEquals(id, b.getInt("idEcho"));
        }
    }
    

}
