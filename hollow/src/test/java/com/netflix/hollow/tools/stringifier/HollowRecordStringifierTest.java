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
import com.netflix.hollow.test.model.TestTypeA;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the HollowRecordStringifier.
 */
public class HollowRecordStringifierTest extends AbstractHollowRecordStringifierTest {
    @Test
    public void testStringifyTypeWithString() throws IOException {
        String msg = "String types should be printed correctly";
        Assert.assertEquals(msg, "foo",
                stringifyType(TypeWithString.class, false, new TypeWithString("foo")));
        Assert.assertEquals(msg, "(TypeWithString) (ordinal 0)" + NEWLINE
                + INDENT + "value: (String) (ordinal 0)" + NEWLINE
                + INDENT + INDENT + "value: foo",
                stringifyType(TypeWithString.class, true, new TypeWithString("foo")));
    }

    @Test
    public void testStringifyTypeWithPrimitive() throws IOException {
        String msg = "Primitive types should be printed correctly";
        Assert.assertEquals(msg, "1337",
                stringifyType(TypeWithPrimitive.class, false, new TypeWithPrimitive(1337)));
        Assert.assertEquals(msg,
                "(TypeWithPrimitive) (ordinal 0)" + NEWLINE + INDENT + "value: 1337",
                stringifyType(TypeWithPrimitive.class, true, new TypeWithPrimitive(1337)));
    }

    @Test
    public void testStringifyTypeWithNonPrimitive() throws IOException {
        String msg = "Non-primitive types should be printed correctly";
        Assert.assertEquals(msg, "31337",
                stringifyType(TypeWithNonPrimitive.class, false, new TypeWithNonPrimitive(31337)));
        Assert.assertEquals(msg, "(TypeWithNonPrimitive) (ordinal 0)" + NEWLINE
                + INDENT + "value: (Integer) (ordinal 0)" + NEWLINE
                + INDENT + INDENT + "value: 31337",
                stringifyType(TypeWithNonPrimitive.class, true, new TypeWithNonPrimitive(31337)));
    }

    @Test
    public void testStringifyTypeWithNestedPrimitiveType() throws IOException {
        String msg = "Types with nested primitives should be printed correctly";
        Assert.assertEquals(msg, NEWLINE + INDENT + "value: 42.0" + NEWLINE
                + INDENT + "nestedType: 42",
                stringifyType(TypeWithNestedPrimitive.class, false,
                        new TypeWithNestedPrimitive(42.0, new TypeWithPrimitive(42))));
        Assert.assertEquals(msg, "(TypeWithNestedPrimitive) (ordinal 0)" + NEWLINE
                + INDENT + "value: (Double) (ordinal 0)" + NEWLINE
                + INDENT + INDENT + "value: 42.0" + NEWLINE
                + INDENT + "nestedType: (TypeWithPrimitive) (ordinal 0)" + NEWLINE
                + INDENT + INDENT + "value: 42",
                stringifyType(TypeWithNestedPrimitive.class, true,
                        new TypeWithNestedPrimitive(42.0, new TypeWithPrimitive(42))));
    }

    @Test
    public void testStringifyTypeWithNestedNonPrimitiveType() throws IOException {
        String msg = "Types with nested non-primitives should be printed correctly";
        Assert.assertEquals(msg, NEWLINE + INDENT + "value: 42.0" + NEWLINE
                + INDENT + "nestedType: 42",
                stringifyType(TypeWithNestedNonPrimitive.class, false,
                        new TypeWithNestedNonPrimitive(42.0, new TypeWithNonPrimitive(42))));
        Assert.assertEquals(msg, "(TypeWithNestedNonPrimitive) (ordinal 0)" + NEWLINE
                + INDENT + "value: (Double) (ordinal 0)" + NEWLINE
                + INDENT + INDENT
                + "value: 42.0"
                + NEWLINE + INDENT + "nestedType: (TypeWithNonPrimitive) (ordinal 0)" + NEWLINE
                + INDENT + INDENT + "value: (Integer) (ordinal 0)" + NEWLINE
                + INDENT + INDENT + INDENT + "value: 42",
                stringifyType(TypeWithNestedNonPrimitive.class, true,
                        new TypeWithNestedNonPrimitive(42.0, new TypeWithNonPrimitive(42))));
    }

    @Test
    public void testStringifyMultipleRecords() throws IOException {
        Assert.assertEquals("Multiple records should be printed correctly",
                "foo" + NEWLINE + "bar",
                stringifyType(TypeWithString.class, false,
                        new TypeWithString("foo"), new TypeWithString("bar")));
    }

    @Test
    public void testStringifyIterator() throws IOException {
        HollowRecordStringifier recordStringifier = new HollowRecordStringifier();
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.useDefaultHashKeys();

        mapper.add(new TestTypeA(1, "one"));
        mapper.add(new TestTypeA(2, "two"));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);

        Iterable<HollowRecord> genericHollowObjects = (Iterable) Arrays.asList(new GenericHollowObject(readEngine, "TestTypeA", 0), new GenericHollowObject(readEngine, "TestTypeA", 1));

        StringWriter writer = new StringWriter();
        recordStringifier.stringify(writer, genericHollowObjects);
        Assert.assertEquals("Multiple records should be printed correctly",
                "[\n" +
                        "  id: 1\n" +
                        "  name: one,\n" +
                        "  id: 2\n" +
                        "  name: two" +
                        "\n]", writer.toString());
    }

    private static <T> String stringifyType(Class<T> clazz, boolean expanded, T... instances) throws IOException {
        HollowRecordStringifier stringifier = expanded
                ? new HollowRecordStringifier(true, true, false) : new HollowRecordStringifier();
        return stringifyType(clazz, stringifier, instances);
    }
}
