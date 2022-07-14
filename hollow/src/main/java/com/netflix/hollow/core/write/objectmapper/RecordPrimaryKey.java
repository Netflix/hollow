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
package com.netflix.hollow.core.write.objectmapper;

import java.util.Arrays;

public class RecordPrimaryKey {
    private final String type;
    private final Object[] key;

    public RecordPrimaryKey(String type, Object[] key) {
        this.type = type;
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public Object[] getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + Arrays.hashCode(key);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RecordPrimaryKey) {
            return type.equals(((RecordPrimaryKey) obj).type)
                    && Arrays.equals(key, ((RecordPrimaryKey) obj).key);
        }
        return false;
    }

    @Override
    public String toString() {
        return type + ": " + Arrays.toString(key);
    }
}
