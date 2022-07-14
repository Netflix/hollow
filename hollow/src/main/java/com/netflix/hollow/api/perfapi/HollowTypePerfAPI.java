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

import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

public abstract class HollowTypePerfAPI {

    private final HollowPerformanceAPI api;
    protected final long maskedTypeIdx;

    public HollowTypePerfAPI(String typeName, HollowPerformanceAPI api) {
        int typeIdx = api.types.getIdx(typeName);
        this.maskedTypeIdx = Ref.toTypeMasked(typeIdx);
        this.api = api;
    }

    public long refForOrdinal(int ordinal) {
        return Ref.toRefWithTypeMasked(maskedTypeIdx, ordinal);
    }

    public abstract HollowTypeDataAccess typeAccess();

    public HollowPerformanceAPI api() {
        return api;
    }

    /**
     * Gets the ordinal of the reference and checks that the reference is of the correct type.
     * @param ref the reference
     * @return the ordinal
     * @throws IllegalArgumentException if the reference's type differs
     */
    public int ordinal(long ref) {
        if(!Ref.isRefOfTypeMasked(maskedTypeIdx, ref)) {
            String expectedType = api.types.getTypeName(Ref.type(maskedTypeIdx));

            if(Ref.isNull(ref)) {
                throw new NullPointerException("Reference is null -- expected type " + expectedType);
            }

            String foundType = api.types.getTypeName(Ref.type(ref));
            throw new IllegalArgumentException("Wrong reference type -- expected type " + expectedType + " but ref was of type " + foundType);
        }
        return Ref.ordinal(ref);
    }

    public boolean isMissingType() {
        return maskedTypeIdx == Ref.toTypeMasked(Ref.TYPE_ABSENT);
    }
}
