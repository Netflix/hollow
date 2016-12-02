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

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;

public class HollowListOrdinalIterator implements HollowOrdinalIterator {

    private final int listOrdinal;
    private final HollowListTypeDataAccess dataAccess;
    private final int size;
    private int currentElement;

    public HollowListOrdinalIterator(int listOrdinal, HollowListTypeDataAccess dataAccess) {
        this.listOrdinal = listOrdinal;
        this.dataAccess = dataAccess;
        this.size = dataAccess.size(listOrdinal);
    }

    @Override
    public int next() {
        if(currentElement == size)
            return NO_MORE_ORDINALS;

        return dataAccess.getElementOrdinal(listOrdinal, currentElement++);
    }

}
