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

import com.netflix.hollow.api.consumer.InMemoryAnnouncement;
import com.netflix.hollow.api.error.IncompatibleSchemaException;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class HollowTypeMapperSchemaMismatchTest {

    @Test
    public void rejectsWriteWhenFieldRemoved() {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        new HollowObjectMapper(engine).add(new EntityV1(1, 2, 3, 4));

        assertIncompatible("Entity", "field 2 (catalogOwner INT vs countries INT)",
                () -> new HollowObjectMapper(engine).add(new EntityV2(1, 2, 4)));
    }

    @Test
    public void rejectsWriteWhenFieldsReordered() {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        new HollowObjectMapper(engine).add(new EntityV1(1, 2, 3, 4));

        assertIncompatible("Entity", "field 1 (type INT vs catalogOwner INT)",
                () -> new HollowObjectMapper(engine).add(new EntityReordered(1, 3, 2, 4)));
    }

    @Test
    public void rejectsWriteWhenFieldTypeChanged() {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        new HollowObjectMapper(engine).add(new VideoV1(1, new Owner(7)));

        assertIncompatible("Video", "field 1 (catalogOwner REFERENCE(Owner) vs catalogOwner STRING)",
                () -> new HollowObjectMapper(engine).add(new VideoV2(1, "owner")));
    }

    @Test
    public void rejectsWriteWhenListElementTypeChanged() {
        HollowWriteStateEngine engine = engineWithOtherType("OtherString", FieldType.STRING);
        engine.addTypeState(new HollowListTypeWriteState(new HollowListSchema("ListOfString", "OtherString")));

        assertIncompatible("ListOfString", "element type (OtherString vs String)",
                () -> new HollowObjectMapper(engine).add(new TypeWithList(Collections.singletonList("a"))));
    }

    @Test
    public void rejectsWriteWhenSetElementTypeChanged() {
        HollowWriteStateEngine engine = engineWithOtherType("OtherString", FieldType.STRING);
        engine.addTypeState(new HollowSetTypeWriteState(new HollowSetSchema("SetOfString", "OtherString")));

        assertIncompatible("SetOfString", "element type (OtherString vs String)",
                () -> new HollowObjectMapper(engine).add(new TypeWithSet(Collections.singleton("a"))));
    }

    @Test
    public void rejectsWriteWhenMapValueTypeChanged() {
        HollowWriteStateEngine engine = engineWithOtherType("OtherInteger", FieldType.INT);
        engine.addTypeState(new HollowMapTypeWriteState(new HollowMapSchema("MapOfStringToInteger", "String", "OtherInteger")));

        assertIncompatible("MapOfStringToInteger", "value type (OtherInteger vs Integer)",
                () -> new HollowObjectMapper(engine).add(new TypeWithMap(Collections.singletonMap("a", 1))));
    }

    @Test
    public void failsWriteWhenRejectedWriteIsSwallowed() {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        new HollowObjectMapper(engine).add(new EntityV1(1, 2, 3, 4));
        try {
            new HollowObjectMapper(engine).add(new EntityV2(1, 2, 4));
        } catch (IncompatibleSchemaException ignored) {
        }

        try {
            new HollowBlobWriter(engine).writeSnapshot(new ByteArrayOutputStream());
            Assert.fail("Expected the write to fail");
        } catch (Exception expected) {
            Assert.assertTrue(String.valueOf(expected), rootCause(expected) instanceof IncompatibleSchemaException);
        }
    }

    @Test
    public void producerAnnouncesNothingUntilMapperMatchesWriteState() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        InMemoryAnnouncement announcement = new InMemoryAnnouncement();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withAnnouncer(announcement)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        long v1 = producer.runCycle(ws -> ws.add(new EntityV1(1, 2, 3, 4)));

        // a fresh mapper for a new data model on the existing write engine, as hollowservice does
        assertCycleFails(producer, ws -> new HollowObjectMapper(ws.getStateEngine()).add(new EntityV2(1, 2, 4)));
        assertCycleFails(producer, ws -> {
            try {
                new HollowObjectMapper(ws.getStateEngine()).add(new EntityV2(1, 2, 4));
            } catch (RuntimeException ignored) {
            }
        });
        Assert.assertEquals(v1, announcement.getLatestVersion());

        long v2 = producer.runCycle(ws -> ws.add(new EntityV1(5, 6, 7, 8)));
        Assert.assertEquals(v2, announcement.getLatestVersion());
    }

    @Test
    public void allowsNewMapperWhenLayoutMatches() {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        new HollowObjectMapper(engine).add(new EntityV1(1, 2, 3, 4));
        new HollowObjectMapper(engine).add(new EntityV1(5, 6, 7, 8));
        new HollowObjectMapper(engine).add(new EntityV1WithPrimaryKey(9, 10, 11, 12));
    }

    @Test
    public void allowsRegisteringMismatchedTypeWithoutWriting() throws IOException {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        new HollowObjectMapper(engine).add(new EntityV1(1, 2, 3, 4));

        new HollowObjectMapper(engine).initializeTypeState(EntityV2.class);

        new HollowBlobWriter(engine).writeSnapshot(new ByteArrayOutputStream());
    }

    private static HollowWriteStateEngine engineWithOtherType(String typeName, FieldType fieldType) {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        HollowObjectSchema schema = new HollowObjectSchema(typeName, 1);
        schema.addField("value", fieldType);
        engine.addTypeState(new HollowObjectTypeWriteState(schema));
        return engine;
    }

    private static void assertIncompatible(String typeName, String difference, Runnable write) {
        try {
            write.run();
            Assert.fail("Expected an IncompatibleSchemaException for type " + typeName);
        } catch (IncompatibleSchemaException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains(typeName));
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains(difference));
        }
    }

    private static void assertCycleFails(HollowProducer producer, HollowProducer.Populator populator) {
        try {
            producer.runCycle(populator);
            Assert.fail("Expected the cycle to fail");
        } catch (RuntimeException expected) {
            Assert.assertTrue(String.valueOf(expected), rootCause(expected) instanceof IncompatibleSchemaException);
        }
    }

    private static Throwable rootCause(Throwable t) {
        while (t.getCause() != null && t.getCause() != t)
            t = t.getCause();
        return t;
    }

    @HollowTypeName(name = "Entity")
    static class EntityV1 {
        final long id;
        final int type;
        final int catalogOwner;
        final int countries;

        EntityV1(long id, int type, int catalogOwner, int countries) {
            this.id = id;
            this.type = type;
            this.catalogOwner = catalogOwner;
            this.countries = countries;
        }
    }

    @HollowTypeName(name = "Entity")
    @HollowPrimaryKey(fields = "id")
    static class EntityV1WithPrimaryKey {
        final long id;
        final int type;
        final int catalogOwner;
        final int countries;

        EntityV1WithPrimaryKey(long id, int type, int catalogOwner, int countries) {
            this.id = id;
            this.type = type;
            this.catalogOwner = catalogOwner;
            this.countries = countries;
        }
    }

    @HollowTypeName(name = "Entity")
    static class EntityV2 {
        final long id;
        final int type;
        final int countries;

        EntityV2(long id, int type, int countries) {
            this.id = id;
            this.type = type;
            this.countries = countries;
        }
    }

    @HollowTypeName(name = "Entity")
    static class EntityReordered {
        final long id;
        final int catalogOwner;
        final int type;
        final int countries;

        EntityReordered(long id, int catalogOwner, int type, int countries) {
            this.id = id;
            this.catalogOwner = catalogOwner;
            this.type = type;
            this.countries = countries;
        }
    }

    static class Owner {
        final int value;

        Owner(int value) {
            this.value = value;
        }
    }

    @HollowTypeName(name = "Video")
    static class VideoV1 {
        final long id;
        final Owner catalogOwner;

        VideoV1(long id, Owner catalogOwner) {
            this.id = id;
            this.catalogOwner = catalogOwner;
        }
    }

    @HollowTypeName(name = "Video")
    static class VideoV2 {
        final long id;
        @HollowInline
        final String catalogOwner;

        VideoV2(long id, String catalogOwner) {
            this.id = id;
            this.catalogOwner = catalogOwner;
        }
    }

    static class TypeWithList {
        final List<String> values;

        TypeWithList(List<String> values) {
            this.values = values;
        }
    }

    static class TypeWithSet {
        final Set<String> values;

        TypeWithSet(Set<String> values) {
            this.values = values;
        }
    }

    static class TypeWithMap {
        final Map<String, Integer> values;

        TypeWithMap(Map<String, Integer> values) {
            this.values = values;
        }
    }
}
