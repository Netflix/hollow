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

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.tools.diff.count.HollowFieldDiff;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowDiffShortcutTest {

    @Test
    public void testShortcutType() throws Exception {
        HollowReadStateEngine from = createStateEngine(false,
                new TypeA(1, 1, 1),
                new TypeA(2, 2, 2),
                new TypeA(3, 3, 3),
                new TypeA(4, 4, 4),
                new TypeA(5, 5, 5),
                new TypeA(6)
        );

        HollowReadStateEngine to = createStateEngine(false,
                new TypeA(1, 1, 1),
                new TypeA(2, 2, 3),
                new TypeA(3, 4, 4),
                new TypeA(4, 4, 4),
                new TypeA(5),
                new TypeA(6, 7, 8)
        );


        HollowDiff diff = new HollowDiff(from, to);
        HollowTypeDiff typeADiff = diff.getTypeDiff("TypeA");
        typeADiff.addShortcutType("TypeB");

        diff.calculateDiffs();

        Assert.assertEquals(1, typeADiff.getFieldDiffs().size());

        HollowFieldDiff aFieldDiff = typeADiff.getFieldDiffs().get(0);

        Assert.assertEquals("TypeA.b (TypeB)", aFieldDiff.getFieldIdentifier().toString());
        Assert.assertEquals(4, aFieldDiff.getNumDiffs());

    }

    @Test
    public void testShortcutTypeMissingField() throws Exception {
        HollowReadStateEngine from = createStateEngine(false,
                new TypeA(1, 1, 1),
                new TypeA(2, 2, 2),
                new TypeA(3, 3, 3),
                new TypeA(4, 4, 4),
                new TypeA(5, 5, 5),
                new TypeA(6)
        );

        HollowReadStateEngine to = createStateEngine(
                new TypeAKeyOnly(1),
                new TypeAKeyOnly(2),
                new TypeAKeyOnly(3),
                new TypeAKeyOnly(4),
                new TypeAKeyOnly(5),
                new TypeAKeyOnly(6)
        );


        HollowDiff diff = new HollowDiff(from, to);
        HollowTypeDiff typeADiff = diff.getTypeDiff("TypeA");
        typeADiff.addShortcutType("TypeB");
        diff.calculateDiffs();

        Assert.assertEquals(1, typeADiff.getFieldDiffs().size());

        HollowFieldDiff aFieldDiff = typeADiff.getFieldDiffs().get(0);

        Assert.assertEquals("TypeA.b (TypeB)", aFieldDiff.getFieldIdentifier().toString());
        Assert.assertEquals(5, aFieldDiff.getNumDiffs());
    }

    @Test
    public void testShortcutTypeMissingHierarchicalField() throws Exception {
        HollowReadStateEngine from = createStateEngine(
                new Type0KeyOnly(1),
                new Type0KeyOnly(2),
                new Type0KeyOnly(3),
                new Type0KeyOnly(4),
                new Type0KeyOnly(5),
                new Type0KeyOnly(6)
        );

        HollowReadStateEngine to = createStateEngine(true,
                new TypeA(1, 1, 1),
                new TypeA(2, 2, 2),
                new TypeA(3, 3, 3),
                new TypeA(4, 4, 4),
                new TypeA(5, 5, 5),
                new TypeA(6)
        );

        HollowDiff diff = new HollowDiff(from, to, false);
        diff.addTypeDiff("Type0", "key").addShortcutType("TypeB");
        diff.addTypeDiff("TypeB", "key");
        diff.calculateDiffs();

        HollowTypeDiff typeADiff = diff.getTypeDiff("Type0");

        Assert.assertEquals(2, typeADiff.getFieldDiffs().size());

        HollowFieldDiff keyFieldDiff = typeADiff.getFieldDiffs().get(0);
        HollowFieldDiff bFieldDiff = typeADiff.getFieldDiffs().get(1);

        Assert.assertEquals("Type0.a.key (INT)", keyFieldDiff.getFieldIdentifier().toString());
        Assert.assertEquals(6, keyFieldDiff.getNumDiffs());
        Assert.assertEquals("Type0.a.b (TypeB)", bFieldDiff.getFieldIdentifier().toString());
        Assert.assertEquals(5, bFieldDiff.getNumDiffs());
    }

    private HollowReadStateEngine createStateEngine(boolean addType0, TypeA... typeAs) throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        for(TypeA a : typeAs) {
            mapper.add(addType0 ? new Type0(a) : a);
        }

        return StateEngineRoundTripper.roundTripSnapshot(writeEngine);
    }

    private HollowReadStateEngine createStateEngine(TypeAKeyOnly... typeAs) throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.initializeTypeState(TypeB.class);

        for(TypeAKeyOnly a : typeAs) {
            mapper.add(a);
        }

        return StateEngineRoundTripper.roundTripSnapshot(writeEngine);
    }

    private HollowReadStateEngine createStateEngine(Type0KeyOnly... type0s) throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.initializeTypeState(TypeB.class);

        for(Type0KeyOnly a : type0s) {
            mapper.add(a);
        }

        return StateEngineRoundTripper.roundTripSnapshot(writeEngine);
    }

    @HollowPrimaryKey(fields = "key")
    private static class TypeA {
        int key;
        TypeB b;

        public TypeA(int key) {
            this.key = key;
            this.b = null;
        }

        public TypeA(int key, int bKey, int data) {
            this.key = key;
            this.b = new TypeB();
            b.key = bKey;
            b.data = data;
        }
    }

    @SuppressWarnings("unused")
    private static class TypeB {
        int key;
        int data;
    }

    @SuppressWarnings("unused")
    @HollowTypeName(name = "TypeA")
    @HollowPrimaryKey(fields = "key")
    private static class TypeAKeyOnly {
        int key;

        public TypeAKeyOnly(int key) {
            this.key = key;
        }
    }

    @SuppressWarnings("unused")
    private static class Type0 {
        int key;
        TypeA a;

        public Type0(TypeA a) {
            this.key = a.key;
            this.a = a;
        }
    }

    @SuppressWarnings("unused")
    @HollowTypeName(name = "Type0")
    private static class Type0KeyOnly {
        int key;

        public Type0KeyOnly(int key) {
            this.key = key;
        }
    }
}
