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
package com.netflix.hollow.core.write;

import com.netflix.hollow.api.error.IncompatibleSchemaException;
import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectTypeWriteStateLayoutTest {

    @Test
    public void rejectsRecordWithDifferentSchema() {
        HollowWriteStateEngine engine = engineWith(entitySchemaWithCatalogOwner());

        try {
            engine.add("Entity", entityWithoutCatalogOwner());
            Assert.fail("Expected an IncompatibleSchemaException");
        } catch (IncompatibleSchemaException expected) {
            Assert.assertTrue(expected.getMessage(),
                    expected.getMessage().contains("Entity") && expected.getMessage().contains("field 1 (catalogOwner STRING vs countries INT)"));
        }
    }

    @Test
    public void failsWriteWhenRejectedRecordIsSwallowed() {
        HollowWriteStateEngine engine = engineWith(entitySchemaWithCatalogOwner());
        try {
            engine.add("Entity", entityWithoutCatalogOwner());
        } catch (IncompatibleSchemaException ignored) {
        }

        assertWriteFails(engine, "Entity");
    }

    @Test
    public void acceptsRecordWithSameLayoutAndDifferentPrimaryKey() throws IOException {
        HollowWriteStateEngine engine = engineWith(entitySchemaWithCatalogOwner());
        HollowObjectSchema withPrimaryKey = new HollowObjectSchema("Entity", 3, "id");
        withPrimaryKey.addField("id", FieldType.LONG);
        withPrimaryKey.addField("catalogOwner", FieldType.STRING);
        withPrimaryKey.addField("countries", FieldType.INT);
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(withPrimaryKey);
        rec.setLong("id", 1);
        rec.setString("catalogOwner", "owner");
        rec.setInt("countries", 5);

        engine.add("Entity", rec);
        engine.add("Entity", rec);

        new HollowBlobWriter(engine).writeSnapshot(new ByteArrayOutputStream());
    }

    @Test
    public void failsWriteWhenSerializedRecordEndsBeforeLastField() {
        HollowObjectSchema schema = new HollowObjectSchema("Triple", 3);
        schema.addField("a", FieldType.INT);
        schema.addField("b", FieldType.INT);
        schema.addField("c", FieldType.INT);
        HollowWriteStateEngine engine = engineWith(schema);
        engine.add("Triple", rawRecord(new byte[] {2, 4}));

        assertWriteFails(engine, "Triple");
    }

    @Test
    public void failsWriteWhenSerializedRecordHasTrailingBytes() {
        HollowObjectSchema schema = new HollowObjectSchema("Single", 1);
        schema.addField("a", FieldType.INT);
        HollowWriteStateEngine engine = engineWith(schema);
        engine.add("Single", rawRecord(new byte[] {2, 4}));

        assertWriteFails(engine, "Single");
    }

    @Test
    public void failsWriteWhenShortRecordEndsOnSegmentBoundary() {
        // record entry = 2-byte length + 2-byte string length + chars, so it ends exactly at 2^k
        for (int k = 8; k <= 14; k++) {
            HollowObjectSchema schema = new HollowObjectSchema("Named", 2);
            schema.addField("name", FieldType.STRING);
            schema.addField("rank", FieldType.INT);
            HollowWriteStateEngine engine = engineWith(schema);
            engine.add("Named", rawRecord(serializedString((1 << k) - 4)));

            assertWriteFails(engine, "Named");
        }
    }

    @Test
    public void writesRecordsMatchingTypeSchema() throws IOException {
        HollowObjectSchema schema = entitySchemaWithCatalogOwner();
        HollowWriteStateEngine engine = engineWith(schema);

        HollowObjectWriteRecord full = new HollowObjectWriteRecord(schema);
        full.setLong("id", 1);
        full.setString("catalogOwner", "owner");
        full.setInt("countries", 5);
        engine.add("Entity", full);
        engine.add("Entity", new HollowObjectWriteRecord(schema));

        new HollowBlobWriter(engine).writeSnapshot(new ByteArrayOutputStream());
    }

    private static HollowWriteStateEngine engineWith(HollowObjectSchema schema) {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        engine.addTypeState(new HollowObjectTypeWriteState(schema));
        return engine;
    }

    private static HollowObjectSchema entitySchemaWithCatalogOwner() {
        HollowObjectSchema schema = new HollowObjectSchema("Entity", 3);
        schema.addField("id", FieldType.LONG);
        schema.addField("catalogOwner", FieldType.STRING);
        schema.addField("countries", FieldType.INT);
        return schema;
    }

    private static HollowObjectWriteRecord entityWithoutCatalogOwner() {
        HollowObjectSchema schema = new HollowObjectSchema("Entity", 2);
        schema.addField("id", FieldType.LONG);
        schema.addField("countries", FieldType.INT);
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setLong("id", 1);
        rec.setInt("countries", 5);
        return rec;
    }

    private static byte[] serializedString(int length) {
        ByteDataArray buf = new ByteDataArray();
        VarInt.writeVInt(buf, length);
        for (int i = 0; i < length; i++)
            buf.write((byte) 'a');
        byte[] bytes = new byte[(int) buf.length()];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = buf.get(i);
        return bytes;
    }

    private static HollowWriteRecord rawRecord(byte[] bytes) {
        return new HollowWriteRecord() {
            @Override
            public void writeDataTo(ByteDataArray buf) {
                for (byte b : bytes)
                    buf.write(b);
            }

            @Override
            public void reset() {
            }
        };
    }

    private static void assertWriteFails(HollowWriteStateEngine engine, String typeName) {
        try {
            new HollowBlobWriter(engine).writeSnapshot(new ByteArrayOutputStream());
            Assert.fail("Expected the write to fail for type " + typeName);
        } catch (Exception expected) {
            Throwable cause = rootCause(expected);
            Assert.assertTrue(String.valueOf(cause), cause instanceof IncompatibleSchemaException);
            Assert.assertTrue(cause.getMessage(), cause.getMessage().contains(typeName));
        }
    }

    private static Throwable rootCause(Throwable t) {
        while (t.getCause() != null && t.getCause() != t)
            t = t.getCause();
        return t;
    }
}
