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

import com.netflix.hollow.api.objects.HollowRecord;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public class DefaultHashCodeFinder implements HollowObjectHashCodeFinder {

    public static final DefaultHashCodeFinder INSTANCE = new DefaultHashCodeFinder();

    private final Set<String> typesWithDefinedHashCodes;

    public DefaultHashCodeFinder(String... typesWithDefinedHashCodes) {
        this.typesWithDefinedHashCodes = new HashSet<String>(typesWithDefinedHashCodes.length);

        for(String type : typesWithDefinedHashCodes) {
            this.typesWithDefinedHashCodes.add(type);
        }
    }

    @Deprecated
    @Override
    public int hashCode(int ordinal, Object objectToHash) {
        return hashCode(null, ordinal, objectToHash);
    }

    @Deprecated
    @Override
    public int hashCode(Object objectToHash) {
        return hashCode(null, objectToHash);
    }

    public int hashCode(String typeName, int ordinal, Object objectToHash) {
        if(!typesWithDefinedHashCodes.contains(typeName))
            return ordinal;

        return objectToHash.hashCode();
    }

    public int hashCode(String typeName, Object objectToHash) {
        if(objectToHash instanceof HollowRecord)
            return ((HollowRecord) objectToHash).getOrdinal();
        return objectToHash.hashCode();
    }

    @Override
    public Set<String> getTypesWithDefinedHashCodes() {
        return typesWithDefinedHashCodes;
    }
}
