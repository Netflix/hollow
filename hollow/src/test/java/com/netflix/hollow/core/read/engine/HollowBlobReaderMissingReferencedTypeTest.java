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
package com.netflix.hollow.core.read.engine;

import static com.netflix.hollow.core.read.filter.TypeFilter.newTypeFilter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.filter.FilteredHollowBlobWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;

public class HollowBlobReaderMissingReferencedTypeTest {

    @Test
    public void readsSnapshotWithRecursiveFilterWhenReferencedTypeSchemaIsAbsent() throws IOException {
        // 1. Produce a normal snapshot where Parent references Child, and both types have records.
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.add(new Parent(1, new Child(11)));
        mapper.add(new Parent(2, new Child(22)));

        ByteArrayOutputStream snapshot = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(snapshot);

        // 2. Filter-write a blob that DROPS the Child type from the header but keeps Parent (whose
        //    'child' field still references the now-absent Child) -- the shape RawHollow emits for an
        //    empty nested type. getFilteredSchemaList does not enforce referential closure, so the
        //    resulting header legitimately omits a referenced type.
        HollowFilterConfig excludeChild = new HollowFilterConfig(true); // exclude filter
        excludeChild.addType("Child");
        ByteArrayOutputStream filtered = new ByteArrayOutputStream();
        new FilteredHollowBlobWriter(excludeChild)
                .filterSnapshot(new ByteArrayInputStream(snapshot.toByteArray()), filtered);
        byte[] blobMissingChild = filtered.toByteArray();

        // 3. Read that blob with a recursive type filter that walks Parent -> child -> (missing) Child.
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);

        assertThatCode(() ->
                reader.readSnapshot(
                        HollowBlobInput.serial(blobMissingChild),
                        newTypeFilter().excludeAll().includeRecursive("Parent").build()))
                .doesNotThrowAnyException();

        // 4. Parent is loaded and readable; the missing Child type is simply not in the read state.
        assertThat(readEngine.getSchema("Child")).isNull();
        assertThat(readEngine.getSchema("Parent")).isNotNull();
        assertThat(readEngine.getTypeState("Parent")).isNotNull();
        assertThat(readEngine.getTypeState("Parent").getPopulatedOrdinals().cardinality()).isEqualTo(2);

        int firstOrdinal = readEngine.getTypeState("Parent").getPopulatedOrdinals().nextSetBit(0);
        assertThat(new GenericHollowObject(readEngine, "Parent", firstOrdinal).getInt("id")).isIn(1, 2);
    }

    @SuppressWarnings("unused")
    private static class Parent {
        int id;
        Child child;

        Parent(int id, Child child) {
            this.id = id;
            this.child = child;
        }
    }

    @SuppressWarnings("unused")
    private static class Child {
        int value;

        Child(int value) {
            this.value = value;
        }
    }
}
