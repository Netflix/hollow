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
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;

public abstract class AllHollowRecordCollection<T> extends HollowRecordCollection<T> {

    public AllHollowRecordCollection(HollowTypeReadState typeState) {
        // SNAP: TODO: NOTE: This holds a reference to populatedOrdinals bitset which gets updated with refreshes so getAll* methods dont operate on a copy of Bitset
        //             super((BitSet) typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals().clone());
        super(typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals());
    }
}
