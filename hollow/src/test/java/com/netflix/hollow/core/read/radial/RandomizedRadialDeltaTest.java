/*
 *  Copyright 2020 Netflix, Inc.
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
package com.netflix.hollow.core.read.radial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;

public class RandomizedRadialDeltaTest {
    
    private Random rand = new Random();
    
    BitSet populatedInteger = initSet();
    BitSet populatedString = initSet();
    BitSet populatedListOfInteger = initSet();
    BitSet populatedSetOfString = initSet();
    BitSet populatedMapOfIntegerToString = initSet();
    
    HollowWriteStateEngine writeEngine;
    HollowObjectSchema intSchema;
    HollowObjectSchema strSchema;
    HollowListSchema listSchema;
    HollowSetSchema setSchema;
    HollowMapSchema mapSchema;

    @Before
    public void setUp() {
        /// initialize some random state which we will later mutate
        populatedInteger = initSet();
        populatedString = initSet();
        populatedListOfInteger = initSet();
        populatedSetOfString = initSet();
        populatedMapOfIntegerToString = initSet();
        
        writeEngine = new HollowWriteStateEngine();

        /// create the schemas
        intSchema = new HollowObjectSchema("Integer", 1);
        intSchema.addField("value", FieldType.INT);
        writeEngine.addTypeState(new HollowObjectTypeWriteState(intSchema));
        
        strSchema = new HollowObjectSchema("String", 1);
        strSchema.addField("value", FieldType.STRING);
        writeEngine.addTypeState(new HollowObjectTypeWriteState(strSchema));

        listSchema = new HollowListSchema("ListOfInteger", "Integer");
        writeEngine.addTypeState(new HollowListTypeWriteState(listSchema));

        setSchema = new HollowSetSchema("SetOfString", "String", "value");
        writeEngine.addTypeState(new HollowSetTypeWriteState(setSchema));

        mapSchema = new HollowMapSchema("MapOfIntegerToString", "Integer", "String", "value");
        writeEngine.addTypeState(new HollowMapTypeWriteState(mapSchema));
    }
    
    @Test
    public void testRadialDelta() throws IOException {
        populateWriteEngine();
        
        writeEngine.prepareForWrite();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(baos);
        byte[] snapshot = baos.toByteArray();

        HollowReadStateEngine deltaEngine = new HollowReadStateEngine();
        HollowReadStateEngine radialEngine = new HollowReadStateEngine();

        new HollowBlobReader(deltaEngine).readSnapshot(new ByteArrayInputStream(snapshot));

        writeEngine.markHubState();
        
        for(int i=0;i<100;i++) {
            /// randomly mutate our state, then populate the data in the next cycle
            writeEngine.prepareForNextCycle();
            mutate();
            populateWriteEngine();
            writeEngine.prepareForWrite();
            
            /// write and consume a delta in our delta state engine
            baos.reset();
            new HollowBlobWriter(writeEngine).writeDelta(baos);
            byte[] delta = baos.toByteArray();
            new HollowBlobReader(deltaEngine).applyDelta(new ByteArrayInputStream(delta));
            
            /// write a radial delta, then consume it in a state engine with a freshly loaded hub state.
            baos.reset();
            new HollowBlobWriter(writeEngine).writeRadialDelta(baos);
            byte[] radialDelta = baos.toByteArray();
            radialEngine = new HollowReadStateEngine();
            new HollowBlobReader(radialEngine).readSnapshot(new ByteArrayInputStream(snapshot));
            new HollowBlobReader(radialEngine).applyRadialDelta(new ByteArrayInputStream(radialDelta));
            
            Assert.assertEquals(HollowChecksum.forStateEngine(deltaEngine), HollowChecksum.forStateEngine(radialEngine));
            
            /// randomly swap the delta engine with the current radial engine
            if(rand.nextInt(5) == 0)
                deltaEngine = radialEngine;
            
            System.out.println(radialDelta.length + "/" + snapshot.length);
        }
        
    }
    
    private void mutate() {
        mutate(populatedInteger);
        mutate(populatedListOfInteger);
        mutate(populatedString);
        mutate(populatedSetOfString);
        mutate(populatedMapOfIntegerToString);
    }
    
    private void mutate(BitSet values) {
        int numMutations = Math.abs(rand.nextInt(20)) + 1;
        
        for(int i=0;i<numMutations;i++) {
            int position = Math.abs(rand.nextInt(11000));
            if(values.get(position))
                values.clear(position);
            else
                values.set(position);
        }
    }
    
    
    private void populateWriteEngine() {
        HollowObjectWriteRecord intRec = new HollowObjectWriteRecord(intSchema);
        
        int val = populatedInteger.nextSetBit(0);
        while(val != -1) {
            intRec.reset();
            intRec.setInt("value", val);
            writeEngine.add("Integer", intRec);
            val = populatedInteger.nextSetBit(val+1);
        }

        HollowObjectWriteRecord strRec = new HollowObjectWriteRecord(strSchema);
        
        val = populatedString.nextSetBit(0);
        while(val != -1) {
            strRec.reset();
            strRec.setString("value", String.valueOf(val));
            writeEngine.add("String", strRec);
            val = populatedString.nextSetBit(val+1);
        }
        
        HollowListWriteRecord listRec = new HollowListWriteRecord();
        
        val = populatedListOfInteger.nextSetBit(0);
        while(val != -1) {
            listRec.reset();
            
            for(int i=0;i<3;i++) {
                intRec.reset();
                intRec.setInt("value", val+100000+i);
                int ordinal = writeEngine.add("Integer", intRec);
                listRec.addElement(ordinal);
            }
            
            writeEngine.add("ListOfInteger", listRec);
            val = populatedListOfInteger.nextSetBit(val+1);
        }
        
        HollowSetWriteRecord setRec = new HollowSetWriteRecord();
        
        val = populatedSetOfString.nextSetBit(0);
        while(val != -1) {
            setRec.reset();
            
            for(int i=0;i<3;i++) {
                strRec.reset();
                strRec.setString("value", String.valueOf(val+100000+i));
                int ordinal = writeEngine.add("String", strRec);
                setRec.addElement(ordinal);
            }

            writeEngine.add("SetOfString", setRec);
            val = populatedSetOfString.nextSetBit(val+1);
        }
        
        HollowMapWriteRecord mapRec = new HollowMapWriteRecord();
        
        val = populatedMapOfIntegerToString.nextSetBit(0);
        while(val != -1) {
            mapRec.reset();
            
            for(int i=0;i<3;i++) {
                intRec.reset();
                strRec.reset();
                intRec.setInt("value", val+200000+i);
                strRec.setString("value", String.valueOf(val+200000+i));
                int keyOrdinal = writeEngine.add("Integer", intRec);
                int valOrdinal = writeEngine.add("String", strRec);
                mapRec.addEntry(keyOrdinal, valOrdinal);
            }
            
            writeEngine.add("MapOfIntegerToString", mapRec);
            val = populatedMapOfIntegerToString.nextSetBit(val+1);
        }
    }
    
    private BitSet initSet() {
        int numValues = 10000 + rand.nextInt(1000);
        
        BitSet initValues = new BitSet(numValues);
        
        for(int i=0;i<numValues;i++) {
            if(rand.nextBoolean())
                initValues.set(i);
        }
        
        return initValues;
    }

}
