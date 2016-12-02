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

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class HollowHashIndexTest extends AbstractStateEngineTest {

    @Test
    public void test() throws Exception {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.addObject(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.addObject(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.addObject(new TypeA(2, 2.2d, new TypeB("two"), new TypeB("twenty"), new TypeB("two hundred")));
        mapper.addObject(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));

        roundTripSnapshot();

        HollowHashIndex index = new HollowHashIndex(readStateEngine, "TypeA", "a1", new String[] {"a1", "ab.element.b1.value"});

        HollowHashIndexResult result = index.findMatches(2, "twenty");
        HollowOrdinalIterator iter = result.iterator();

        int matchedOrdinal = iter.next();

        while(matchedOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            System.out.println(matchedOrdinal);
            matchedOrdinal = iter.next();
        }

        System.out.println(result.contains(2));

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

    @Override
    protected void initializeTypeStates() { }

}
