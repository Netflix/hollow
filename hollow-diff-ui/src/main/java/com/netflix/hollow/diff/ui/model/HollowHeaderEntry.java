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
package com.netflix.hollow.diff.ui.model;

public class HollowHeaderEntry {

    private final int idx;
    private final String key;
    private final String fromValue;
    private final String toValue;

    public HollowHeaderEntry(int idx, String key, String fromValue, String toValue) {
        this.idx = idx;
        this.key = key;
        this.fromValue = fromValue;
        this.toValue = toValue;
    }

    public int getIdx() {
        return idx;
    }

    public String getBgColor() {
        if(isSame())
            return "";
        return "#FFCC99";
    }

    public boolean isSame() {
        return fromValue == null ? toValue == null : fromValue.equals(toValue);
    }

    public String getKey() {
        return key;
    }

    public String getFromValue() {
        return fromValue == null ? "null" : fromValue;
    }

    public String getToValue() {
        return toValue == null ? "null" : toValue;
    }

}
