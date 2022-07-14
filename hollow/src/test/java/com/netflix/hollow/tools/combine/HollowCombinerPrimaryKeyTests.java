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
package com.netflix.hollow.tools.combine;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowCombinerPrimaryKeyTests {

    HollowReadStateEngine input1;
    HollowReadStateEngine input2;
    HollowReadStateEngine input3;

    @Before
    public void setUp() throws IOException {

        HollowWriteStateEngine input = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(input);
        addObject(mapper, 1, 1, 1, 1);
        addObject(mapper, 1, 2, 2, 2);
        addObject(mapper, 1, 3, 3, 3);
        input1 = StateEngineRoundTripper.roundTripSnapshot(input);

        input = new HollowWriteStateEngine();
        mapper = new HollowObjectMapper(input);
        addObject(mapper, 2, 4, 2, 3);
        addObject(mapper, 2, 5, 4, 4);
        addObject(mapper, 2, 6, 6, 6);
        input2 = StateEngineRoundTripper.roundTripSnapshot(input);

        input = new HollowWriteStateEngine();
        mapper = new HollowObjectMapper(input);
        addObject(mapper, 3, 7, 2, 3);
        addObject(mapper, 3, 8, 7, 6);
        addObject(mapper, 3, 9, 8, 8);
        addObject(mapper, 3, 10, 4, 10);
        mapper.add(new TypeC(100, 3));
        input3 = StateEngineRoundTripper.roundTripSnapshot(input);

    }

    @Test
    public void testCascadingKeys() throws IOException {
        HollowCombiner combiner = new HollowCombiner(input1, input2, input3);
        combiner.setPrimaryKeys(new PrimaryKey("TypeB", "key"), new PrimaryKey("TypeC", "key"));

        combiner.combine();

        HollowReadStateEngine output = StateEngineRoundTripper.roundTripSnapshot(combiner.getCombinedStateEngine());

        assertObject(output, 1, 1, 1, 1, 1, 1);
        assertObject(output, 2, 1, 2, 1, 2, 1);
        assertObject(output, 3, 1, 3, 1, 3, 1);
        assertObject(output, 4, 2, 2, 1, 2, 1);
        assertObject(output, 5, 2, 4, 2, 4, 2);
        assertObject(output, 6, 2, 6, 2, 6, 2);
        assertObject(output, 7, 3, 2, 1, 2, 1);
        assertObject(output, 8, 3, 7, 3, 6, 2);
        assertObject(output, 9, 3, 8, 3, 8, 3);
        assertObject(output, 10, 3, 4, 2, 4, 2);
    }

    @Test
    public void testCompoundKeys() throws IOException {
        HollowCombiner combiner = new HollowCombiner(input1, input2, input3);
        combiner.setPrimaryKeys(new PrimaryKey("TypeB", "key", "c.key"));

        combiner.combine();

        HollowReadStateEngine output = StateEngineRoundTripper.roundTripSnapshot(combiner.getCombinedStateEngine());

        assertObject(output, 1, 1, 1, 1, 1, 1);
        assertObject(output, 2, 1, 2, 1, 2, 1);
        assertObject(output, 3, 1, 3, 1, 3, 1);
        assertObject(output, 4, 2, 2, 2, 3, 2);
        assertObject(output, 5, 2, 4, 2, 4, 2);
        assertObject(output, 6, 2, 6, 2, 6, 2);
        assertObject(output, 7, 3, 2, 2, 3, 2);
        assertObject(output, 8, 3, 7, 3, 6, 3);
        assertObject(output, 9, 3, 8, 3, 8, 3);
        assertObject(output, 10, 3, 4, 3, 10, 3);
    }

    @Test
    public void testCompoundCascadingKeys() throws IOException {
        HollowCombiner combiner = new HollowCombiner(input1, input2, input3);
        combiner.setPrimaryKeys(new PrimaryKey("TypeB", "key", "c.key"), new PrimaryKey("TypeC", "key"));

        combiner.combine();

        HollowReadStateEngine output = StateEngineRoundTripper.roundTripSnapshot(combiner.getCombinedStateEngine());

        assertObject(output, 1, 1, 1, 1, 1, 1);
        assertObject(output, 2, 1, 2, 1, 2, 1);
        assertObject(output, 3, 1, 3, 1, 3, 1);
        assertObject(output, 4, 2, 2, 2, 3, 1);
        assertObject(output, 5, 2, 4, 2, 4, 2);
        assertObject(output, 6, 2, 6, 2, 6, 2);
        assertObject(output, 7, 3, 2, 2, 3, 1);
        assertObject(output, 8, 3, 7, 3, 6, 2);
        assertObject(output, 9, 3, 8, 3, 8, 3);
        assertObject(output, 10, 3, 4, 3, 10, 3);
    }

    @Test
    public void testExplicitlyExcludedAndRemappedKeys() throws IOException {
        HollowPrimaryKeyIndex cIdx1 = new HollowPrimaryKeyIndex(input1, "TypeC", "key");
        HollowCombinerExcludePrimaryKeysCopyDirector director = new HollowCombinerExcludePrimaryKeysCopyDirector();
        director.excludeKey(cIdx1, 3);

        HollowCombiner combiner = new HollowCombiner(director, input1, input2, input3);
        combiner.setPrimaryKeys(new PrimaryKey("TypeB", "key", "c.key"), new PrimaryKey("TypeC", "key"));

        combiner.combine();

        HollowReadStateEngine output = StateEngineRoundTripper.roundTripSnapshot(combiner.getCombinedStateEngine());

        assertObject(output, 1, 1, 1, 1, 1, 1);
        assertObject(output, 2, 1, 2, 1, 2, 1);
        assertObject(output, 3, 1, 3, 1, 3, 2);
        assertObject(output, 4, 2, 2, 2, 3, 2);
        assertObject(output, 5, 2, 4, 2, 4, 2);
        assertObject(output, 6, 2, 6, 2, 6, 2);
        assertObject(output, 7, 3, 2, 2, 3, 2);
        assertObject(output, 8, 3, 7, 3, 6, 2);
        assertObject(output, 9, 3, 8, 3, 8, 3);
        assertObject(output, 10, 3, 4, 3, 10, 3);
    }

    @Test
    public void testExplicitlyExcludedButOtherwiseReferencedKeys() throws IOException {
        HollowPrimaryKeyIndex cIdx3 = new HollowPrimaryKeyIndex(input3, "TypeC", "key");
        HollowCombinerExcludePrimaryKeysCopyDirector director = new HollowCombinerExcludePrimaryKeysCopyDirector();
        director.excludeKey(cIdx3, 8);

        HollowCombiner combiner = new HollowCombiner(director, input1, input2, input3);
        combiner.setPrimaryKeys(new PrimaryKey("TypeB", "key", "c.key"), new PrimaryKey("TypeC", "key"));

        combiner.combine();

        HollowReadStateEngine output = StateEngineRoundTripper.roundTripSnapshot(combiner.getCombinedStateEngine());

        assertObject(output, 9, 3, 8, 3, 8, 3);
    }

    @Test
    public void testExcludeReferencedObjectsFromPrimaryKeyCopyDirector() throws IOException {
        HollowPrimaryKeyIndex aIdx3 = new HollowPrimaryKeyIndex(input3, "TypeA", "key");
        HollowCombinerExcludePrimaryKeysCopyDirector director = new HollowCombinerExcludePrimaryKeysCopyDirector();
        director.excludeKey(aIdx3, 9);

        HollowCombiner combiner = new HollowCombiner(director, input1, input2, input3);
        combiner.setPrimaryKeys(new PrimaryKey("TypeB", "key", "c.key"), new PrimaryKey("TypeC", "key"));
        combiner.combine();
        HollowReadStateEngine output = StateEngineRoundTripper.roundTripSnapshot(combiner.getCombinedStateEngine());

        Assert.assertEquals(-1, new HollowPrimaryKeyIndex(output, "TypeA", "key").getMatchingOrdinal(9));
        Assert.assertNotEquals(-1, new HollowPrimaryKeyIndex(output, "TypeB", "key").getMatchingOrdinal(8));
        Assert.assertNotEquals(-1, new HollowPrimaryKeyIndex(output, "TypeC", "key").getMatchingOrdinal(8));

        director.excludeReferencedObjects();

        combiner = new HollowCombiner(director, input1, input2, input3);
        combiner.setPrimaryKeys(new PrimaryKey("TypeB", "key", "c.key"), new PrimaryKey("TypeC", "key"));
        combiner.combine();
        output = StateEngineRoundTripper.roundTripSnapshot(combiner.getCombinedStateEngine());

        Assert.assertEquals(-1, new HollowPrimaryKeyIndex(output, "TypeA", "key").getMatchingOrdinal(9));
        Assert.assertEquals(-1, new HollowPrimaryKeyIndex(output, "TypeB", "key").getMatchingOrdinal(8));
        Assert.assertEquals(-1, new HollowPrimaryKeyIndex(output, "TypeC", "key").getMatchingOrdinal(8));
    }


    private void assertObject(HollowReadStateEngine output, int aKey, int expectAOrigin, int expectBKey, int expectBOrigin, int expectCKey, int expectCOrigin) {
        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(output, new PrimaryKey("TypeA", "key"));
        GenericHollowObject typeA = (GenericHollowObject) GenericHollowRecordHelper.instantiate(output, "TypeA", idx.getMatchingOrdinal(aKey));
        Assert.assertEquals(aKey, typeA.getInt("key"));
        Assert.assertEquals(expectAOrigin, typeA.getInt("origin"));

        GenericHollowObject typeB = (GenericHollowObject) typeA.getReferencedGenericRecord("b");
        Assert.assertEquals(expectBKey, typeB.getInt("key"));
        Assert.assertEquals(expectBOrigin, typeB.getInt("origin"));

        GenericHollowObject typeC = (GenericHollowObject) typeB.getReferencedGenericRecord("c");
        Assert.assertEquals(expectCKey, typeC.getInt("key"));
        Assert.assertEquals(expectCOrigin, typeC.getInt("origin"));
    }


    private void addObject(HollowObjectMapper mapper, int origin, int aKey, int bKey, int cKey) {
        mapper.add(new TypeA(aKey, origin, new TypeB(bKey, origin, new TypeC(cKey, origin))));
    }


    @SuppressWarnings("unused")
    private static class TypeA {
        private final int key;
        private final int origin;
        private final TypeB b;

        public TypeA(int key, int origin, TypeB b) {
            this.key = key;
            this.origin = origin;
            this.b = b;
        }
    }

    @SuppressWarnings("unused")
    private static class TypeB {
        private final int key;
        private final int origin;
        private final TypeC c;

        public TypeB(int key, int origin, TypeC c) {
            this.key = key;
            this.origin = origin;
            this.c = c;
        }
    }

    @SuppressWarnings("unused")
    private static class TypeC {
        private final int key;
        private final int origin;

        public TypeC(int key, int origin) {
            this.key = key;
            this.origin = origin;
        }

    }


}
