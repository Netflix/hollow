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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public void testIndexingStringTypeFieldsWithNullValues() throws Exception {
        mapper.add(new TypeC(null, "onez:"));
        mapper.add(new TypeC("onez:", "onez:"));
        mapper.add(new TypeC(null, null));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeC", "", "b1.value", "b2.value");

        Assert.assertNull(index.findMatches("one"));
        Assert.assertNull(index.findMatches("one", "onez:"));
        assertIteratorContainsAll(index.findMatches("onez:", "onez:").iterator(), 1);
    }

    @Test
    public void testIndexingStringTypeFieldsWithNullValuesInDifferentOrder() throws Exception {
        mapper.add(new TypeC(null, null));
        mapper.add(new TypeC(null, "onez:"));
        mapper.add(new TypeC("onez:", "onez:"));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeC", "", "b1.value", "b2.value");

        Assert.assertNull(index.findMatches("one"));
        Assert.assertNull(index.findMatches("one", "onez:"));
        assertIteratorContainsAll(index.findMatches("onez:", "onez:").iterator(), 2);
    }

    @Test
    public void testIndexingBooleanTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeD(null, null));
        mapper.add(new TypeD(true, "onez:"));
        mapper.add(new TypeD(false, null));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeD", "", "b1.value");

        assertIteratorContainsAll(index.findMatches(Boolean.FALSE).iterator(), 2);
        assertIteratorContainsAll(index.findMatches(Boolean.TRUE).iterator(), 1);
    }

    @Test
    public void testIndexingInlinedStringTypeFieldWithNullValues() throws Exception {
        mapper.add(new TypeE(null));
        mapper.add(new TypeE("onez:"));
        mapper.add(new TypeE(null));

        roundTripSnapshot();
        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeE", "", "b1");

        Assert.assertNull(index.findMatches("one:"));
        assertIteratorContainsAll(index.findMatches("onez:").iterator(), 1);
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

    private void assertIteratorContainsAll(HollowOrdinalIterator iter, int... expectedOrdinals) {
        Set<Integer> ordinalSet = new HashSet<Integer>();
        int ordinal = iter.next();
        while (ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            ordinalSet.add(ordinal);
            ordinal = iter.next();
        }

        for (int ord : expectedOrdinals) {
            Assert.assertTrue(ordinalSet.contains(ord));
        }
        Assert.assertTrue(ordinalSet.size() == expectedOrdinals.length);

    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    private static class TypeC {
        private final String b1;
        private final String b2;

        public TypeC(String b1, String b2) {
            this.b1 = b1;
            this.b2 = b2;
        }
    }

    @SuppressWarnings("unused")
    private static class TypeD {
        private final Boolean b1;
        private final String b2;

        public TypeD(Boolean b1, String b2) {
            this.b1 = b1;
            this.b2 = b2;
        }
    }

    @SuppressWarnings("unused")
    private static class TypeE {
        @HollowInline
        private final String b1;

        public TypeE(String b1) {
            this.b1 = b1;
        }
    }
}
