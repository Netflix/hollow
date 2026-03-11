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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a custom Hollow type name for the element type of a {@link java.util.List}
 * or {@link java.util.Set} field.
 *
 * <p>By default, {@code List<String>} uses the global shared {@code "String"} type state.
 * Specifying {@code elementTypeName} creates a dedicated type state with that name,
 * resulting in a smaller ordinal pool and fewer bits per reference.
 *
 * <p>Only processed when {@link HollowObjectMapper#enableCollectionTypeNaming()} has been called.
 *
 * <p><b>Schema change warning:</b> Adding this annotation to an existing field changes the
 * Hollow schema. Coordinate producer and consumer updates before deploying.
 *
 * <p>Composable with {@link HollowTypeName} on the same field to also rename the outer
 * collection type:
 * <pre>
 * {@code
 *  &#64;HollowTypeName("MyMovieIds")
 *  &#64;HollowCollectionTypeName(elementTypeName = "MovieId")
 *  private List<Integer> movieIds;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HollowCollectionTypeName {

    /**
     * The Hollow type name for the element type of the List or Set.
     * An empty string (the default) means the auto-generated name is used.
     */
    String elementTypeName() default "";
}
