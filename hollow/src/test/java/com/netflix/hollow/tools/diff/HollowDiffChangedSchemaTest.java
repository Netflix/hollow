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
import org.junit.Before;
import org.junit.Test;

public class HollowDiffChangedSchemaTest {

    HollowObjectSchema typeASchema1;
    HollowObjectSchema typeASchema2;
    HollowObjectSchema typeBSchema;

    HollowListSchema listOfTypeCSchema;
    HollowSetSchema setOfTypeCSchema;
    HollowMapSchema mapOfTypeCSchema;
    HollowObjectSchema typeCSchema;

    HollowWriteStateEngine stateEngine1;
    HollowWriteStateEngine stateEngine2;


    @Before
    public void setUp() {
        typeASchema1 = new HollowObjectSchema("TypeA", 3);
        typeASchema1.addField("key", FieldType.BYTES);
        typeASchema1.addField("b", FieldType.REFERENCE, "TypeB");
        typeASchema1.addField("doubleVal", FieldType.DOUBLE);

        typeASchema2 = new HollowObjectSchema("TypeA", 6);
        typeASchema2.addField("key", FieldType.BYTES);
        typeASchema2.addField("b", FieldType.REFERENCE, "TypeB");
        typeASchema2.addField("c", FieldType.REFERENCE, "TypeC");
        typeASchema2.addField("cList", FieldType.REFERENCE, "ListOfTypeC");
        typeASchema2.addField("cSet", FieldType.REFERENCE, "SetOfTypeC");
        typeASchema2.addField("cMap", FieldType.REFERENCE, "MapOfTypeC");

        typeBSchema = new HollowObjectSchema("TypeB", 1);
        typeBSchema.addField("longVal", FieldType.LONG);

        listOfTypeCSchema = new HollowListSchema("ListOfTypeC", "TypeC");
        setOfTypeCSchema = new HollowSetSchema("SetOfTypeC", "TypeC");
        mapOfTypeCSchema = new HollowMapSchema("MapOfTypeC", "TypeC", "TypeC");


        typeCSchema = new HollowObjectSchema("TypeC", 1);
        typeCSchema.addField("stringVal", FieldType.STRING);

        stateEngine1 = new HollowWriteStateEngine();
        stateEngine1.addTypeState(new HollowObjectTypeWriteState(typeASchema1));
        stateEngine1.addTypeState(new HollowObjectTypeWriteState(typeBSchema));

        stateEngine2 = new HollowWriteStateEngine();
        stateEngine2.addTypeState(new HollowObjectTypeWriteState(typeASchema2));
        stateEngine2.addTypeState(new HollowObjectTypeWriteState(typeBSchema));
        stateEngine2.addTypeState(new HollowListTypeWriteState(listOfTypeCSchema));
        stateEngine2.addTypeState(new HollowSetTypeWriteState(setOfTypeCSchema));
        stateEngine2.addTypeState(new HollowMapTypeWriteState(mapOfTypeCSchema));
        stateEngine2.addTypeState(new HollowObjectTypeWriteState(typeCSchema));

    }


    @Test
    @SuppressWarnings("unused")
    public void testDiffWithChangedSchemas() throws IOException {
        int a1_0 = a1(new byte[] {1, 2, 3}, 1024L, 1020.3523d);
        int a2_0 = a2(new byte[] {1, 2, 3}, 1024L,
                list(c("list1"), c("list2")),
                set(c("list1"), c("set1"), c("set2")),
                map(c("key1"), c("val1"),
                    c("key2"), c("val2"),
                    c("key3"), c("val3")),
                c("singleton1"));


        int a1_1 = a1(new byte[] {2, 4, 6}, 12345678L, 1020.3523d);
        int a2_1 = a2(new byte[] {2, 4, 6}, 123456L,
                list(c("list3")),
                set(c("set4"), c("set5"), c("set6"), c("set7")),
                map(c("key10"), c("val10"),
                    c("key20"), c("val20")),
                c("singleton2"));

        HollowDiff diff = diff();
        HollowTypeDiff typeDiff = diff.getTypeDiffs().get(0);

        for(HollowFieldDiff fieldDiff : typeDiff.getFieldDiffs()) {
            System.out.println(fieldDiff.getFieldIdentifier() + ": " + fieldDiff.getTotalDiffScore());
            for(int i=0;i<fieldDiff.getNumDiffs();i++) {
                System.out.println("    " + fieldDiff.getFromOrdinal(i) + "," + fieldDiff.getToOrdinal(i) + ": " + fieldDiff.getPairScore(i));
            }
        }
    }

    private int a1(byte[] key, long bVal, double d) {
        HollowObjectWriteRecord bRec = new HollowObjectWriteRecord(typeBSchema);
        bRec.setLong("longVal", bVal);
        int bOrdinal = stateEngine1.add("TypeB", bRec);

        HollowObjectWriteRecord aRec = new HollowObjectWriteRecord(typeASchema1);
        aRec.setBytes("key", key);
        aRec.setReference("b", bOrdinal);
        aRec.setDouble("doubleVal", d);

        return stateEngine1.add("TypeA", aRec);
    }

    private int a2(byte[] key, long bVal, int listOrdinal, int setOrdinal, int mapOrdinal, int cOrdinal) {
        HollowObjectWriteRecord bRec = new HollowObjectWriteRecord(typeBSchema);
        bRec.setLong("longVal", bVal);
        int bOrdinal = stateEngine2.add("TypeB", bRec);

        HollowObjectWriteRecord aRec = new HollowObjectWriteRecord(typeASchema2);
        aRec.setBytes("key", key);
        aRec.setReference("b", bOrdinal);
        aRec.setReference("c", cOrdinal);
        aRec.setReference("cList", listOrdinal);
        aRec.setReference("cSet", setOrdinal);
        aRec.setReference("cMap", mapOrdinal);
        return stateEngine2.add("TypeA", aRec);
    }

    private int list(int... cOrdinals) {
        HollowListWriteRecord listRec = new HollowListWriteRecord();
        for(int ordinal : cOrdinals) {
            listRec.addElement(ordinal);
        }
        return stateEngine2.add("ListOfTypeC", listRec);
    }

    private int set(int... cOrdinals) {
        HollowSetWriteRecord setRec = new HollowSetWriteRecord();
        for(int ordinal : cOrdinals) {
            setRec.addElement(ordinal);
        }
        return stateEngine2.add("SetOfTypeC", setRec);
    }

    private int map(int... cOrdinals) {
        HollowMapWriteRecord mapRec = new HollowMapWriteRecord();
        for(int i=0;i<cOrdinals.length;i+=2) {
            mapRec.addEntry(cOrdinals[i], cOrdinals[i+1]);
        }
        return stateEngine2.add("MapOfTypeC", mapRec);
    }

    private int c(String val) {
        HollowObjectWriteRecord cRec = new HollowObjectWriteRecord(typeCSchema);
        cRec.setString("stringVal", val);
        return stateEngine2.add("TypeC", cRec);
    }

    private HollowDiff diff() throws IOException {
        HollowDiff diff = new HollowDiff(roundTrip(stateEngine1), roundTrip(stateEngine2));
        HollowTypeDiff typeDiff = diff.addTypeDiff("TypeA");
        typeDiff.addMatchPath("key");
        diff.calculateDiffs();

        return diff;
    }

    private final HollowReadStateEngine roundTrip(HollowWriteStateEngine stateEngine) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        writer.writeSnapshot(baos);

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine(true);
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
        return readStateEngine;
    }
}
