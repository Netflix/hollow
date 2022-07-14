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

import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;

public class HollowPerfMapEntryIterator {

    private final long keyMaskedTypeIdx;
    private final long valueMaskedTypeIdx;
    private final HollowMapEntryOrdinalIterator iter;

    public HollowPerfMapEntryIterator(HollowMapEntryOrdinalIterator iter, long keyMaskedTypeIdx, long valueMaskedTypeIdx) {
        this.iter = iter;
        this.keyMaskedTypeIdx = keyMaskedTypeIdx;
        this.valueMaskedTypeIdx = valueMaskedTypeIdx;
    }

    public boolean next() {
        return iter.next();
    }

    public long getKey() {
        return Ref.toRefWithTypeMasked(keyMaskedTypeIdx, iter.getKey());
    }

    public long getValue() {
        return Ref.toRefWithTypeMasked(valueMaskedTypeIdx, iter.getValue());
    }

}
