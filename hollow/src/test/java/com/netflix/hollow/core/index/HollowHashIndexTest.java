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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public class HollowHashIndexTest extends AbstractStateEngineTest {
    private HollowObjectMapper mapper;

    @Override
    protected void initializeTypeStates() {
        mapper = new HollowObjectMapper(writeStateEngine);
    }

    @Test
    public void testBasicHashIndexFunctionality() throws Exception {
        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two"), new TypeB("twenty"), new TypeB("two hundred")));
        mapper.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
        mapper.add(new TypeA(4, 4.4d, new TypeB("four")));
        mapper.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));

        roundTripSnapshot();

        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeA", "a1", new String[]{"a1", "ab.element.b1.value"});

        Assert.assertNull("An entry that doesn't have any matches has a null iterator", index.findMatches(0, "notfound"));
        assertIteratorContainsAll(index.findMatches(1, "one").iterator(), 0);
        assertIteratorContainsAll(index.findMatches(1, "1").iterator(), 1);
        assertIteratorContainsAll(index.findMatches(2, "two").iterator(), 2);
        assertIteratorContainsAll(index.findMatches(2, "twenty").iterator(), 2);
        assertIteratorContainsAll(index.findMatches(2, "two hundred").iterator(), 2);
        assertIteratorContainsAll(index.findMatches(3, "three").iterator(), 3);
        assertIteratorContainsAll(index.findMatches(3, "thirty").iterator(), 3);
        assertIteratorContainsAll(index.findMatches(3, "three hundred").iterator(), 3);
        assertIteratorContainsAll(index.findMatches(4, "four").iterator(), 4, 5);
        assertIteratorContainsAll(index.findMatches(4, "forty").iterator(), 5);

    }

    @Test
    public void testIndexingStringTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeB(null));
        mapper.add(new TypeB("onez:"));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeB", "", "b1.value");

        Assert.assertNull(index.findMatches("one:"));
        assertIteratorContainsAll(index.findMatches("onez:").iterator(), 1);
    }

    @Test
    public void testIndexingBytesTypeFieldWithNullValues() throws Exception {
        byte[] bytes = {-120,0,0,0};
        mapper.add(new TypeBytes(null));
        mapper.add(new TypeBytes(bytes));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeBytes", "", "data");

        byte[] nonExistingBytes = {1};
        Assert.assertNull(index.findMatches(nonExistingBytes));
        assertIteratorContainsAll(index.findMatches(bytes).iterator(), 1);
    }

    @Test
    public void testIndexingStringTypeFieldsWithNullValues() throws Exception {
        mapper.add(new TypeTwoStrings(null, "onez:"));
        mapper.add(new TypeTwoStrings("onez:", "onez:"));
        mapper.add(new TypeTwoStrings(null, null));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeTwoStrings", "", "b1.value", "b2.value");

        Assert.assertNull(index.findMatches("one"));
        Assert.assertNull(index.findMatches("one", "onez:"));
        assertIteratorContainsAll(index.findMatches("onez:", "onez:").iterator(), 1);
    }

    @Test
    public void testIndexingStringTypeFieldsWithNullValuesInDifferentOrder() throws Exception {
        mapper.add(new TypeTwoStrings(null, null));
        mapper.add(new TypeTwoStrings(null, "onez:"));
        mapper.add(new TypeTwoStrings("onez:", "onez:"));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeTwoStrings", "", "b1.value", "b2.value");

        Assert.assertNull(index.findMatches("one"));
        Assert.assertNull(index.findMatches("one", "onez:"));
        assertIteratorContainsAll(index.findMatches("onez:", "onez:").iterator(), 2);
    }

    @Test
    public void testIndexingBooleanTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeBoolean(null));
        mapper.add(new TypeBoolean(true));
        mapper.add(new TypeBoolean(false));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeBoolean", "", "data.value");

        assertIteratorContainsAll(index.findMatches(Boolean.FALSE).iterator(), 2);
        assertIteratorContainsAll(index.findMatches(Boolean.TRUE).iterator(), 1);
    }

    @Test
    public void testIndexingInlinedStringTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeInlinedString(null));
        mapper.add(new TypeInlinedString("onez:"));
        mapper.add(new TypeInlinedString(null));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeInlinedString", "", "data");

        Assert.assertNull(index.findMatches("one:"));
        assertIteratorContainsAll(index.findMatches("onez:").iterator(), 1);
    }

    @Test
    public void testIndexingLongTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeLong(null));
        mapper.add(new TypeLong(3L));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeLong", "", "data.value");

        Assert.assertNull(index.findMatches(2L));
        assertIteratorContainsAll(index.findMatches(3L).iterator(), 1);
    }

    @Test
    public void testIndexingDoubleTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeDouble(null));
        mapper.add(new TypeDouble(-8.0));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeDouble", "", "data.value");

        Assert.assertNull(index.findMatches(2.0));
        assertIteratorContainsAll(index.findMatches(-8.0).iterator(), 1);
    }

    @Test
    public void testIndexingIntegerTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeInteger(null));
        mapper.add(new TypeInteger(-1));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeInteger", "", "data.value");

        Assert.assertNull(index.findMatches(2));
        assertIteratorContainsAll(index.findMatches(-1).iterator(), 1);
    }

    @Test
    public void testIndexingFloatTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeFloat(null));
        mapper.add(new TypeFloat(-1.0f));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeFloat", "", "data.value");

        Assert.assertNull(index.findMatches(2.0f));
        assertIteratorContainsAll(index.findMatches(-1.0f).iterator(), 1);
    }

    @Test
    public void testIndexingReferenceTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeC(null));
        mapper.add(new TypeC(new TypeD(null)));
        mapper.add(new TypeC(new TypeD("one")));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeC", "", "cd.d1.value");

        Assert.assertNull(index.findMatches("none"));
        assertIteratorContainsAll(index.findMatches("one").iterator(), 2);
    }

    @Test
    public void testIndexingListTypeField() throws Exception {
        mapper.add(new TypeList("A", "B", "C", "D", "A", "B", "C", "D"));
        mapper.add(new TypeList("B", "C", "D", "E"));
        mapper.add(new TypeList("X", "Y", "Z"));
        mapper.add(new TypeList());
        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeList", "", "data.element.value");

        Assert.assertNull(index.findMatches("M"));
        Assert.assertNull(index.findMatches(""));
        assertIteratorContainsAll(index.findMatches("A").iterator(), 0);
        assertIteratorContainsAll(index.findMatches("B").iterator(), 0, 1);
        assertIteratorContainsAll(index.findMatches("X").iterator(), 2);
    }

    @Test
    public void testIndexingSetTypeField() throws Exception {
        mapper.add(new TypeSet("A", "B", "C", "D"));
        mapper.add(new TypeSet("B", "C", "D", "E"));
        mapper.add(new TypeSet("X", "Y", "Z"));
        mapper.add(new TypeSet());
        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeSet", "", "data.element.value");

        Assert.assertNull(index.findMatches("M"));
        Assert.assertNull(index.findMatches(""));
        assertIteratorContainsAll(index.findMatches("A").iterator(), 0);
        assertIteratorContainsAll(index.findMatches("B").iterator(), 0, 1);
        assertIteratorContainsAll(index.findMatches("X").iterator(), 2);
    }

    @Test
    public void testIndexingListOfIntTypeField() throws Exception {
        mapper.add(new TypeListOfTypeString(10, 20, 30, 40, 10, 12));
        mapper.add(new TypeListOfTypeString(10, 20, 30));
        mapper.add(new TypeListOfTypeString(50, 51, 52));
        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeListOfTypeString", "", "data.element.data.value");

        Assert.assertNull(index.findMatches(10000));
        Assert.assertNull(index.findMatches(-1));
        assertIteratorContainsAll(index.findMatches(40).iterator(), 0);
        assertIteratorContainsAll(index.findMatches(10).iterator(), 0, 1);
        assertIteratorContainsAll(index.findMatches(50).iterator(), 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindingMatchForNullQueryValue() throws Exception {
        mapper.add(new TypeB("one:"));
        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeB", "", "b1.value");
        index.findMatches(new Object[]{null});
        Assert.fail("exception expected");
    }

    @Test
    public void testUpdateListener() throws Exception {
        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two"), new TypeB("twenty"), new TypeB("two hundred")));
        mapper.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
        mapper.add(new TypeA(4, 4.4d, new TypeB("four")));
        mapper.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));

        roundTripSnapshot();

        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeA", "", "a1");
        index.listenForDeltaUpdates();

        // spot check initial mapper state
        Assert.assertNull("An entry that doesn't have any matches has a null iterator", index.findMatches(0));
        assertIteratorContainsAll(index.findMatches(1).iterator(), 1, 0);
        assertIteratorContainsAll(index.findMatches(2).iterator(), 2);
        assertIteratorContainsAll(index.findMatches(3).iterator(), 3);
        assertIteratorContainsAll(index.findMatches(4).iterator(), 4, 5);

        HollowOrdinalIterator preUpdateIterator = index.findMatches(4).iterator();

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
        mapper.add(new TypeA(4, 4.4d, new TypeB("four"), new TypeB("fore")));
        mapper.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("fourfour")));
        mapper.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));

        // run a round trip
        roundTripDelta();

        // verify the ordinals we get from the index match our new expected ones.
        assertIteratorContainsAll(index.findMatches(1).iterator(), 1, 0);
        Assert.assertNull("A removed entry that doesn't have any matches", index.findMatches(2));
        assertIteratorContainsAll(index.findMatches(3).iterator(), 3);
        assertIteratorContainsAll(index.findMatches(4).iterator(), 5, 6, 7);

        // an iterator doesn't update itself if it was retrieved prior to an update being applied
        assertIteratorContainsAll(preUpdateIterator, 4, 5);
    }
    
    @Test
    public void testGettingPropertiesValues() throws Exception {
        mapper.add(new TypeInlinedString(null));
        mapper.add(new TypeInlinedString("onez:"));
        mapper.add(new TypeInlinedString(null));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeInlinedString", "", "data");
        Assert.assertEquals(index.getMatchFields().length, 1);
        Assert.assertEquals(index.getMatchFields()[0], "data");
        Assert.assertEquals(index.getType(), "TypeInlinedString");
        Assert.assertEquals(index.getSelectField(), "");
    }

    private void assertIteratorContainsAll(HollowOrdinalIterator iter, int... expectedOrdinals) {
        Set<Integer> ordinalSet = new HashSet<>();
        int ordinal = iter.next();
        while (ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            ordinalSet.add(ordinal);
            ordinal = iter.next();
        }

        Set<Integer> expectedSet = IntStream.of(expectedOrdinals).boxed().collect(toSet());
        Assert.assertEquals(expectedSet, ordinalSet);
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeA {
        private final int a1;
        private final double a2;
        private final List<TypeB> ab;

        public TypeA(int a1, double a2, TypeB... ab) {
            this.a1 = a1;
            this.a2 = a2;
            this.ab = Arrays.asList(ab);
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeB {
        private final String b1;
        private final boolean isDuplicate;

        public TypeB(String b1) {
            this(b1, false);
        }

        public TypeB(String b1, boolean isDuplicate) {
            this.b1 = b1;
            this.isDuplicate = isDuplicate;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeC {
        private final TypeD cd;

        public TypeC(TypeD cd) {
            this.cd = cd;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeD {
        private final String d1;

        public TypeD(String d1) {
            this.d1 = d1;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeTwoStrings {
        private final String b1;
        private final String b2;

        public TypeTwoStrings(String b1, String b2) {
            this.b1 = b1;
            this.b2 = b2;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeBoolean {
        private final Boolean data;

        public TypeBoolean(Boolean data) {
            this.data = data;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeInlinedString {
        @HollowInline
        private final String data;

        public TypeInlinedString(String data) {
            this.data = data;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeLong {
        private final Long data;

        public TypeLong(Long data) {
            this.data = data;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeDouble {
        private final Double data;

        public TypeDouble(Double data) {
            this.data = data;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeInteger {
        private final Integer data;

        public TypeInteger(Integer data) {
            this.data = data;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeFloat {
        private final Float data;

        public TypeFloat(Float data) {
            this.data = data;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeBytes {
        private final byte[] data;

        public TypeBytes(byte[] data) {
            this.data = data;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeList {
        private final List<String> data;

        public TypeList(String ... data) {
            this.data = Arrays.asList(data);
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class TypeListOfTypeString {
        private final List<TypeInteger> data;

        public TypeListOfTypeString(Integer ... data) {
            this.data = stream(data).map(TypeInteger::new).collect(toList());
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
    private static class TypeSet {
        private final Set<String> data;

        public TypeSet(String ... data) {
            this.data = new HashSet<>(Arrays.asList(data));
        }
    }
}
