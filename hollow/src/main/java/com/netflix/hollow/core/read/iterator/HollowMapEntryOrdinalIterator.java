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
package com.netflix.hollow.core.read.iterator;

/**
 * A {@link HollowMapEntryOrdinalIterator} allows for iteration over key/value entries in a Hollow MAP record. 
 * <p>
 * The pattern for usage is:
 * <pre>
 * {@code
 * HollowMapEntryOrdinalIterator iter = /// some iterator
 * while(iter.next()) {
 *     int keyOrdinal = iter.getKey();
 *     int valueOrdinal = iter.getValue();
 * }
 * }
 * </pre>
 */
public interface HollowMapEntryOrdinalIterator {

    public boolean next();

    public int getKey();

    public int getValue();

}
