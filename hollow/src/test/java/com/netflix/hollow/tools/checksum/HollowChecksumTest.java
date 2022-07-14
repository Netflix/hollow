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
package com.netflix.hollow.tools.checksum;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowChecksumTest {

    HollowReadStateEngine readEngine1;
    HollowReadStateEngine readEngine2;

    @Before
    public void setUp() throws IOException {
        HollowObjectSchema schema1 = new HollowObjectSchema("TypeA", 3);
        HollowObjectSchema schema2 = new HollowObjectSchema("TypeA", 3);

        schema1.addField("a1", FieldType.INT);
        schema2.addField("a1", FieldType.INT);
        schema1.addField("a4", FieldType.FLOAT);
        schema2.addField("a4", FieldType.FLOAT);
        schema1.addField("a2", FieldType.STRING);
        schema2.addField("a3", FieldType.LONG);

        readEngine1 = createStateEngine(schema1);
        readEngine2 = createStateEngine(schema2);
    }

    @Test
    public void checksumsCanBeEvaluatedAcrossObjectTypesWithDifferentSchemas() {
        HollowChecksum cksum1 = HollowChecksum.forStateEngineWithCommonSchemas(readEngine1, readEngine2);
        HollowChecksum cksum2 = HollowChecksum.forStateEngineWithCommonSchemas(readEngine2, readEngine1);

        Assert.assertEquals(cksum1, cksum2);
    }


    private HollowReadStateEngine createStateEngine(HollowObjectSchema schema) throws IOException {
        HollowWriteStateEngine writeState = new HollowWriteStateEngine();
        writeState.addTypeState(new HollowObjectTypeWriteState(schema));

        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);

        for(int i = 0; i < 100; i++) {
            rec.reset();
            rec.setInt("a1", i);
            rec.setFloat("a4", (float) i);
            if(schema.getPosition("a2") != -1)
                rec.setString("a2", String.valueOf(i));
            if(schema.getPosition("a3") != -1)
                rec.setLong("a3", i);

            writeState.add(schema.getName(), rec);
        }

        return StateEngineRoundTripper.roundTripSnapshot(writeState);
    }


}
