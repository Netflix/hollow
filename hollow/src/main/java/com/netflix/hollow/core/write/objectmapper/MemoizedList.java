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
package com.netflix.hollow.core.write.objectmapper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A MemoizedList is a java.util.ArrayList which is expected to be memoized during a producer cycle.
 * 
 * When a HollowObjectMapper adds a MemoizedList to the HollowWriteStateEngine, it will tag it with the ordinal
 * which the corresponding record is assigned.  If the same MemoizedList instance is encountered during the same cycle,
 * then it will short-circuit the process of serializing the list -- returning the previously memoized ordinal.
 */
public class MemoizedList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 4055358559110722153L;


    public MemoizedList() {
        super();
    }

    public MemoizedList(int initialCapacity) {
        super(initialCapacity);
    }

    public MemoizedList(Collection<? extends E> c) {
        super(c);
    }

    transient long __assigned_ordinal = -1L;
}
