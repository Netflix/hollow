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

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.diff.count.HollowFieldDiff;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowDiffTest {

    HollowObjectSchema typeASchema;
    HollowObjectSchema typeBSchema;
    HollowObjectSchema typeCSchema;
    HollowObjectSchema typeDSchema;

    HollowListSchema listOfTypeCSchema;
    HollowSetSchema setOfTypeDSchema;
    HollowMapSchema mapOfTypeCToTypeDSchema;

    @Before
    public void setUp() {
        typeASchema = new HollowObjectSchema("TypeA", 3);
        typeASchema.addField("a1", FieldType.STRING);
        typeASchema.addField("a2", FieldType.REFERENCE, "TypeB");
        typeASchema.addField("a3", FieldType.REFERENCE, "MapOfTypeCToTypeD");

        typeBSchema = new HollowObjectSchema("TypeB", 3);
        typeBSchema.addField("b1", FieldType.INT);
        typeBSchema.addField("b2", FieldType.REFERENCE, "ListOfTypeC");
        typeBSchema.addField("b3", FieldType.REFERENCE, "SetOfTypeD");

        typeCSchema = new HollowObjectSchema("TypeC", 2);
        typeCSchema.addField("c1", FieldType.LONG);
        typeCSchema.addField("c2", FieldType.BOOLEAN);

        typeDSchema = new HollowObjectSchema("TypeD", 3, "d1", "d2");
        typeDSchema.addField("d1", FieldType.FLOAT);
        typeDSchema.addField("d2", FieldType.DOUBLE);
        typeDSchema.addField("d3", FieldType.BYTES);

        listOfTypeCSchema = new HollowListSchema("ListOfTypeC", "TypeC");
        setOfTypeDSchema = new HollowSetSchema("SetOfTypeD", "TypeD");
        mapOfTypeCToTypeDSchema = new HollowMapSchema("MapOfTypeCToTypeD", "TypeC", "TypeD");
    }

    @Test
    public void test() throws IOException {
        HollowWriteStateEngine fromStateEngine = newWriteStateEngine();
        HollowWriteStateEngine toStateEngine = newWriteStateEngine();

        //// FIRST OBJECT PAIR ////
        addRec(fromStateEngine,
                "recordOne", 1,
                cList(
                        c(1001, true),
                        c(1002, true),
                        c(1003, true)
                        ),
                dSet(
                        d(1.001f, 1.00001d, new byte[]{ 1, 1 }),
                        d(1.002f, 1.00002d, new byte[]{ 1, 2 })
                        ),
                map(
                        entry(
                                c(1001, true),
                                d(1.001f, 1.00001d, new byte[]{ 1, 1 })
                                ),
                        entry(
                                c(1002, true),
                                d(1.002f, 1.00002d, new byte[]{ 1, 2 })
                                )
                        ));

        addRec(toStateEngine,
                "recordOne", 1,
                cList(
                        c(1001, false), // now false instead of true
                        c(1002, true),
                        c(1003, false)  // now false instead of true
                        ),
                dSet(
                        d(1.001f, 1.00001d, new byte[]{ 1, 9 }), /// 9 instead of 1
                        d(1.002f, 1.00002d, new byte[]{ 1, 2 })
                        ),
                map(
                        entry(
                                c(1001, true),
                                d(1.001f, 1.00001d, new byte[]{ 1, 9 }) /// 9 instead of 1
                                ),
                        entry(
                                c(1002, true),
                                d(1.002f, 1.00002d, new byte[]{ 1, 2 })
                                )
                        ));

        //// SECOND OBJECT PAIR ////
        addRec(fromStateEngine,
                "recordTwo", 2,
                cList(
                        c(2001, true),
                        c(2002, true),
                        c(2003, true)
                        ),
                dSet(
                        d(2.001f, 2.00001d, new byte[]{ 2, 1 }),
                        d(2.002f, 2.00002d, new byte[]{ 2, 2 })
                        ),
                map(
                        entry(
                                c(2001, true),
                                d(2.001f, 2.00001d, new byte[]{ 2, 1 })
                                ),
                        entry(
                                c(2002, true),
                                d(2.002f, 2.00002d, new byte[]{ 2, 2 })
                                )
                        ));

        addRec(toStateEngine,
                "recordTwo", 2,
                cList(
                        c(2001, true),
                        c(2002, false), // now false instead of true
                        c(2003, true)
                        ),
                dSet(
                        d(2.001f, 2.00001d, new byte[]{ 2, 7 }), /// 7 instead of 1
                        d(2.002f, 2.00002d, new byte[]{ 2, 2 })
                        ),
                map(
                        entry(
                                c(2001, true),
                                d(2.001f, 2.00001d, new byte[]{ 2, 7 }) /// 7 instead of 1
                                ),
                        entry(
                                c(2002, true),
                                d(2.002f, 2.00002d, new byte[]{ 2, 2 })
                                )
                        ));


        //// UNPAIRED OBJECTS ////
        addRec(toStateEngine,
                "recordThree", 3,
                cList(), dSet(), map());
        addRec(fromStateEngine,
                "recordThree", 4,
                cList(), dSet(), map());


        HollowDiff diff = new HollowDiff(readEngine(fromStateEngine), readEngine(toStateEngine), false);
        HollowTypeDiff typeDiff = diff.addTypeDiff("TypeA");
        typeDiff.addMatchPath("a1");
        typeDiff.addMatchPath("a2.b1");
        diff.calculateDiffs();

        List<HollowFieldDiff> fieldDiffs = typeDiff.getFieldDiffs();

        Assert.assertEquals(3, fieldDiffs.size());
        assertContainsFieldDiff(fieldDiffs, "TypeA.a2.b2.element.c2 (BOOLEAN)", 2, 6);
        assertContainsFieldDiff(fieldDiffs, "TypeA.a2.b3.element.d3 (BYTES)", 2, 4);
        assertContainsFieldDiff(fieldDiffs, "TypeA.a3.value.d3 (BYTES)", 2, 4);


        Assert.assertEquals(1, typeDiff.getUnmatchedOrdinalsInTo().size());
        Assert.assertEquals(1, typeDiff.getUnmatchedOrdinalsInTo().size());
    }

    @Test
    public void testAutoDiscoverTypeDiffs() throws Exception {
        HollowWriteStateEngine fromStateEngine = newWriteStateEngine();
        HollowWriteStateEngine toStateEngine = newWriteStateEngine();
        HollowDiff diff = new HollowDiff(readEngine(fromStateEngine), readEngine(toStateEngine));

        Assert.assertEquals(1, diff.getTypeDiffs().size());

        HollowTypeDiff typeDDiff = diff.getTypeDiff("TypeD");
        Assert.assertNotNull(typeDDiff);

        HollowDiffMatcher matcher = typeDDiff.getMatcher();
        Assert.assertNotNull(matcher);

        List<String> matchPaths = matcher.getMatchPaths();
        Assert.assertEquals(2, matchPaths.size());
        Assert.assertEquals("d1", matchPaths.get(0));
        Assert.assertEquals("d2", matchPaths.get(1));
    }

    private void assertContainsFieldDiff(List<HollowFieldDiff> diffs, String fieldId, int numDiffPairs, int totalDiffScores) {
        for(HollowFieldDiff diff : diffs) {
            if(fieldId.equals(diff.getFieldIdentifier().toString())) {
                Assert.assertEquals(numDiffPairs, diff.getNumDiffs());
                Assert.assertEquals(totalDiffScores, diff.getTotalDiffScore());
                return;
            }
        }

        Assert.fail();
    }

    private HollowReadStateEngine readEngine(HollowWriteStateEngine writeEngine) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);

        HollowReadStateEngine readEngine = new HollowReadStateEngine(true);
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));

        return readEngine;
    }

    private HollowWriteStateEngine newWriteStateEngine() {
        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        stateEngine.addTypeState(new HollowObjectTypeWriteState(typeASchema));
        stateEngine.addTypeState(new HollowObjectTypeWriteState(typeBSchema));
        stateEngine.addTypeState(new HollowObjectTypeWriteState(typeCSchema));
        stateEngine.addTypeState(new HollowObjectTypeWriteState(typeDSchema));

        stateEngine.addTypeState(new HollowListTypeWriteState(listOfTypeCSchema));
        stateEngine.addTypeState(new HollowSetTypeWriteState(setOfTypeDSchema));
        stateEngine.addTypeState(new HollowMapTypeWriteState(mapOfTypeCToTypeDSchema));

        return stateEngine;
    }

    private int addRec(HollowWriteStateEngine stateEngine,
            String a1, int b1,
            TypeCRec[] typeCs,
            TypeDRec[] typeDs,
            MapEntry[] mapEntries) {
        int listOrdinal = addListRec(stateEngine, typeCs);
        int setOrdinal = addSetRec(stateEngine, typeDs);
        int bOrdinal = addBRec(stateEngine, b1, listOrdinal, setOrdinal);
        int mapOrdinal = addMapRec(stateEngine, mapEntries);
        return addARec(stateEngine, a1, bOrdinal, mapOrdinal);
    }

    private int addARec(HollowWriteStateEngine stateEngine, String a1, int bOrdinal, int mapOrdinal) {
        HollowObjectWriteRecord aRec = new HollowObjectWriteRecord(typeASchema);
        aRec.setString("a1", a1);
        aRec.setReference("a2", bOrdinal);
        aRec.setReference("a3", mapOrdinal);
        return stateEngine.add("TypeA", aRec);
    }

    private int addMapRec(HollowWriteStateEngine stateEngine, MapEntry[] mapEntries) {
        HollowMapWriteRecord mapRec = new HollowMapWriteRecord();
        for(MapEntry entry : mapEntries) {
            int cOrdinal = addCRec(stateEngine, entry.key);
            int dOrdinal = addDRec(stateEngine, entry.value);
            mapRec.addEntry(cOrdinal, dOrdinal);
        }
        int mapOrdinal = stateEngine.add(mapOfTypeCToTypeDSchema.getName(), mapRec);
        return mapOrdinal;
    }

    private int addBRec(HollowWriteStateEngine stateEngine, int b1, int listOrdinal, int setOrdinal) {
        HollowObjectWriteRecord bRec = new HollowObjectWriteRecord(typeBSchema);
        bRec.setInt("b1", b1);
        bRec.setReference("b2", listOrdinal);
        bRec.setReference("b3", setOrdinal);
        int bOrdinal = stateEngine.add("TypeB", bRec);
        return bOrdinal;
    }

    private int addListRec(HollowWriteStateEngine stateEngine, TypeCRec[] typeCs) {
        HollowListWriteRecord listRec = new HollowListWriteRecord();
        for(TypeCRec typeC : typeCs) {
            listRec.addElement(addCRec(stateEngine, typeC));
        }
        int listOrdinal = stateEngine.add(listOfTypeCSchema.getName(), listRec);
        return listOrdinal;
    }

    private int addSetRec(HollowWriteStateEngine stateEngine, TypeDRec[] typeDs) {
        HollowSetWriteRecord setRec = new HollowSetWriteRecord();
        for(TypeDRec typeD : typeDs) {
            setRec.addElement(addDRec(stateEngine, typeD));
        }
        int setOrdinal = stateEngine.add(setOfTypeDSchema.getName(), setRec);
        return setOrdinal;
    }

    private int addCRec(HollowWriteStateEngine stateEngine, TypeCRec typeC) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(typeCSchema);
        rec.setLong("c1", typeC.c1);
        rec.setBoolean("c2", typeC.c2);
        return stateEngine.add("TypeC", rec);
    }

    private int addDRec(HollowWriteStateEngine stateEngine, TypeDRec typeD) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(typeDSchema);
        rec.setFloat("d1", typeD.d1);
        rec.setDouble("d2", typeD.d2);
        rec.setBytes("d3", typeD.d3);
        return stateEngine.add("TypeD", rec);
    }

    private TypeCRec[] cList(TypeCRec... cs) {
        return cs;
    }

    private TypeDRec[] dSet(TypeDRec... ds) {
        return ds;
    }

    private MapEntry[] map(MapEntry... entries) {
        return entries;
    }

    private MapEntry entry(TypeCRec c, TypeDRec d) {
        MapEntry entry = new MapEntry();
        entry.key = c;
        entry.value = d;
        return entry;
    }

    private TypeCRec c(long c1, boolean c2) {
        TypeCRec rec = new TypeCRec();
        rec.c1 = c1;
        rec.c2 = c2;
        return rec;
    }

    private TypeDRec d(float d1, double d2, byte[] d3) {
        TypeDRec rec = new TypeDRec();
        rec.d1 = d1;
        rec.d2 = d2;
        rec.d3 = d3;
        return rec;
    }

    private static class MapEntry {
        TypeCRec key;
        TypeDRec value;
    }

    private static class TypeCRec {
        private long c1;
        private boolean c2;
    }

    private static class TypeDRec {
        private float d1;
        private double d2;
        private byte[] d3;
    }

}
