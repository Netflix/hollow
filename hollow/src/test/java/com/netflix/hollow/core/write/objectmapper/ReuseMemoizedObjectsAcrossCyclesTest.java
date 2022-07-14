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

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class ReuseMemoizedObjectsAcrossCyclesTest {

    @Test
    public void reuseObjectsAcrossCycles() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        TypeA a1 = new TypeA(1);
        TypeA a2 = new TypeA(2);

        Assert.assertEquals(0, mapper.add(a1));
        Assert.assertEquals(1, mapper.add(a2));
        Assert.assertEquals(0, mapper.add(a1));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        writeEngine.prepareForNextCycle();

        Assert.assertEquals(0, mapper.add(a1));
        Assert.assertEquals(2, mapper.add(new TypeA(3)));
        Assert.assertEquals(1, mapper.add(a2));
        Assert.assertEquals(1, mapper.add(a2));

        StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);

        Assert.assertEquals(3, readEngine.getTypeState("TypeA").getPopulatedOrdinals().cardinality());
    }

    @Test
    public void reuseMemoizedListsAcrossCycles() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        List<TypeA> a1 = new MemoizedList<TypeA>(Arrays.asList(new TypeA(1)));
        List<TypeA> a2 = new MemoizedList<TypeA>(Arrays.asList(new TypeA(2)));

        Assert.assertEquals(0, mapper.add(new ListType(a1)));
        Assert.assertEquals(1, mapper.add(new ListType(a2)));
        Assert.assertEquals(0, mapper.add(new ListType(a1)));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        writeEngine.prepareForNextCycle();

        List<TypeA> a3 = new MemoizedList<TypeA>(Arrays.asList(new TypeA(3)));

        Assert.assertEquals(0, mapper.add(new ListType(a1)));
        Assert.assertEquals(2, mapper.add(new ListType(a3)));
        Assert.assertEquals(1, mapper.add(new ListType(a2)));

        StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);

        Assert.assertEquals(3, readEngine.getTypeState("ListOfTypeA").getPopulatedOrdinals().cardinality());
    }

    @SuppressWarnings("unused")
    public class ListType {
        private final List<TypeA> list;

        public ListType(List<TypeA> list) {
            this.list = list;
        }
    }

    @Test
    public void reuseMemoizedSetsAcrossCycles() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        Set<TypeA> a1 = new MemoizedSet<TypeA>(Arrays.asList(new TypeA(1)));
        Set<TypeA> a2 = new MemoizedSet<TypeA>(Arrays.asList(new TypeA(2)));

        Assert.assertEquals(0, mapper.add(new SetType(a1)));
        Assert.assertEquals(1, mapper.add(new SetType(a2)));
        Assert.assertEquals(0, mapper.add(new SetType(a1)));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        writeEngine.prepareForNextCycle();

        Set<TypeA> a3 = new MemoizedSet<TypeA>(Arrays.asList(new TypeA(3)));

        Assert.assertEquals(0, mapper.add(new SetType(a1)));
        Assert.assertEquals(2, mapper.add(new SetType(a3)));
        Assert.assertEquals(1, mapper.add(new SetType(a2)));

        StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);

        Assert.assertEquals(3, readEngine.getTypeState("SetOfTypeA").getPopulatedOrdinals().cardinality());
    }

    @SuppressWarnings("unused")
    public class SetType {
        private final Set<TypeA> set;

        public SetType(Set<TypeA> set) {
            this.set = set;
        }
    }


    @Test
    public void reuseMemoizedMapsAcrossCycles() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        Map<Integer, TypeA> a1 = new MemoizedMap<Integer, TypeA>();
        a1.put(1, new TypeA(1));
        Map<Integer, TypeA> a2 = new MemoizedMap<Integer, TypeA>();
        a2.put(2, new TypeA(2));

        Assert.assertEquals(0, mapper.add(new MapType(a1)));
        Assert.assertEquals(1, mapper.add(new MapType(a2)));
        Assert.assertEquals(0, mapper.add(new MapType(a1)));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        writeEngine.prepareForNextCycle();

        Map<Integer, TypeA> a3 = new MemoizedMap<Integer, TypeA>();
        a3.put(3, new TypeA(3));

        Assert.assertEquals(0, mapper.add(new MapType(a1)));
        Assert.assertEquals(2, mapper.add(new MapType(a3)));
        Assert.assertEquals(1, mapper.add(new MapType(a2)));

        StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);

        Assert.assertEquals(3, readEngine.getTypeState("MapOfIntegerToTypeA").getPopulatedOrdinals().cardinality());

    }

    @SuppressWarnings("unused")
    public class MapType {
        private final Map<Integer, TypeA> map;

        public MapType(Map<Integer, TypeA> map) {
            this.map = map;
        }
    }

    @SuppressWarnings("unused")
    private static class TypeA {
        int value;

        public TypeA(int value) {
            this.value = value;
        }

        private final long __assigned_ordinal = -1;
    }

}
