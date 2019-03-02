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
 * Indicates that a hollow object type (a POJO) has a primary key.
 * <p>
 * The primary key value of such a hollow object is the sequence of
 * values obtained by resolving the {@link #fields field} paths (in order)
 * given that hollow object.  There must be only one such hollow object, of
 * a particular type, for a given primary key.  Therefore, a hollow object
 * may be looked up given its primary key value
 * (see
 * {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex UniqueKeyIndex}
 * and
 * {@link com.netflix.hollow.core.index.HollowPrimaryKeyIndex HollowPrimaryKeyIndex}
 * ).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HollowPrimaryKey {

    /**
     * Returns the field paths of the primary key.
     *
     * @return the field paths of the primary key
     */
    String[] fields();
}