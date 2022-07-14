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

import java.util.List;
import java.util.Map;

public class TypeC {

    private final char c1;
    private final Map<String, List<Integer>> map;

    public TypeC(char c1, Map<String, List<Integer>> map) {
        this.c1 = c1;
        this.map = map;
    }

    public char getC1() {
        return c1;
    }

    public Map<String, List<Integer>> getMap() {
        return map;
    }
}
