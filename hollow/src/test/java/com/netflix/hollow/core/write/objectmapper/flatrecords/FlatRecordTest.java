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
package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FlatRecordTest {
    private HollowObjectMapper mapper;
    private FlatRecordWriter flatRecordWriter;

    @Before
    public void setUp() {
        mapper = new HollowObjectMapper(new HollowWriteStateEngine());
        mapper.initializeTypeState(TestRecord.class);
        HollowSchemaIdentifierMapper schemaIdMapper = new FakeHollowSchemaIdentifierMapper(mapper.getStateEngine());
        flatRecordWriter = new FlatRecordWriter(mapper.getStateEngine(), schemaIdMapper);
    }

    @Test
    public void independentlyGeneratedRecordsWithIdenticalBytesAreEqual() {
        FlatRecord first = flatten(new TestRecord(1, "same"));
        FlatRecord second = flatten(new TestRecord(1, "same"));

        Assert.assertEquals(first, first);
        Assert.assertEquals(first, second);
        Assert.assertEquals(second, first);
    }

    @Test
    public void recordsWithDifferentBytesAreNotEqual() {
        FlatRecord first = flatten(new TestRecord(1, "same"));
        FlatRecord second = flatten(new TestRecord(2, "same"));

        Assert.assertNotEquals(first, second);
    }

    @Test
    public void recordsAreNotEqualToNullOrUnrelatedValues() {
        FlatRecord record = flatten(new TestRecord(1, "value"));

        Assert.assertNotEquals(record, null);
        Assert.assertNotEquals(record, "value");
    }

    @Test
    public void independentlyGeneratedEqualRecordsHaveMatchingHashCodes() {
        FlatRecord first = flatten(new TestRecord(1, "same"));
        FlatRecord second = flatten(new TestRecord(1, "same"));

        Assert.assertEquals(first.hashCode(), second.hashCode());
    }

    private FlatRecord flatten(TestRecord record) {
        flatRecordWriter.reset();
        mapper.writeFlat(record, flatRecordWriter);
        return flatRecordWriter.generateFlatRecord();
    }

    public static class TestRecord {
        int id;
        @HollowInline
        String value;

        public TestRecord(int id, String value) {
            this.id = id;
            this.value = value;
        }
    }
}
