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

import java.util.HashMap;
import java.util.Map;

/**
 * A MemoizedMap is a java.util.HashMap which is expected to be memoized during a producer cycle.
 * 
 * When a HollowObjectMapper adds a MemoizedMap to the HollowWriteStateEngine, it will tag it with the ordinal
 * which the corresponding record is assigned.  If the same MemoizedMap instance is encountered during the same cycle,
 * then it will short-circuit the process of serializing the map -- returning the previously memoized ordinal.
 */
public class MemoizedMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = -7952842518944521839L;

    public MemoizedMap() {
        super();
    }

    public MemoizedMap(int initialCapacity) {
        super(initialCapacity);
    }

    public MemoizedMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public MemoizedMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    transient long __assigned_ordinal = -1L;
}
