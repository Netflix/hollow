/*
 *
 *  Copyright 2017 Netflix, Inc.
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

import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;

public class HollowRecordJsonStringifierTest extends AbstractHollowRecordStringifierTest {
    @Test
    public void testStringifyTypeWithString() throws IOException {
        String msg = "String types should be printed correctly";
        Assert.assertEquals(msg, "\"foo\"",
                stringifyType(TypeWithString.class, false, new TypeWithString("foo")));
        Assert.assertEquals(msg, "{" + NEWLINE
                + INDENT + "\"value\": {" + NEWLINE
                + INDENT + INDENT + "\"value\": \"foo\"" + NEWLINE
                + INDENT + "}" + NEWLINE
                + "}", 
                stringifyType(TypeWithString.class, true, new TypeWithString("foo")));
    }

    @Test
    public void testStringifyTypeWithPrimitive() throws IOException {
        String msg = "Primitive types should be printed correctly";
        Assert.assertEquals(msg, "1337",
                stringifyType(TypeWithPrimitive.class, false, new TypeWithPrimitive(1337)));
        Assert.assertEquals(msg, "{" + NEWLINE
                + INDENT + "\"value\": 1337" + NEWLINE
                + "}",
                stringifyType(TypeWithPrimitive.class, true, new TypeWithPrimitive(1337)));
    }

    @Test
    public void testStringifyTypeWithNonPrimitive() throws IOException {
        String msg = "Non-primitive types should be printed correctly";
        Assert.assertEquals(msg, "31337",
                stringifyType(TypeWithNonPrimitive.class, false, new TypeWithNonPrimitive(31337)));
        Assert.assertEquals(msg, "{" + NEWLINE
                + INDENT + "\"value\": {" + NEWLINE
                + INDENT + INDENT + "\"value\": 31337" + NEWLINE
                + INDENT + "}" + NEWLINE
                + "}",
                stringifyType(TypeWithNonPrimitive.class, true, new TypeWithNonPrimitive(31337)));
    }

    @Test
    public void testStringifyTypeWithNestedPrimitiveType() throws IOException {
        String msg = "Types with nested primitives should be printed correctly";
        Assert.assertEquals(msg, "{" + NEWLINE
                + INDENT + "\"value\": 42.0," + NEWLINE
                + INDENT + "\"nestedType\": 42" + NEWLINE
                + "}", 
                stringifyType(TypeWithNestedPrimitive.class, false,
                    new TypeWithNestedPrimitive(42.0, new TypeWithPrimitive(42))));
        Assert.assertEquals(msg, "{" + NEWLINE
                + INDENT + "\"value\": {" + NEWLINE
                + INDENT + INDENT + "\"value\": 42.0" + NEWLINE
                + INDENT + "}," + NEWLINE
                + INDENT + "\"nestedType\": {" + NEWLINE
                + INDENT + INDENT + "\"value\": 42" + NEWLINE
                + INDENT + "}" + NEWLINE
                + "}",
                stringifyType(TypeWithNestedPrimitive.class, true,
                    new TypeWithNestedPrimitive(42.0, new TypeWithPrimitive(42))));
    }

    @Test
    public void testStringifyTypeWithNestedNonPrimitiveType() throws IOException {
        String msg = "Types with nested non-primitives should be printed correctly";
        Assert.assertEquals(msg, "{" + NEWLINE
                + INDENT + "\"value\": 42.0," + NEWLINE
                + INDENT + "\"nestedType\": 42" + NEWLINE
                + "}", 
                stringifyType(TypeWithNestedNonPrimitive.class, false,
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
                stringifyType(TypeWithNestedNonPrimitive.class, true,
                    new TypeWithNestedNonPrimitive(42.0, new TypeWithNonPrimitive(42))));
    }

    @Test
    public void testStringifyMultipleRecords() throws IOException {
        Assert.assertEquals("Multiple records should be printed correctly",
                "\"foo\"" + NEWLINE + "\"bar\"",
                stringifyType(TypeWithString.class, false,
                    new TypeWithString("foo"), new TypeWithString("bar")));
    }

    private static <T> String stringifyType(Class<T> clazz, boolean expanded, T... instances) throws IOException {
        HollowRecordJsonStringifier stringifier = expanded
            ? new HollowRecordJsonStringifier(true, false) : new HollowRecordJsonStringifier();
        return stringifyType(clazz, stringifier, instances);
    }
}
