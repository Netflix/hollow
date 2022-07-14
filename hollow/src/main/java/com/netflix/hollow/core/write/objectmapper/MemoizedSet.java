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

import java.util.Collection;
import java.util.HashSet;

/**
 * A MemoizedSet is a java.util.HashSet which is expected to be memoized during a producer cycle.
 * 
 * When a HollowObjectMapper adds a MemoizedSet to the HollowWriteStateEngine, it will tag it with the ordinal
 * which the corresponding record is assigned.  If the same MemoizedSet instance is encountered during the same cycle,
 * then it will short-circuit the process of serializing the set -- returning the previously memoized ordinal.
 */
public class MemoizedSet<E> extends HashSet<E> {

    private static final long serialVersionUID = -3603271528350592970L;

    public MemoizedSet() {
        super();
    }

    public MemoizedSet(int initialCapacity) {
        super(initialCapacity);
    }

    public MemoizedSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public MemoizedSet(Collection<? extends E> c) {
        super(c);
    }

    transient long __assigned_ordinal = -1;
}
