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

import java.util.Set;

public class TypeA {

    private final String a1;
    private final int a2;
    private final TypeB b;
    private final Set<TypeC> cList;

    public TypeA(String a1, int a2, TypeB b, Set<TypeC> cList) {
        this.a1 = a1;
        this.a2 = a2;
        this.b = b;
        this.cList = cList;
    }

    public String getA1() {
        return a1;
    }

    public int getA2() {
        return a2;
    }

    public TypeB getB() {
        return b;
    }

    public Set<TypeC> getCList() {
        return cList;
    }

}
