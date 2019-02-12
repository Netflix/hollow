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
package com.netflix.hollow.core.read.iterator;

/**
 * An iterator over ordinals.  The general pattern for use is:
 * <pre>
 * {@code
 * HollowOrdinalIterator iter = ...;
 * 
 * int ordinal = iter.next();
 * while(ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
 *     /// do something with the ordinal
 *     ordinal = iter.next();
 * }
 * }
 * </pre>
 *
 */
public interface HollowOrdinalIterator {

    /**
     * A value indicating that no more ordinals are available in the iteration.
     */
    public static final int NO_MORE_ORDINALS = Integer.MAX_VALUE;

    /**
     * @return The next ordinal, or {@link HollowOrdinalIterator#NO_MORE_ORDINALS} if no more ordinals are available in this iteration.
     */
    public int next();

}
