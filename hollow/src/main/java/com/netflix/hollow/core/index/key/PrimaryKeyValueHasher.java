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
package com.netflix.hollow.core.index.key;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.Arrays;

/**
 * Used to hash records by their PrimaryKey.
 */
public class PrimaryKeyValueHasher {

    private final FieldType[] fieldTypes;

    /**
     * Create a new hasher.
     *  
     * @param key The primary key spec
     * @param dataset The dataset to retrieve data from
     */
    public PrimaryKeyValueHasher(PrimaryKey key, HollowDataset dataset) {
        this.fieldTypes = new FieldType[key.numFields()];

        for(int i = 0; i < fieldTypes.length; i++) {
            fieldTypes[i] = key.getFieldType(dataset, i);
        }
    }

    public int hash(Object key) {
        return hashElement(key, 0);
    }

    public int hash(Object... keys) {
        int hash = 0;

        for(int i = 0; i < keys.length; i++) {
            hash = hash * 31;
            hash ^= hashElement(keys[i], i);
        }

        return hash;
    }

    public int hashElement(Object key, int fieldTypeIdx) {
        switch(fieldTypes[fieldTypeIdx]) {
            case BOOLEAN:
                return key == null ? 0 : ((Boolean) key).booleanValue() ? 1231 : 1237;
            case BYTES:
                return key == null ? 0 : Arrays.hashCode((byte[]) key);
            case DOUBLE:
                long dVal = Double.doubleToRawLongBits(((Double) key).doubleValue());
                return (int) (dVal ^ (dVal >>> 32));
            case FLOAT:
                return Float.floatToRawIntBits(((Float) key).floatValue());
            case LONG:
                long lVal = ((Long) key).longValue();
                return (int) (lVal ^ (lVal >>> 32));
            case INT:
                return ((Integer) key).intValue();
            case REFERENCE:
                return ((Integer) key).intValue();
            case STRING:
                return key.hashCode();
            default:
                throw new IllegalArgumentException("Unknown field type: " + fieldTypes[fieldTypeIdx]);
        }
    }

}
