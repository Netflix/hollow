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
import java.util.Set;

/**
 * Use of this interface is not recommended.  Instead, use the <i>hash key</i> functionality available sets and maps.
 * <p>
 * With this interface, in conjunction with a cooperating data ingestion mechanism, it is possible to use custom hash codes
 * in Hollow sets and maps.
 * @deprecated Use hash key the <i>hash key</i> functionality available sets and maps
 * @see com.netflix.hollow.core.write.objectmapper.HollowHashKey
 */
@Deprecated
public interface HollowObjectHashCodeFinder {

    String DEFINED_HASH_CODES_HEADER_NAME = "DEFINED_HASH_CODES";

    /**
     * For look-up at runtime.
     * <p>
     * If using simple ordinal-based hashing, then objectToHash must be a {@link HollowRecord}, and the return value will be
     * objectToHash.getOrdinal();
     * <p>
     * Otherwise, the hash code is determined with exactly the same logic as was used during serialization.
     *
     * @param objectToHash the object to hash
     * @return the hash code
     */
    int hashCode(Object objectToHash);

    /**
     * For serialization.
     * <p>
     * At serialization time, we know the ordinal of a newly added object, but may not know how to hash the object
     * which is being serialized, which is necessary for Sets and Maps.
     * <p>
     * If using simple ordinal-based hashing, the ordinal will be returned.  Otherwise, the return value will be calculated based on the objectToHash.
     *
     * @param typeName the type name
     * @param ordinal the ordinal
     * @param objectToHash the object to hash
     * @return the hash code
     */
    int hashCode(String typeName, int ordinal, Object objectToHash);

    /**
     * @return the set of types which have hash codes defined (i.e. hash codes which are not simply each record's ordinal)
     */
    Set<String> getTypesWithDefinedHashCodes();


    @Deprecated
    int hashCode(int ordinal, Object objectToHash);

}
