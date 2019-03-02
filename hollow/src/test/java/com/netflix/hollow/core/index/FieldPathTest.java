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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for FieldPath class.
 */
public class FieldPathTest {

    private HollowWriteStateEngine writeStateEngine;
    private HollowReadStateEngine readStateEngine;
    private HollowObjectMapper objectMapper;

    @Before
    public void beforeTestSetup() {
        writeStateEngine = new HollowWriteStateEngine();
        readStateEngine = new HollowReadStateEngine();
        objectMapper = new HollowObjectMapper(writeStateEngine);
    }

    private static class SimpleValue {
        int id;
    }

    private static class IntegerReference {
        Integer id;
    }

    private static class ObjectReference {
        IntegerReference reference;
    }

    @Test
    public void testSimpleValue() throws Exception {
        SimpleValue simpleValue = new SimpleValue();
        simpleValue.id = 3;
        objectMapper.add(simpleValue);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // with auto-expand feature on
        FieldPath fieldPath = new FieldPath(readStateEngine, "SimpleValue", "id");
        Object[] values = fieldPath.findValues(0);
        Assert.assertEquals(3, (int) values[0]);
        Object value = fieldPath.findValue(0);
        Assert.assertEquals(3, (int) value);

        // with auto-expand feature off
        fieldPath = new FieldPath(readStateEngine, "SimpleValue", "id", false);
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, (int) values[0]);
    }

    @Test
    public void testSimpleReference() throws Exception {
        IntegerReference integerReference = new IntegerReference();
        integerReference.id = 3;
        objectMapper.add(integerReference);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        //with auto expand
        FieldPath fieldPath = new FieldPath(readStateEngine, "IntegerReference", "id");
        Object[] values = fieldPath.findValues(0);
        Assert.assertEquals(3, (int) values[0]);
        Object value = fieldPath.findValue(0);
        Assert.assertEquals(3, (int) value);

        // with auto expand but giving full field path
        fieldPath = new FieldPath(readStateEngine, "IntegerReference", "id.value");
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, (int) values[0]);

        // without auto expand feature
        fieldPath = new FieldPath(readStateEngine, "IntegerReference", "id.value");
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, (int) values[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSimpleReferenceWithoutAutoExpand() throws Exception {
        IntegerReference integerReference = new IntegerReference();
        integerReference.id = 3;
        objectMapper.add(integerReference);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        //with auto expand
        try {
            new FieldPath(readStateEngine, "IntegerReference", "id", false);
        } catch (FieldPaths.FieldPathException e) {
            Assert.assertEquals(FieldPaths.FieldPathException.ErrorKind.NOT_FULL, e.error);
            Assert.assertEquals(1, e.fieldSegments.size());
            Assert.assertEquals("Integer", e.fieldSegments.get(0).getTypeName());
            Assert.assertEquals("id", e.fieldSegments.get(0).getName());
            throw e;
        }
    }


    @Test
    public void testObjectReference() throws Exception {
        IntegerReference integerReference = new IntegerReference();
        integerReference.id = 3;

        ObjectReference ref = new ObjectReference();
        ref.reference = integerReference;

        objectMapper.add(ref);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        //with auto expand -> this case works because there is only one path possible leading to a scalar value.
        FieldPath fieldPath = new FieldPath(readStateEngine, "ObjectReference", "reference");
        Object[] values = fieldPath.findValues(0);
        Assert.assertEquals(3, (int) values[0]);
        Object value = fieldPath.findValue(0);
        Assert.assertEquals(3, (int) value);

        // with partial auto-expand
        fieldPath = new FieldPath(readStateEngine, "ObjectReference", "reference.id");
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, (int) values[0]);

        // with auto-expand but complete path
        fieldPath = new FieldPath(readStateEngine, "ObjectReference", "reference.id.value");
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, (int) values[0]);

        // without auto-expand but complete path
        fieldPath = new FieldPath(readStateEngine, "ObjectReference", "reference.id.value");
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, (int) values[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObjectReferenceWithoutAutoExpand() throws Exception {
        IntegerReference integerReference = new IntegerReference();
        integerReference.id = 3;

        ObjectReference ref = new ObjectReference();
        ref.reference = integerReference;

        objectMapper.add(ref);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // with partial path
        try {
            new FieldPath(readStateEngine, "ObjectReference", "reference.id", false);
        } catch (FieldPaths.FieldPathException e) {
            Assert.assertEquals(FieldPaths.FieldPathException.ErrorKind.NOT_FULL, e.error);
            Assert.assertEquals(2, e.fieldSegments.size());
            Assert.assertEquals("Integer", e.fieldSegments.get(1).getTypeName());
            Assert.assertEquals("id", e.fieldSegments.get(1).getName());
            throw e;
        }
    }

    private static class MultiValue {
        int id;
        IntegerReference intRef;
    }

    private static class ObjectReferenceToMultiValue {
        int refId;
        MultiValue multiValue;
    }

    @Test
    public void testMultiFieldReference() throws Exception {
        IntegerReference simpleValue = new IntegerReference();
        simpleValue.id = 3;
        MultiValue multiValue = new MultiValue();
        multiValue.id = 2;
        multiValue.intRef = simpleValue;

        ObjectReferenceToMultiValue referenceToMultiValue = new ObjectReferenceToMultiValue();
        referenceToMultiValue.multiValue = multiValue;
        referenceToMultiValue.refId = 1;

        objectMapper.add(referenceToMultiValue);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // with auto-expand
        FieldPath fieldPath = new FieldPath(readStateEngine, "ObjectReferenceToMultiValue", "multiValue.intRef");
        Object[] values = fieldPath.findValues(0);
        Assert.assertEquals(3, ((int) values[0]));

        fieldPath = new FieldPath(readStateEngine, "ObjectReferenceToMultiValue", "multiValue.intRef.id.value");
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, ((int) values[0]));

        //without auto-complete but full path given
        fieldPath = new FieldPath(readStateEngine, "ObjectReferenceToMultiValue", "multiValue.intRef.id.value", false);
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, ((int) values[0]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiFieldReferenceIncomplete() throws Exception {
        IntegerReference simpleValue = new IntegerReference();
        simpleValue.id = 3;
        MultiValue multiValue = new MultiValue();
        multiValue.id = 2;
        multiValue.intRef = simpleValue;

        ObjectReferenceToMultiValue referenceToMultiValue = new ObjectReferenceToMultiValue();
        referenceToMultiValue.multiValue = multiValue;
        referenceToMultiValue.refId = 1;

        objectMapper.add(referenceToMultiValue);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // with auto-expand but incomplete, since reference has two fields, cannot auto-expand
        try {
            new FieldPath(readStateEngine, "ObjectReferenceToMultiValue", "multiValue");
        } catch (FieldPaths.FieldPathException e) {
            Assert.assertEquals(FieldPaths.FieldPathException.ErrorKind.NOT_EXPANDABLE, e.error);
            Assert.assertEquals("MultiValue", e.enclosingSchema.getName());
            Assert.assertEquals(1, e.fieldSegments.size());
            Assert.assertEquals("multiValue", e.fieldSegments.get(0).getName());
            throw e;
        }
    }


    // ------- unit tests for LIST type -------

    private static class ListType {
        List<Integer> intValues;
    }

    private static class ListObjectReference {
        List<SimpleValue> intValues;
    }

    @Test
    public void testListIntegerReference() throws Exception {
        ListType listType = new ListType();
        listType.intValues = Arrays.asList(1, 2, 3);

        objectMapper.add(listType);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        FieldPath fieldPath;
        Object[] values;

        //with partial auto expand
        fieldPath = new FieldPath(readStateEngine, "ListType", "intValues.element");
        values = fieldPath.findValues(0);
        Assert.assertEquals(1, (int) values[0]);
        Assert.assertEquals(2, (int) values[1]);
        Assert.assertEquals(3, (int) values[2]);

        //with auto expand but full path given
        fieldPath = new FieldPath(readStateEngine, "ListType", "intValues.element.value");
        values = fieldPath.findValues(0);
        Assert.assertEquals(1, (int) values[0]);
        Assert.assertEquals(2, (int) values[1]);
        Assert.assertEquals(3, (int) values[2]);

        //without auto expand but full path given
        fieldPath = new FieldPath(readStateEngine, "ListType", "intValues.element.value", false);
        values = fieldPath.findValues(0);
        Assert.assertEquals(1, (int) values[0]);
        Assert.assertEquals(2, (int) values[1]);
        Assert.assertEquals(3, (int) values[2]);
    }

    @Test
    public void testListObjectReference() throws Exception {
        ListObjectReference listType = new ListObjectReference();
        SimpleValue val1 = new SimpleValue();
        val1.id = 1;
        SimpleValue val2 = new SimpleValue();
        val2.id = 2;

        listType.intValues = Arrays.asList(val1, val2);

        objectMapper.add(listType);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        FieldPath fieldPath;
        Object[] values;

        //with partial auto expand
        fieldPath = new FieldPath(readStateEngine, "ListObjectReference", "intValues.element");
        values = fieldPath.findValues(0);
        Assert.assertEquals(1, (int) values[0]);
        Assert.assertEquals(2, (int) values[1]);

        //without auto expand
        fieldPath = new FieldPath(readStateEngine, "ListObjectReference", "intValues.element.id");
        values = fieldPath.findValues(0);
        Assert.assertEquals(1, (int) values[0]);
        Assert.assertEquals(2, (int) values[1]);
    }


    // ------- unit tests for SET type -------

    private static class SetType {
        Set<Integer> intValues;
    }

    @Test
    public void testSetIntegerReference() throws Exception {
        SetType setType = new SetType();
        setType.intValues = new HashSet<>(Arrays.asList(1, 2, 3));

        objectMapper.add(setType);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        FieldPath fieldPath;
        Object[] values;
        Set<Integer> valuesAsSet;

        //with partial auto expand
        fieldPath = new FieldPath(readStateEngine, "SetType", "intValues.element");
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, values.length);
        valuesAsSet = new HashSet<>();
        for (Object v : values) valuesAsSet.add((int) v);

        Assert.assertTrue(valuesAsSet.contains(1));
        Assert.assertTrue(valuesAsSet.contains(2));
        Assert.assertTrue(valuesAsSet.contains(3));

        //without auto expand
        fieldPath = new FieldPath(readStateEngine, "SetType", "intValues.element.value");
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, values.length);
        valuesAsSet = new HashSet<>();
        for (Object v : values) valuesAsSet.add((int) v);

        Assert.assertTrue(valuesAsSet.contains(1));
        Assert.assertTrue(valuesAsSet.contains(2));
        Assert.assertTrue(valuesAsSet.contains(3));
    }


    // ------- unit tests for MAP type -------


    private static class MapReference {
        Map<Integer, String> mapValues;
    }

    @Test
    public void testMapReference() throws Exception {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "one");
        map.put(2, "two");

        MapReference mapReference = new MapReference();
        mapReference.mapValues = map;

        objectMapper.add(mapReference);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // no auto-expand for a map
        FieldPath fieldPath = new FieldPath(readStateEngine, "MapReference", "mapValues.value");
        Object[] values = fieldPath.findValues(0);
        Assert.assertEquals(2, values.length);
        Set<String> valuesAsSet = new HashSet<>();
        for (Object v : values) valuesAsSet.add((String) v);
        Assert.assertTrue(valuesAsSet.contains("one"));
        Assert.assertTrue(valuesAsSet.contains("two"));
    }

    private static class MapObjectReference {
        Map<Integer, MapValue> mapValues;
    }

    private static class MapValue {
        String val;
    }

    @Test
    public void testMapObjectValueReference() throws Exception {
        MapValue val1 = new MapValue();
        val1.val = "one";

        MapValue val2 = new MapValue();
        val2.val = "two";
        Map<Integer, MapValue> map = new HashMap<>();
        map.put(1, val1);
        map.put(2, val2);

        MapObjectReference mapObjectReference = new MapObjectReference();
        mapObjectReference.mapValues = map;

        objectMapper.add(mapObjectReference);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // for values
        FieldPath fieldPath = new FieldPath(readStateEngine, "MapObjectReference", "mapValues.value");
        Object[] values = fieldPath.findValues(0);
        Assert.assertEquals(2, values.length);
        Set<String> valuesAsSet = new HashSet<>();
        for (Object v : values) valuesAsSet.add((String) v);
        Assert.assertTrue(valuesAsSet.contains("one"));
        Assert.assertTrue(valuesAsSet.contains("two"));

        // for keys
        fieldPath = new FieldPath(readStateEngine, "MapObjectReference", "mapValues.key");
        values = fieldPath.findValues(0);
        Assert.assertEquals(2, values.length);
        Set<Integer> keysAsSet = new HashSet<>();
        for (Object v : values) keysAsSet.add((int) v);
        Assert.assertTrue(keysAsSet.contains(1));
        Assert.assertTrue(keysAsSet.contains(2));
    }

    // Map key is a list
    private static class MapKeyReferenceAsList {
        Map<ListType, Integer> mapValues;
    }

    @Test
    public void testMapKeyReferenceToList() throws Exception {
        ListType listType = new ListType();
        listType.intValues = Arrays.asList(1, 2, 3);

        Map<ListType, Integer> map = new HashMap<>();
        map.put(listType, 1);

        MapKeyReferenceAsList mapKeyReferenceAsList = new MapKeyReferenceAsList();
        mapKeyReferenceAsList.mapValues = map;

        objectMapper.add(mapKeyReferenceAsList);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        FieldPath fieldPath;
        Object[] values;

        fieldPath = new FieldPath(readStateEngine, "MapKeyReferenceAsList", "mapValues.key.intValues.element.value");
        values = fieldPath.findValues(0);
        Assert.assertEquals(3, values.length);
    }
}
