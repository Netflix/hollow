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
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

/**
 * Hashes POJO keys according to the Set/Map hash key specification.  This is used
 * in order to lookup matching records in these hashtable data structures.
 */
public class SetMapKeyHasher {

    /**
     * Hash a key
     * 
     * @param key the key
     * @param fieldType the respective FieldTypes of each key.
     * @return the hash code
     */
    public static int hash(Object key[], FieldType fieldType[]) {
        int hash = 0;

        for(int i = 0; i < key.length; i++) {
            hash *= 31;
            hash ^= hash(key[i], fieldType[i]);
        }

        return hash;
    }

    /**
     * Hash a single key field
     *
     * @param key the key
     * @param fieldType the FieldType of the key.
     * @return the hash code
     */
    public static int hash(Object key, FieldType fieldType) {
        switch(fieldType) {
            case INT:
                return HashCodes.hashInt(((Integer) key).intValue());
            case LONG:
                long longVal = ((Long) key).longValue();
                return HashCodes.hashInt((int) (longVal ^ (longVal >>> 32)));
            case REFERENCE:
                return HashCodes.hashInt(((Integer) key).intValue());
            case BYTES:
                return HashCodes.hashInt(HashCodes.hashCode((byte[]) key));
            case STRING:
                return HashCodes.hashInt(key.hashCode());
            case BOOLEAN:
                return HashCodes.hashInt(((Boolean) key).booleanValue() ? 1231 : 1237);
            case DOUBLE:
                long longBits = Double.doubleToRawLongBits(((Double) key).doubleValue());
                return HashCodes.hashInt((int) (longBits ^ (longBits >>> 32)));
            case FLOAT:
                return HashCodes.hashInt(Float.floatToRawIntBits(((Float) key).floatValue()));
            default:
                throw new IllegalArgumentException("Unknown field type: " + fieldType);
        }
    }

}
