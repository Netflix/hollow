/*
 *  Copyright 2016-2026 Netflix, Inc.
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
package com.netflix.hollow.jsonadapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowSet;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordDumper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.core.write.objectmapper.flatrecords.HollowSchemaIdentifierMapper;
import com.netflix.hollow.tools.stringifier.HollowRecordJsonStringifier;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Test;

/// Maps given in the array form: [{"key": ..., "value": ...}]
public class HollowJsonAdapterStructuredMapTest {

    @Test
    public void scalarKeysAndValuesOfSingleFieldTypesAreParsed() throws IOException {
        HollowReadStateEngine state = parse("{\"id\":\"a1\","
                + "\"scopes\":[{\"key\":\"REGION\",\"value\":[\"r1\",\"r2\"]},{\"key\":\"STUDIO\",\"value\":[\"s1\"]}],"
                + "\"labels\":[{\"key\":{\"a\":1,\"b\":2},\"value\":\"x\"}]}");

        assertEquals("{REGION=[r1, r2], STUDIO=[s1]}", scopes(state).toString());
        assertEquals("{1,2=x}", labels(state).toString());
    }

    @Test
    public void nestedKeysAndValuesAreParsed() throws IOException {
        HollowReadStateEngine state = parse("{\"id\":\"a1\","
                + "\"scopes\":[{\"key\":{\"_name\":\"REGION\"},\"value\":[\"r1\"]}],"
                + "\"labels\":[{\"key\":{\"a\":1,\"b\":2},\"value\":{\"value\":\"x\"}}]}");

        assertEquals("{REGION=[r1]}", scopes(state).toString());
        assertEquals("{1,2=x}", labels(state).toString());
    }

    @Test
    public void unknownEntryMembersAreSkipped() throws IOException {
        HollowReadStateEngine state = parse("{\"id\":\"a1\",\"scopes\":[{\"note\":{\"a\":[1]},\"key\":\"REGION\",\"value\":[\"r1\"]}]}");

        assertEquals("{REGION=[r1]}", scopes(state).toString());
    }

    @Test
    public void entriesWithoutKeyOrValueAreRejected() {
        for(String scopes : new String[] { "[{\"key\":null,\"value\":[\"r1\"]}]", "[{\"value\":[\"r1\"]}]", "[{\"key\":\"REGION\"}]", "[{\"key\":\"REGION\",\"value\":null}]" })
            assertRejected("{\"id\":\"a1\",\"scopes\":" + scopes + "}", "MapOfScopeTypeToSetOfString");
    }

    @Test
    public void scalarsForOtherTypesAreRejected() {
        assertRejected("{\"id\":\"a1\",\"labels\":[{\"key\":\"1\",\"value\":\"x\"}]}", "Pair");
        assertRejected("{\"id\":\"a1\",\"scopes\":[{\"key\":\"REGION\",\"value\":\"r1\"}]}", "SetOfString");
    }

    @Test
    public void stringifiedRecordsCanBeParsedBack() throws IOException {
        Assignment assignment = new Assignment();
        assignment.id = "a1";
        assignment.scopes = Collections.singletonMap(ScopeType.REGION, (Set<String>) new HashSet<>(Collections.singleton("r1")));
        assignment.labels = Collections.singletonMap(new Pair(1, 2), "x");

        HollowWriteStateEngine engine = newEngine();
        new HollowObjectMapper(engine).add(assignment);
        HollowReadStateEngine expected = StateEngineRoundTripper.roundTripSnapshot(engine);

        HollowReadStateEngine actual = parse(new HollowRecordJsonStringifier().stringify(expected, "Assignment", 0));

        assertEquals(scopes(expected), scopes(actual));
        assertEquals(labels(expected), labels(actual));
    }

    @Test
    public void flatRecordsCanBeDumped() throws IOException {
        HollowWriteStateEngine schemas = newEngine();
        FlatRecordWriter flatRecordWriter = new FlatRecordWriter(schemas, new SchemaIdMapper(schemas.getSchemas()));
        new HollowJsonAdapter(schemas, "Assignment")
                .processRecord("{\"id\":\"a1\",\"scopes\":[{\"key\":\"REGION\",\"value\":[\"r1\"]}]}", flatRecordWriter);

        HollowWriteStateEngine engine = newEngine();
        new FlatRecordDumper(engine).dump(flatRecordWriter.generateFlatRecord());

        assertEquals("{REGION=[r1]}", scopes(StateEngineRoundTripper.roundTripSnapshot(engine)).toString());
    }

    private static HollowReadStateEngine parse(String json) throws IOException {
        HollowWriteStateEngine engine = newEngine();
        new HollowJsonAdapter(engine, "Assignment").processRecord(json);
        return StateEngineRoundTripper.roundTripSnapshot(engine);
    }

    private static void assertRejected(String json, String expectedInMessage) {
        try {
            parse(json);
            fail("Expected to be rejected: " + json);
        } catch(IOException expected) {
            Throwable cause = expected;
            while(cause.getCause() != null)
                cause = cause.getCause();
            assertTrue(cause.getMessage(), cause.getMessage().contains(expectedInMessage));
        }
    }

    private static HollowWriteStateEngine newEngine() {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        new HollowObjectMapper(engine).initializeTypeState(Assignment.class);
        return engine;
    }

    private static Map<String, Set<String>> scopes(HollowReadStateEngine state) {
        Map<String, Set<String>> scopes = new TreeMap<>();
        for(Map.Entry<HollowRecord, HollowRecord> entry : new GenericHollowObject(state, "Assignment", 0).getMap("scopes").entrySet()) {
            Set<String> values = new TreeSet<>();
            for(HollowRecord value : (GenericHollowSet) entry.getValue())
                values.add(((GenericHollowObject) value).getString("value"));
            scopes.put(((GenericHollowObject) entry.getKey()).getString("_name"), values);
        }
        return scopes;
    }

    private static Map<String, String> labels(HollowReadStateEngine state) {
        Map<String, String> labels = new TreeMap<>();
        for(Map.Entry<HollowRecord, HollowRecord> entry : new GenericHollowObject(state, "Assignment", 0).getMap("labels").entrySet()) {
            GenericHollowObject key = (GenericHollowObject) entry.getKey();
            labels.put(key.getInt("a") + "," + key.getInt("b"), ((GenericHollowObject) entry.getValue()).getString("value"));
        }
        return labels;
    }

    enum ScopeType { REGION, STUDIO }

    static class Pair {
        int a;
        int b;

        Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    @HollowPrimaryKey(fields = "id")
    static class Assignment {
        @HollowInline String id;
        Map<ScopeType, Set<String>> scopes;
        Map<Pair, String> labels;
    }

    private static class SchemaIdMapper implements HollowSchemaIdentifierMapper {
        private final List<HollowSchema> schemas;

        SchemaIdMapper(List<HollowSchema> schemas) {
            this.schemas = schemas;
        }

        @Override
        public HollowSchema getSchema(int identifier) {
            return schemas.get(identifier);
        }

        @Override
        public FieldType[] getPrimaryKeyFieldTypes(int identifier) {
            return new FieldType[] { FieldType.STRING };
        }

        @Override
        public int getSchemaId(HollowSchema schema) {
            return schemas.indexOf(schema);
        }
    }
}
