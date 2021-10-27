/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.perfapi;

/**
 * Utility methods on hollow references.
 * <p>
 * A hollow reference is a 64 bit pointer comprised of two parts.
 * One part is an ordinal value.
 * The other part is a type identifier associated with the ordinal.
 * A hollow reference provides a degree of type safety such that a hollow reference can only be used to
 * operate on a hollow object whose type corresponds to the reference's type identifier.
 */
public final class Ref {
    
    public static final int TYPE_ABSENT = -1;

    private static final long TYPE_MASK = 0x0000FFFF_00000000L;

    public static final long NULL = -1;

    private Ref() {
    }

    public static boolean isNonNull(long ref) {
        return ref != -1;
    }

    public static boolean isNull(long ref) {
        return ref == -1;
    }

    public static boolean isRefOfType(int type, long ref) {
        return isRefOfTypeMasked(toTypeMasked(type), ref);
    }

    public static boolean isRefOfTypeMasked(long typeMasked, long ref) {
        return typeMasked(ref) == typeMasked;
    }

    public static int ordinal(long ref) {
        return (int) ref;
    }

    public static int type(long ref) {
        return (int) (ref >>> 32);
    }

    public static long typeMasked(long ref) {
        return ref & TYPE_MASK;
    }

    public static long toRef(int type, int ordinal) {
        return toTypeMasked(type) | ordinal;
    }

    public static long toRefWithTypeMasked(long typeMasked, int ordinal) {
        // @@@ This erases the type
        return typeMasked | ordinal;
    }

    public static long toTypeMasked(int type) {
        return ((long) type << 32) & TYPE_MASK;
    }
}
