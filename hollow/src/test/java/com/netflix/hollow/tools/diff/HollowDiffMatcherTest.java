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
package com.netflix.hollow.tools.diff;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.LongList;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowDiffMatcherTest {

    private HollowObjectSchema fromSchema;
    private HollowObjectSchema toSchema;
    private HollowObjectSchema subObjectSchema;
    
    private HollowWriteStateEngine fromStateEngine;
    private HollowWriteStateEngine toStateEngine;
    
    @Before
    public void setUp() {
        fromSchema = new HollowObjectSchema("TestObject", 3);
        fromSchema.addField("int", FieldType.INT);
        fromSchema.addField("ref", FieldType.REFERENCE, "TestSubObject");
        fromSchema.addField("str", FieldType.STRING);
        
        toSchema = new HollowObjectSchema("TestObject", 2);
        toSchema.addField("ref", FieldType.REFERENCE, "TestSubObject");
        toSchema.addField("str", FieldType.STRING);
        
        subObjectSchema = new HollowObjectSchema("TestSubObject", 2);
        subObjectSchema.addField("int", FieldType.INT);
        subObjectSchema.addField("double", FieldType.DOUBLE);
        
        fromStateEngine = new HollowWriteStateEngine();
        fromStateEngine.addTypeState(new HollowObjectTypeWriteState(fromSchema));
        fromStateEngine.addTypeState(new HollowObjectTypeWriteState(subObjectSchema));
        
        toStateEngine = new HollowWriteStateEngine();
        toStateEngine.addTypeState(new HollowObjectTypeWriteState(toSchema));
        toStateEngine.addTypeState(new HollowObjectTypeWriteState(subObjectSchema));     
    }
    
    @Test
    public void findsObjectMatches() throws IOException {
        int from1 = addFromRecord(1, "one", 1, 1.1d);
        int from2 = addFromRecord(2, "two", 2, 2.2d);
        int from3 = addFromRecord(3, "three", 3, 3.3d);
        int from4 = addFromRecord(4, "four", 1, 1.1d);
        
        int to4 = addToRecord("four", 4, 4.4d);
        int to3 = addToRecord("three", 3, 3.3d);
        int to2 = addToRecord("two", 2, 2.2d);
        int to1 = addToRecord("one", 1, 1.1d);
        
        HollowObjectTypeReadState fromState = roundTripAndGetTypeState(fromStateEngine);
        HollowObjectTypeReadState toState = roundTripAndGetTypeState(toStateEngine);
        
        HollowDiffMatcher matcher = new HollowDiffMatcher(fromState, toState);
        matcher.addMatchPath("ref.double");
        matcher.addMatchPath("str");
        
        matcher.calculateMatches();
        
        LongList matches = matcher.getMatchedOrdinals();
        IntList fromExtra = matcher.getExtraInFrom();
        IntList toExtra = matcher.getExtraInTo();
        
        Assert.assertEquals((long)from3 << 32 | to3, matches.get(0));
        Assert.assertEquals((long)from2 << 32 | to2, matches.get(1));
        Assert.assertEquals((long)from1 << 32 | to1, matches.get(2));
        Assert.assertEquals(3, matches.size());
        
        Assert.assertEquals(from4, fromExtra.get(0));
        Assert.assertEquals(1, fromExtra.size());
        
        Assert.assertEquals(to4, toExtra.get(0));
        Assert.assertEquals(1, toExtra.size());
    }
    
    @Test
    public void retrievesDisplayStringsForRecords() throws IOException {
        int from1 = addFromRecord(1, "one", 1, 1.1d);
        int from2 = addFromRecord(2, "two", 2, 2.2d);
        int from3 = addFromRecord(3, "three", 3, 3.3d);
        int from4 = addFromRecord(4, "four", 1, 1.1d);
        
        int to4 = addToRecord("four", 4, 4.4d);
        int to3 = addToRecord("three", 3, 3.3d);
        int to2 = addToRecord("two", 2, 2.2d);
        int to1 = addToRecord("one", 1, 1.1d);
        
        HollowObjectTypeReadState fromState = roundTripAndGetTypeState(fromStateEngine);
        HollowObjectTypeReadState toState = roundTripAndGetTypeState(toStateEngine);
        
        HollowDiffMatcher matcher = new HollowDiffMatcher(fromState, toState);
        matcher.addMatchPath("ref.double");
        matcher.addMatchPath("str");
        
        matcher.calculateMatches();
        
        Assert.assertEquals("1.1 one", matcher.getKeyDisplayString(fromState, from1));
        Assert.assertEquals("2.2 two", matcher.getKeyDisplayString(fromState, from2));
        Assert.assertEquals("3.3 three", matcher.getKeyDisplayString(fromState, from3));
        Assert.assertEquals("1.1 four", matcher.getKeyDisplayString(fromState, from4));

        Assert.assertEquals("1.1 one", matcher.getKeyDisplayString(toState, to1));
        Assert.assertEquals("2.2 two", matcher.getKeyDisplayString(toState, to2));
        Assert.assertEquals("3.3 three", matcher.getKeyDisplayString(toState, to3));
        Assert.assertEquals("4.4 four", matcher.getKeyDisplayString(toState, to4));
    }
    
    private HollowObjectTypeReadState roundTripAndGetTypeState(HollowWriteStateEngine stateEngine) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        readStateEngine.addTypeListener("TestObject", new PopulatedOrdinalListener());
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }
    
    private int addFromRecord(int i, String str, int subi, double subd) {
        int subOrdinal = addSubRecord(fromStateEngine, subi, subd);
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(fromSchema);
        rec.setString("str", str);
        rec.setInt("int", i);
        rec.setReference("ref", subOrdinal);
        return fromStateEngine.add("TestObject", rec);
    }
    
    private int  addToRecord(String str, int subi, double subd) {
        int subOrdinal = addSubRecord(toStateEngine, subi, subd);
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(toSchema);
        rec.setString("str", str);
        rec.setReference("ref", subOrdinal);
        return toStateEngine.add("TestObject", rec);
    }
    
    private int addSubRecord(HollowWriteStateEngine stateEngine, int subi, double subd) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(subObjectSchema);
        rec.setInt("int", subi);
        rec.setDouble("double", subd);
        
        return stateEngine.add("TestSubObject", rec);
    }
}
