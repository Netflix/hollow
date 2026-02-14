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
package com.netflix.hollow.core.write.objectmapper;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.stringifier.HollowRecordJsonStringifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class HollowObjectMapperTest extends AbstractStateEngineTest {

    @Test
    public void testBasic() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA("two", 2, new TypeB((short) 20, 20000000L, 2.2f, "two".toCharArray(), new byte[]{2, 2, 2}),
                Collections.<TypeC>emptySet()));
        mapper.add(new TypeA("one", 1, new TypeB((short) 10, 10000000L, 1.1f, "one".toCharArray(), new byte[]{1, 1, 1}),
                new HashSet<TypeC>(Arrays.asList(new TypeC('d', map("one.1", 1, "one.2", 1, 1, "one.3", 1, 2, 3))))));

        roundTripSnapshot();


        Assert.assertEquals("{\"a1\": \"two\",\"a2\": 2,\"b\": {\"b1\": 20,\"b2\": 20000000,\"b3\": 2.2,\"b4\": \"two\",\"b5\": [2, 2, 2]},\"cList\": []}",
                new HollowRecordJsonStringifier(false, true).stringify(readStateEngine, "TypeA", 0));

        //System.out.println("---------------------------------");
        //System.out.println(new HollowRecordJsonStringifier(false, true).stringify(readStateEngine, "TypeA", 1));
    }

    @Test
    public void testNullElements() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        // Lists cannot contain null elements
        try {
            mapper.add(new TypeWithList("a", null, "c"));
            Assert.fail("NullPointerException not thrown from List containing null elements");
        } catch (NullPointerException e) {
            String m = e.getMessage();
            Assert.assertNotNull(m);
            Assert.assertTrue(m.contains("Lists"));
        }

        // Sets cannot contain null elements
        try {
            mapper.add(new TypeWithSet("a", null, "c"));
            Assert.fail("NullPointerException not thrown from Set containing null elements");
        } catch (NullPointerException e) {
            String m = e.getMessage();
            Assert.assertNotNull(m);
            Assert.assertTrue(m.contains("Sets"));
        }

        // Maps cannot contain null keys
        try {
            mapper.add(new TypeWithMap("a", "a", null, "b", "c", "c"));
            Assert.fail("NullPointerException not thrown from Map containing null keys");
        } catch (NullPointerException e) {
            String m = e.getMessage();
            Assert.assertNotNull(m);
            Assert.assertTrue(m.contains("Maps"));
            Assert.assertTrue(m.contains("key"));
        }

        // Maps cannot contain null values
        try {
            mapper.add(new TypeWithMap("a", "a", "b", null, "c", "c"));
            Assert.fail("NullPointerException not thrown from Map containing null values");
        } catch (NullPointerException e) {
            String m = e.getMessage();
            Assert.assertNotNull(m);
            Assert.assertTrue(m.contains("Maps"));
            Assert.assertTrue(m.contains("value"));
        }
    }

    @Test
    public void testAllFieldTypes() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeWithAllFieldTypes(1));
        mapper.add(new TypeWithAllFieldTypes(2));

        TypeWithAllFieldTypes t = new TypeWithAllFieldTypes(3);
        t.nullFirstHalf();
        mapper.add(t);

        t = new TypeWithAllFieldTypes(4);
        t.nullSecondHalf();
        mapper.add(t);

        roundTripSnapshot();

        TypeWithAllFieldTypes expected = new TypeWithAllFieldTypes(1);
        TypeWithAllFieldTypes actual = new TypeWithAllFieldTypes(new GenericHollowObject(readStateEngine, "TypeWithAllFieldTypes", 0));
        Assert.assertEquals(expected, actual);

        expected = new TypeWithAllFieldTypes(2);
        actual = new TypeWithAllFieldTypes(new GenericHollowObject(readStateEngine, "TypeWithAllFieldTypes", 1));
        Assert.assertEquals(expected, actual);

        expected = new TypeWithAllFieldTypes(3);
        expected.nullFirstHalf();
        actual = new TypeWithAllFieldTypes(new GenericHollowObject(readStateEngine, "TypeWithAllFieldTypes", 2));
        Assert.assertEquals(expected, actual);

        expected = new TypeWithAllFieldTypes(4);
        expected.nullSecondHalf();
        actual = new TypeWithAllFieldTypes(new GenericHollowObject(readStateEngine, "TypeWithAllFieldTypes", 3));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEnumAndInlineClass() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(TestEnum.ONE);
        mapper.add(TestEnum.TWO);
        mapper.add(TestEnum.THREE);

        roundTripSnapshot();

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(readStateEngine, new PrimaryKey("TestEnum", "_name"));

        int twoOrdinal = idx.getMatchingOrdinal("TWO");

        GenericHollowObject obj = new GenericHollowObject(readStateEngine, "TestEnum", twoOrdinal);

        Assert.assertEquals("TWO", obj.getString("_name"));

        GenericHollowObject subObj = obj.getObject("testClass");

        Assert.assertEquals(2, subObj.getInt("val1"));
        Assert.assertEquals(3, subObj.getInt("val2"));
    }

    @Test
    public void testDate() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        long time = System.currentTimeMillis();

        mapper.add(new Date(time));

        roundTripSnapshot();

        int theOrdinal = readStateEngine.getTypeState("Date").maxOrdinal();

        GenericHollowObject obj = new GenericHollowObject(readStateEngine, "Date", theOrdinal);

        Assert.assertEquals(time, obj.getLong("value"));
    }

    @Test
    public void testInstant() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        long time = System.currentTimeMillis();
        Instant currentInstant = Instant.ofEpochMilli(time);

        mapper.add(currentInstant);

        roundTripSnapshot();

        int theOrdinal = readStateEngine.getTypeState("Instant").maxOrdinal();

        GenericHollowObject obj = new GenericHollowObject(readStateEngine, "Instant", theOrdinal);

        Assert.assertEquals(currentInstant, Instant.ofEpochSecond(obj.getLong("seconds"), obj.getInt("nanos")));
    }

    @Test
    public void testTransient() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.initializeTypeState(TestTransientClass.class);
        mapper.add(new TestTransientClass(1, 2, 3));

        roundTripSnapshot();

        HollowSchema schema = readStateEngine.getSchema("TestTransientClass");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        schema.writeTo(baos);

        String schemeText = baos.toString();
        Assert.assertTrue(schemeText.contains("notTransient"));
        Assert.assertFalse(schemeText.contains("transientKeyword"));
        Assert.assertFalse(schemeText.contains("annotatedTransient"));
    }

    @Test
    public void testTypeWithSpecialTypes() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        long time = System.currentTimeMillis();
        Date date = new Date(time);
        // what if the DateTimeException is thrown?
        Instant instant = Instant.now();
        LocalDate localDate = LocalDate.now();

        mapper.initializeTypeState(TypeWithSpecialTypes.class);
        mapper.add(new TypeWithSpecialTypes(date, instant, localDate));

        roundTripSnapshot();

        HollowSchema schema = readStateEngine.getSchema("TypeWithSpecialTypes");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        schema.writeTo(baos);

        String schemeText = baos.toString();
        Assert.assertTrue(schemeText.contains("date"));
        Assert.assertTrue(schemeText.contains("instant"));
        Assert.assertTrue(schemeText.contains("localDate"));

        int theOrdinal = readStateEngine.getTypeState("TypeWithSpecialTypes").maxOrdinal();

        GenericHollowObject obj = new GenericHollowObject(readStateEngine, "TypeWithSpecialTypes", theOrdinal);

        Assert.assertEquals(instant.getEpochSecond(), obj.getObject("instant").getLong("seconds"));
        Assert.assertEquals(instant.getNano(), obj.getObject("instant").getInt("nanos"));

        Assert.assertEquals(localDate.getYear(), obj.getObject("localDate").getInt("year"));
        Assert.assertEquals(localDate.getMonthValue(), obj.getObject("localDate").getInt("month"));
        Assert.assertEquals(localDate.getDayOfMonth(), obj.getObject("localDate").getInt("day"));
    }

    @Test
    public void testMappingInterface() throws IOException {
        assertExpectedFailureMappingInterfaceType(TestInterface.class, TestInterface.class);
    }

    @Test
    public void testMappingClassWithInterface() throws IOException {
        assertExpectedFailureMappingInterfaceType(TestClassContainingInterface.class, TestInterface.class);
    }

    @Test
    public void testMappingClassWithArray() throws IOException {
        assertExpectedFailureMappingArraysType(TestClassContainingArray.class, int[].class);
    }

    @Test
    public void testMappingClassWithTransientArray() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.initializeTypeState(TestClassContainingTransientArray.class);
        mapper.add(new TestClassContainingTransientArray(new int[]{1, 2}));

        roundTripSnapshot();

        HollowSchema schema = readStateEngine.getSchema("TestClassContainingTransientArray");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        schema.writeTo(baos);

        String schemeText = baos.toString();
        Assert.assertTrue(schemeText.contains("TestClassContainingTransientArray"));
        Assert.assertFalse(schemeText.contains("int[]"));
        Assert.assertFalse(schemeText.contains("intArray"));
    }

    @Test
    public void testMappingClassWithHollowTransientArray() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.initializeTypeState(TestClassContainingHollowTransientArray.class);
        mapper.add(new TestClassContainingTransientArray(new int[]{1, 2}));

        roundTripSnapshot();

        HollowSchema schema = readStateEngine.getSchema("TestClassContainingHollowTransientArray");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        schema.writeTo(baos);

        String schemeText = baos.toString();
        Assert.assertTrue(schemeText.contains("TestClassContainingHollowTransientArray"));
        Assert.assertFalse(schemeText.contains("int[]"));
        Assert.assertFalse(schemeText.contains("intArray"));
    }

    @Test
    public void testMappingCircularReference() throws IOException {
        assertExpectedFailureMappingType(DirectCircularReference.class, "child");
    }

    @Test
    public void testMappingCircularReferenceList() throws IOException {
        assertExpectedFailureMappingType(DirectListCircularReference.class, "children");
    }

    @Test
    public void testMappingCircularReferenceSet() throws IOException {
        assertExpectedFailureMappingType(DirectSetCircularReference.class, "children");
    }

    @Test
    public void testMappingCircularReferenceMap() throws IOException {
        assertExpectedFailureMappingType(DirectMapCircularReference.class, "children");
    }

    @Test
    public void testMappingIndirectircularReference() throws IOException {
        assertExpectedFailureMappingType(IndirectCircularReference.TypeE.class, "f");
    }

    @Test
    public void testAssignedOrdinal() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        TypeWithAssignedOrdinal o = new TypeWithAssignedOrdinal();
        mapper.add(o);
        Assert.assertNotEquals(HollowConstants.ORDINAL_NONE, o.__assigned_ordinal);
    }

    @Test
    public void testFinalAssignedOrdinal() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        TypeWithFinalAssignedOrdinal o = new TypeWithFinalAssignedOrdinal();
        mapper.add(o);
        Assert.assertNotEquals(HollowConstants.ORDINAL_NONE, o.__assigned_ordinal);
    }

    @Test
    public void testPreassignedOrdinal() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        TypeWithAssignedOrdinal o = new TypeWithAssignedOrdinal();
        o.__assigned_ordinal = 1;
        mapper.add(o);
        Assert.assertNotEquals(1, o.__assigned_ordinal);
    }

    @Test
    public void testIntAssignedOrdinal() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        TypeWithIntAssignedOrdinal o = new TypeWithIntAssignedOrdinal();
        mapper.add(o);
        Assert.assertEquals(HollowConstants.ORDINAL_NONE, o.__assigned_ordinal);
    }

    @Test
    public void testIntPreassignedOrdinal() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        TypeWithIntAssignedOrdinal o = new TypeWithIntAssignedOrdinal();
        o.__assigned_ordinal = 1;
        mapper.add(o);
        // int fields are ignored
        Assert.assertEquals(1, o.__assigned_ordinal);
    }

    @Test
    public void testFailsToCreateSchemaIfThereAreDuplicateFields() {
        try {
            HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
            mapper.initializeTypeState(Child.class);
            Assert.fail("Expected Exception not thrown");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Duplicate field name 'myField1' found in class hierarchy for class com.netflix.hollow.core.write.objectmapper.HollowObjectMapperTest$Child", e.getMessage());
        }
    }

    /**
     * Convenience method for experimenting with {@link HollowObjectMapper#initializeTypeState(Class)}
     * on classes we know should fail due to circular references, confirming the exception message is correct.
     *
     * @param clazz     class to initialize
     * @param fieldName the name of the field that should trip the circular reference detection
     */
    protected void assertExpectedFailureMappingType(Class<?> clazz, String fieldName) {
        final String expected = clazz.getSimpleName() + "." + fieldName;
        try {
            HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
            mapper.initializeTypeState(clazz);
            Assert.fail("Expected Exception not thrown");
        } catch (IllegalStateException e) {
            Assert.assertTrue(String.format("missing expected fieldname %s in the message, was %s", expected, e.getMessage()), e.getMessage().contains(expected));
        }
    }

    /**
     * Convenience method for experimenting with {@link HollowObjectMapper#initializeTypeState(Class)}
     * on classes we know should fail due to fields that are interfaces, confirming the exception message is correct.
     *
     * @param clazz          class to initialize
     * @param interfaceClazz interface class that breaks the initialization
     */
    protected void assertExpectedFailureMappingInterfaceType(Class<?> clazz, Class<?> interfaceClazz) {
        final String expected = "Unexpected interface " + interfaceClazz.getSimpleName() + " passed as field.";
        try {
            HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
            mapper.initializeTypeState(clazz);
            Assert.fail("Expected Exception not thrown");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(String.format("trying to generate schema based on interface %s, was %s", interfaceClazz.getSimpleName(), e.getMessage()), e.getMessage().contains(expected));
        }
    }

    /**
     * Convenience method for experimenting with {@link HollowObjectMapper#initializeTypeState(Class)}
     * on classes we know should fail due to fields that are arrays, confirming the exception message is correct.
     *
     * @param clazz      class to initialize
     * @param arrayClass interface class that breaks the initialization
     */
    protected void assertExpectedFailureMappingArraysType(Class<?> clazz, Class<?> arrayClass) {
        final String expected = "Unexpected array " + arrayClass.getSimpleName() + " passed as field. Consider using collections or marking as transient.";
        try {
            HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
            mapper.initializeTypeState(clazz);
            Assert.fail("Expected Exception not thrown");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(String.format("trying to generate schema based on array %s, was %s", arrayClass.getSimpleName(), e.getMessage()), e.getMessage().contains(expected));
        }
    }

    private Map<String, List<Integer>> map(Object... keyValues) {
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        int i = 0;

        while (i < keyValues.length) {
            String key = (String) keyValues[i];
            List<Integer> values = new ArrayList<Integer>();
            i++;
            while (i < keyValues.length && keyValues[i] instanceof Integer) {
                values.add((Integer) keyValues[i]);
                i++;
            }

            map.put(key, values);
        }

        return map;
    }

    @Override
    protected void initializeTypeStates() {
    }

    @SuppressWarnings("unused")
    private enum TestEnum {
        ONE(1),
        TWO(2),
        THREE(3);

        int value;
        TestClass<TypeA> testClass;

        private TestEnum(int value) {
            this.value = value;
            this.testClass = new TestClass<TypeA>(value, value + 1) {

            };
        }

    }

    public interface TestInterface {
        int test();
    }

    @SuppressWarnings("unused")
    private static abstract class TestClass<T> {
        int val1;
        int val2;

        public TestClass(int val1, int val2) {
            this.val1 = val1;
            this.val2 = val2;
        }
    }

    @SuppressWarnings("unused")
    private static class TestTransientClass {
        int notTransient;
        transient int transientKeyword;
        @HollowTransient
        int annotatedTransient;

        public TestTransientClass(int notTransient, int transientKeyword, int annotatedTransient) {
            this.notTransient = notTransient;
            this.transientKeyword = transientKeyword;
            this.annotatedTransient = annotatedTransient;
        }
    }

    @SuppressWarnings("unused")
    private static class TestClassContainingInterface {
        int val1;
        TestInterface val2;

        public TestClassContainingInterface(int val1, TestInterface val2) {
            this.val1 = val1;
            this.val2 = val2;
        }
    }

    @SuppressWarnings("unused")
    private static class TestClassContainingArray {
        int[] intArray;

        public TestClassContainingArray(int[] intArray) {
            this.intArray = intArray;
        }
    }

    @SuppressWarnings("unused")
    private static class TestClassContainingTransientArray {
        transient int[] intArray;

        public TestClassContainingTransientArray(int[] intArray) {
            this.intArray = intArray;
        }
    }

    @SuppressWarnings("unused")
    private static class TestClassContainingHollowTransientArray {
        @HollowTransient
        int[] intArray;

        public TestClassContainingHollowTransientArray(int[] intArray) {
            this.intArray = intArray;
        }
    }

    @SuppressWarnings("unused")
    private static class TestClassImplementingInterface implements TestInterface {
        int val1;

        public TestClassImplementingInterface(int val1) {
            this.val1 = val1;
        }

        @Override
        public int test() {
            return 0;
        }
    }

    static class TypeWithSet {
        Set<String> c;

        TypeWithSet(String... c) {
            this.c = new HashSet<>(Arrays.asList(c));
        }
    }

    static class TypeWithList {
        List<String> c;

        TypeWithList(String... c) {
            this.c = new ArrayList<>(Arrays.asList(c));
        }
    }

    static class TypeWithMap {
        Map<String, String> m;

        TypeWithMap(String... kv) {
            m = new HashMap<>();
            for (int i = 0; i < kv.length; i += 2) {
                m.put(kv[i], kv[i + 1]);
            }
        }
    }

    static class TypeWithAssignedOrdinal {
        long __assigned_ordinal = HollowConstants.ORDINAL_NONE;
    }

    static class TypeWithFinalAssignedOrdinal {
        // Cannot assign directly to this field otherwise javac may
        // assume the value is a constant when accessed.
        final long __assigned_ordinal;

        TypeWithFinalAssignedOrdinal() {
            this.__assigned_ordinal = HollowConstants.ORDINAL_NONE;
        };
    }

    static class TypeWithIntAssignedOrdinal {
        int __assigned_ordinal = HollowConstants.ORDINAL_NONE;
    }

    static class Parent {
        String myField1;
    }

    static class Child extends Parent {
        String myField1;
    }
}
