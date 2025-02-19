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
package com.netflix.hollow.tools.stringifier;

import static com.netflix.hollow.tools.stringifier.HollowStringifier.INDENT;
import static com.netflix.hollow.tools.stringifier.HollowStringifier.NEWLINE;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.TypeE;
import com.netflix.hollow.test.model.TestTypeA;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class HollowRecordJsonStringifierTest extends AbstractHollowRecordStringifierTest {
    @Test
    public void testStringifyTypeWithString() throws IOException {
        String msg = "String types should be printed correctly";
        Assert.assertEquals(msg, "\"foo\"",
                stringifyType(TypeWithString.class, true, false, new TypeWithString("foo")));
        Assert.assertEquals(msg, "{" + NEWLINE
                        + INDENT + "\"value\": {" + NEWLINE
                        + INDENT + INDENT + "\"value\": \"foo\"" + NEWLINE
                        + INDENT + "}" + NEWLINE
                        + "}",
                stringifyType(TypeWithString.class, true, true, new TypeWithString("foo")));
    }

    @Test
    public void testStringifyTypeWithPrimitive() throws IOException {
        String msg = "Primitive types should be printed correctly";
        Assert.assertEquals(msg, "1337",
                stringifyType(TypeWithPrimitive.class, true, false, new TypeWithPrimitive(1337)));
        Assert.assertEquals(msg, "{" + NEWLINE
                        + INDENT + "\"value\": 1337" + NEWLINE
                        + "}",
                stringifyType(TypeWithPrimitive.class, true, true, new TypeWithPrimitive(1337)));
    }

    @Test
    public void testStringifyTypeWithNonPrimitive() throws IOException {
        String msg = "Non-primitive types should be printed correctly";
        Assert.assertEquals(msg, "31337",
                stringifyType(TypeWithNonPrimitive.class, true, false, new TypeWithNonPrimitive(31337)));
        Assert.assertEquals(msg, "{" + NEWLINE
                        + INDENT + "\"value\": {" + NEWLINE
                        + INDENT + INDENT + "\"value\": 31337" + NEWLINE
                        + INDENT + "}" + NEWLINE
                        + "}",
                stringifyType(TypeWithNonPrimitive.class, true, true, new TypeWithNonPrimitive(31337)));
    }

    @Test
    public void testStringifyTypeWithNestedPrimitiveType() throws IOException {
        String msg = "Types with nested primitives should be printed correctly";
        Assert.assertEquals(msg, "{" + NEWLINE
                        + INDENT + "\"value\": 42.0," + NEWLINE
                        + INDENT + "\"nestedType\": 42" + NEWLINE
                        + "}",
                stringifyType(TypeWithNestedPrimitive.class, true, false,
                        new TypeWithNestedPrimitive(42.0, new TypeWithPrimitive(42))));
        Assert.assertEquals(msg, "{" + NEWLINE
                        + INDENT + "\"value\": {" + NEWLINE
                        + INDENT + INDENT + "\"value\": 42.0" + NEWLINE
                        + INDENT + "}," + NEWLINE
                        + INDENT + "\"nestedType\": {" + NEWLINE
                        + INDENT + INDENT + "\"value\": 42" + NEWLINE
                        + INDENT + "}" + NEWLINE
                        + "}",
                stringifyType(TypeWithNestedPrimitive.class, true, true,
                        new TypeWithNestedPrimitive(42.0, new TypeWithPrimitive(42))));
    }

    @Test
    public void testStringifyTypeWithNestedNonPrimitiveType() throws IOException {
        // with prettyPrint
        String msg = "Types with nested non-primitives should be printed correctly";
        Assert.assertEquals(msg, "{" + NEWLINE
                        + INDENT + "\"value\": 42.0," + NEWLINE
                        + INDENT + "\"nestedType\": 42" + NEWLINE
                        + "}",
                stringifyType(TypeWithNestedNonPrimitive.class, true, false,
                        new TypeWithNestedNonPrimitive(42.0, new TypeWithNonPrimitive(42))));
        Assert.assertEquals(msg, "{" + NEWLINE
                        + INDENT + "\"value\": {" + NEWLINE
                        + INDENT + INDENT + "\"value\": 42.0" + NEWLINE
                        + INDENT + "}," + NEWLINE
                        + INDENT + "\"nestedType\": {" + NEWLINE
                        + INDENT + INDENT + "\"value\": {" + NEWLINE
                        + INDENT + INDENT + INDENT + "\"value\": 42" + NEWLINE
                        + INDENT + INDENT + "}" + NEWLINE
                        + INDENT + "}" + NEWLINE
                        + "}",
                stringifyType(TypeWithNestedNonPrimitive.class, true, true,
                        new TypeWithNestedNonPrimitive(42.0, new TypeWithNonPrimitive(42))));
    }

    @Test
    public void testStringifyWithoutPrettyPrint() throws IOException {
        String msg = "Types should be printed correctly without prettyPrint";
        // without prettyPrint
        Assert.assertEquals(msg, "{"
                        + "\"value\": 42.0,"
                        + "\"nestedType\": 42"
                        + "}",
                stringifyType(TypeWithNestedNonPrimitive.class, false, false,
                        new TypeWithNestedNonPrimitive(42.0, new TypeWithNonPrimitive(42))));
        Assert.assertEquals(msg, "{"
                        + "\"value\": {"
                        + "\"value\": 42.0"
                        + "},"
                        + "\"nestedType\": {"
                        + "\"value\": {"
                        + "\"value\": 42"
                        + "}"
                        + "}"
                        + "}",
                stringifyType(TypeWithNestedNonPrimitive.class, false, true,
                        new TypeWithNestedNonPrimitive(42.0, new TypeWithNonPrimitive(42))));
    }

    @Test
    public void testStringifyMultipleRecords() throws IOException {
        Assert.assertEquals("Multiple records should be printed correctly",
                "\"foo\"" + NEWLINE + "\"bar\"",
                stringifyType(TypeWithString.class, true, false,
                        new TypeWithString("foo"), new TypeWithString("bar")));
    }

    @Test
    public void testStringifyIterator() throws IOException {
        HollowRecordJsonStringifier recordJsonStringifier = new HollowRecordJsonStringifier(false, false);
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.useDefaultHashKeys();

        mapper.add(new TestTypeA(1, "one"));
        mapper.add(new TestTypeA(2, "two"));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);

        Iterable<HollowRecord> genericHollowObjects = (Iterable) Arrays.asList(new GenericHollowObject(readEngine, "TestTypeA", 0), new GenericHollowObject(readEngine, "TestTypeA", 1));

        StringWriter writer = new StringWriter();
        recordJsonStringifier.stringify(writer, genericHollowObjects);
        Assert.assertEquals("Multiple records should be printed correctly",
                "[{\"id\": 1,\"name\": {\"value\": \"one\"}},{\"id\": 2,\"name\": {\"value\": \"two\"}}]", writer.toString());
    }

    @Test
    public void testStringifyTypeWithExpandMapTypes() throws IOException {
        HollowRecordJsonStringifier stringifier = new HollowRecordJsonStringifier(false, false);
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.useDefaultHashKeys();
        Map<List<TypeE.SubType>, Integer> mapWithList = new HashMap();
        Map<String, List<TypeE.SubType>> mapWithString = new HashMap();
        List<TypeE.SubType> subTypes = new ArrayList();
        Map<TypeE.SubType, Integer> map = new HashMap<>();
        map.put(new TypeE.SubType("name1", 2000), 2000);
        subTypes.add(new TypeE.SubType("name2", 2000));
        mapWithList.put(subTypes, 200);
        mapWithString.put("name1", subTypes);
        mapper.add(new TypeE(map, mapWithList, mapWithString));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);

        HollowRecord genericHollowObjects = new GenericHollowObject(readEngine, "TypeE", 0);
        StringWriter writer = new StringWriter();
        stringifier.stringify(writer, genericHollowObjects);
        Assert.assertEquals("Map JSON should be a list of key and value when keyNode is Reference or Non-object; Map JSON should be key:value pair when keyNode is primitive", "{\"map\": [{\"key\":{\"name\": {\"value\": \"name1\"},\"year\": {\"value\": 2000}},\"value\":{\"value\": 2000}}],\"mapWithList\": [{\"key\":[{\"name\": {\"value\": \"name2\"},\"year\": {\"value\": 2000}}],\"value\":{\"value\": 200}}],\"mapWithString\": {\"name1\": [{\"name\": {\"value\": \"name2\"},\"year\": {\"value\": 2000}}]}}", writer.toString());
    }

    private static <T> String stringifyType(Class<T> clazz, boolean prettyPrint, boolean expanded, T... instances) throws IOException {
        HollowRecordJsonStringifier stringifier = new HollowRecordJsonStringifier(prettyPrint, !expanded);
        // HollowRecordJsonStringifier stringifier = expanded
        //    ? new HollowRecordJsonStringifier(prettyPrint, false) : new HollowRecordJsonStringifier();
        return stringifyType(clazz, stringifier, instances);
    }

}
