/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import java.util.Date;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.tools.stringifier.HollowRecordJsonStringifier;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectMapperTest extends AbstractStateEngineTest {

    @Test
    public void testBasic() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA("two", 2, new TypeB((short)20, 20000000L, 2.2f, "two".toCharArray(), new byte[] { 2, 2, 2 }),
                Collections.<TypeC>emptySet()));
        mapper.add(new TypeA("one", 1, new TypeB((short)10, 10000000L, 1.1f, "one".toCharArray(), new byte[] { 1, 1, 1 }),
                new HashSet<TypeC>(Arrays.asList(new TypeC('d', map("one.1", 1, "one.2", 1, 1, "one.3", 1, 2, 3))))));

        roundTripSnapshot();

        System.out.println(new HollowRecordJsonStringifier(false, true).stringify(readStateEngine, "TypeA", 0));
        System.out.println("---------------------------------");
        System.out.println(new HollowRecordJsonStringifier().stringify(readStateEngine, "TypeA", 1));
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

    /**
     * Convenience method for experimenting with {@link HollowObjectMapper#initializeTypeState(Class)}
     * on classes we know should fail due to circular references, confirming the exception message is correct.
     *
     * @param clazz class to initialize
     * @param fieldName the name of the field that should trip the circular reference detection
     */
    protected void assertExpectedFailureMappingType(Class<?> clazz, String fieldName) {
        final String expected = clazz.getSimpleName() + "." + fieldName;
        try {
            HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
            mapper.initializeTypeState(clazz);
        } catch (IllegalStateException e) {

            Assert.assertTrue(String.format("missing expected fieldname %s in the message, was %s", expected, e.getMessage()), e.getMessage().contains(expected));
        }
    }
    private Map<String, List<Integer>> map(Object... keyValues) {
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        int i = 0;

        while(i < keyValues.length) {
            String key = (String)keyValues[i];
            List<Integer> values = new ArrayList<Integer>();
            i++;
            while(i < keyValues.length && keyValues[i] instanceof Integer) {
                values.add((Integer)keyValues[i]);
                i++;
            }

            map.put(key, values);
        }

        return map;
    }

    @Override
    protected void initializeTypeStates() {  }
    
    @SuppressWarnings("unused")
    private static enum TestEnum {
        ONE(1),
        TWO(2),
        THREE(3);
        
        int value;
        TestClass<TypeA> testClass;
        
        private TestEnum(int value) {
            this.value = value;
            this.testClass = new TestClass<TypeA>(value, value+1) {
                
            };
        }
        
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



}
