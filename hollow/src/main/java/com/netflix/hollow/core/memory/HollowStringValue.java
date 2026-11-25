/*
 * Copyright 2026 Netflix, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hollow.core.memory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * An immutable String prepared for repeated field comparisons. ASCII values retain an additional
 * byte array so readers can compare encoded storage without creating a String on each read.
 * Create this value once and reuse it; non-ASCII values retain only their original String.
 */
public final class HollowStringValue {
    private static final MethodHandle ARRAY_MISMATCH = findArrayMismatch();
    private final String value;
    private final byte[] ascii;

    private HollowStringValue(String value, byte[] ascii) {
        this.value = value;
        this.ascii = ascii;
    }

    /** Prepare a non-null String for repeated comparisons. */
    public static HollowStringValue of(String value) {
        Objects.requireNonNull(value, "value");
        for(int i = 0; i < value.length(); i++) {
            if(value.charAt(i) > 127)
                return new HollowStringValue(value, null);
        }
        return new HollowStringValue(value, value.getBytes(StandardCharsets.US_ASCII));
    }

    public String getValue() {
        return value;
    }

    public boolean isAscii() {
        return ascii != null;
    }

    int asciiLength() {
        return ascii.length;
    }

    boolean matchesAsciiSegment(byte[] segment, int offset, int consumed, int length) {
        if(ARRAY_MISMATCH != null) {
            try {
                return (int)ARRAY_MISMATCH.invokeExact(segment, offset, offset + length,
                        ascii, consumed, consumed + length) < 0;
            } catch(RuntimeException | Error e) {
                throw e;
            } catch(Throwable e) {
                throw new AssertionError("Array comparison failed", e);
            }
        }
        // Keep Java 8 support without linking directly to the Java 9+ API.
        for(int i = 0; i < length; i++) {
            if(segment[offset + i] != ascii[consumed + i])
                return false;
        }
        return true;
    }

    private static MethodHandle findArrayMismatch() {
        try {
            return MethodHandles.publicLookup().findStatic(Arrays.class, "mismatch",
                    MethodType.methodType(int.class, byte[].class, int.class, int.class,
                            byte[].class, int.class, int.class));
        } catch(NoSuchMethodException | IllegalAccessException e) {
            return null;
        }
    }
}
