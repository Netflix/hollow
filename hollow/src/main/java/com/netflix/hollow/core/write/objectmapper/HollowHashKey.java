/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field, declared by a hollow object type (a POJO),
 * of type {@code Set} or {@code Map} has a hash key defining how the
 * hollow objects that are elements in a {@code Set} or are keys in a
 * {@code Map} are hashed.
 * <p>
 * A hash is derived from the sequence of values obtained by resolving
 * the {@link #fields field} paths (in order) given an hollow object that is
 * the element or key.
 * Such hashes are used to distribute the hollow objects encoded within a
 * hollow set or map.
 * <p>
 * By default if this annotation is not declared on a field of type {@code Set} or {@code Map},
 * referred to as the hash key field, then a hash key is derived from the element or key type
 * as follows.
 * If the type is annotated with {@link HollowPrimaryKey} then it's as if the
 * hash key field is annotated with {@code HollowHashKey} with the same field paths as
 * declared by the {@code HollowPrimaryKey}.
 * Otherwise, if the type declares exactly one field whose type is a primitive type then
 * it's as if the hash key field is annotated with {@code HollowHashKey} with a single
 * field path that is the name of that one field.
 * Otherwise, it's as if the field is annotated with {@code HollowHashKey} with an empty
 * field paths array (indicating the ordinal of an element or key is used as the hash).
 * This annotation with an empty array may be utilized to enforce the latter case,
 * overriding one of the other prior cases, if applicable.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HollowHashKey {

    /**
     * Returns the field paths of the hash key.
     * <p>
     * An empty array indicates that the ordinal of an element in a set
     * or a key in a map is used as the hash.
     *
     * @return the field paths of the hash key
     */
    String[] fields();
}
