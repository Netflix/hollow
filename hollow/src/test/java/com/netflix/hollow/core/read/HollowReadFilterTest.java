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
package com.netflix.hollow.core.read;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class HollowReadFilterTest extends AbstractStateEngineTest {

    HollowObjectSchema objSchema;
    HollowListSchema listSchema;
    HollowSetSchema setSchema;
    HollowMapSchema mapSchema;
    HollowObjectSchema elementSchema;

    @Before
    public void setUp() {
        objSchema = new HollowObjectSchema("TestObject", 5);
        objSchema.addField("field1", FieldType.STRING);
        objSchema.addField("field2", FieldType.INT);
        objSchema.addField("field3", FieldType.STRING);
        objSchema.addField("field4", FieldType.DOUBLE);
        objSchema.addField("field5", FieldType.FLOAT);

        listSchema = new HollowListSchema("TestList", "TestElement");
        setSchema = new HollowSetSchema("TestSet", "TestElement");
        mapSchema = new HollowMapSchema("TestMap", "TestElement", "TestElement");

        elementSchema = new HollowObjectSchema("TestElement", 1);
        elementSchema.addField("field", FieldType.INT);

        super.setUp();
    }

    @Test
    public void testIgnoredObject() throws IOException {
        readFilter = new HollowFilterConfig(true);
        readFilter.addType(objSchema.getName());
        runThroughTheMotions();

        Assert.assertNull(readStateEngine.getTypeState(objSchema.getName()));
    }

    @Test
    public void testIgnoredList() throws IOException {
        readFilter = new HollowFilterConfig(true);
        readFilter.addType(listSchema.getName());
        runThroughTheMotions();

        Assert.assertNull(readStateEngine.getTypeState(listSchema.getName()));
    }

    @Test
    public void testIgnoredSet() throws IOException {
        readFilter = new HollowFilterConfig(true);
        readFilter.addType(setSchema.getName());
        runThroughTheMotions();

        Assert.assertNull(readStateEngine.getTypeState(setSchema.getName()));
    }

    @Test
    public void testIgnoredMap() throws IOException {
        readFilter = new HollowFilterConfig(true);
        readFilter.addType(mapSchema.getName());
        runThroughTheMotions();

        Assert.assertNull(readStateEngine.getTypeState(mapSchema.getName()));
    }

    @Test
    public void testMultipleIgnoredTypes() throws Exception {
        readFilter = new HollowFilterConfig(true);
        readFilter.addType(listSchema.getName());
        readFilter.addType(setSchema.getName());
        readFilter.addType(mapSchema.getName());
        runThroughTheMotions();

        Assert.assertNull(readStateEngine.getTypeState(listSchema.getName()));
        Assert.assertNull(readStateEngine.getTypeState(setSchema.getName()));
        Assert.assertNull(readStateEngine.getTypeState(mapSchema.getName()));
    }

    @Test
    public void testExcludedObjectFields() throws Exception {
        readFilter = new HollowFilterConfig(true);
        readFilter.addField(objSchema.getName(), "field2");
        readFilter.addField(objSchema.getName(), "field3");
        readFilter.addField(objSchema.getName(), "field5");
        runThroughTheMotions();

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(objSchema.getName());

        HollowObjectSchema filteredSchema = typeState.getSchema();
        Assert.assertEquals("field1", filteredSchema.getFieldName(0));
        Assert.assertEquals("field4", filteredSchema.getFieldName(1));

        Assert.assertEquals("obj1", typeState.readString(0, 0));
        Assert.assertEquals("OBJECT number two", typeState.readString(1, 0));
        Assert.assertEquals("#3", typeState.readString(2, 0));
        Assert.assertEquals("number four!", typeState.readString(3, 0));

        Assert.assertEquals(1.01D, typeState.readDouble(0, 1), 0);
        Assert.assertEquals(2.02D, typeState.readDouble(1, 1), 0);
        Assert.assertEquals(3.03D, typeState.readDouble(2, 1), 0);
        Assert.assertEquals(4.04D, typeState.readDouble(3, 1), 0);
    }

    @Test
    public void testIncludedObjectFields() throws Exception {
        readFilter = new HollowFilterConfig();
        readFilter.addField(objSchema.getName(), "field2");
        readFilter.addField(objSchema.getName(), "field3");
        readFilter.addField(objSchema.getName(), "field5");
        runThroughTheMotions();

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(objSchema.getName());

        HollowObjectSchema filteredSchema = typeState.getSchema();
        Assert.assertEquals("field2", filteredSchema.getFieldName(0));
        Assert.assertEquals("field3", filteredSchema.getFieldName(1));
        Assert.assertEquals("field5", filteredSchema.getFieldName(2));

        Assert.assertEquals(1000, typeState.readInt(0, 0));
        Assert.assertEquals(2000, typeState.readInt(1, 0));
        Assert.assertEquals(3000, typeState.readInt(2, 0));
        Assert.assertEquals(4000, typeState.readInt(3, 0));

        Assert.assertEquals("ONE", typeState.readString(0, 1));
        Assert.assertEquals("TWO", typeState.readString(1, 1));
        Assert.assertEquals("THREE", typeState.readString(2, 1));
        Assert.assertEquals("FOUR", typeState.readString(3, 1));

        Assert.assertEquals(1.1F, typeState.readFloat(0, 2), 0);
        Assert.assertEquals(2.2F, typeState.readFloat(1, 2), 0);
        Assert.assertEquals(3.3F, typeState.readFloat(2, 2), 0);
        Assert.assertEquals(4.4F, typeState.readFloat(3, 2), 0);
    }


    private void runThroughTheMotions() throws IOException {
        addState1Data();
        roundTripSnapshot();
        addState2Data();
        roundTripDelta();
    }

    private void addState1Data() {
        addTestObject("obj1", 1000, "ONE", 1.01D, 1.1F);
        addTestObject("OBJECT number two", 2000, "TWO", 2.02D, 2.2F);
        addTestObject("#3", 3000, "THREE", 3.03D, 3.3F);

        addTestList(1, 3);
        addTestList(1, 2, 3, 4);
        addTestList(2, 4);

        addTestSet(1, 3);
        addTestSet(1, 2, 3, 4);
        addTestSet(2, 4);

        addTestMap(1, 3);
        addTestMap(1, 2, 3, 4);
        addTestMap(2, 4);

        addTestEntry(0);
        addTestEntry(1);
        addTestEntry(2);
        addTestEntry(3);
        addTestEntry(4);
    }

    private void addState2Data() {
        addTestObject("obj1", 1000, "ONE", 1.01D, 1.1F);
        addTestObject("#3", 3000, "THREE", 3.03D, 3.3F);
        addTestObject("number four!", 4000, "FOUR", 4.04D, 4.4F);

        addTestList(0, 3);
        addTestList(0, 2, 3, 4, 5);
        addTestList(2, 4);

        addTestSet(0, 3);
        addTestSet(0, 2, 3, 4, 5);
        addTestSet(2, 4);

        addTestMap(0, 3);
        addTestMap(0, 2, 3, 5);
        addTestMap(2, 4);

        addTestEntry(1);
        addTestEntry(2);
        addTestEntry(3);
        addTestEntry(4);
        addTestEntry(5);

    }

    private void addTestObject(String f1, int f2, String f3, double f4, float f5) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(objSchema);
        rec.setString("field1", f1);
        rec.setInt("field2", f2);
        rec.setString("field3", f3);
        rec.setDouble("field4", f4);
        rec.setFloat("field5", f5);
        writeStateEngine.add(objSchema.getName(), rec);
    }

    private void addTestList(int... elementOrdinals) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(int ordinal : elementOrdinals)
            rec.addElement(ordinal);
        writeStateEngine.add(listSchema.getName(), rec);
    }

    private void addTestSet(int... elementOrdinals) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(int ordinal : elementOrdinals)
            rec.addElement(ordinal);
        writeStateEngine.add(setSchema.getName(), rec);
    }

    private void addTestMap(int... keyAndValueOrdinals) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        for(int i = 0; i < keyAndValueOrdinals.length; i += 2)
            rec.addEntry(keyAndValueOrdinals[i], keyAndValueOrdinals[i + 1]);
        writeStateEngine.add(mapSchema.getName(), rec);
    }

    private void addTestEntry(int data) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(elementSchema);
        rec.setInt("field", data);
        writeStateEngine.add(elementSchema.getName(), rec);
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(objSchema));
        writeStateEngine.addTypeState(new HollowListTypeWriteState(listSchema));
        writeStateEngine.addTypeState(new HollowSetTypeWriteState(setSchema));
        writeStateEngine.addTypeState(new HollowMapTypeWriteState(mapSchema));
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(elementSchema));
    }
}
