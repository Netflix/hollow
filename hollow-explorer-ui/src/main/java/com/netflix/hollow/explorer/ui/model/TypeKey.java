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
package com.netflix.hollow.explorer.ui.model;

public class TypeKey {

    private final int idx;
    private final String keyStr;
    private final String keyDisplayStr;
    private final int ordinal;

    public TypeKey(int idx, int ordinal, String keyStr, String keyDisplayStr) {
        this.idx = idx;
        this.ordinal = ordinal;
        this.keyStr = keyStr;
        this.keyDisplayStr = keyDisplayStr;
    }

    public int getIdx() {
        return idx;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public String getKey() {
        return keyStr;
    }

    public String getKeyDisplay() {
        return keyDisplayStr;
    }

}
