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
package com.netflix.hollow.zenoadapter.util;

import org.junit.Assert;
import org.junit.Test;

public class ObjectIdentityOrdinalMapTest {

    Object obj[] = new Object[10000];

    @Test
    public void test() {
        for(int i = 0; i < obj.length; i++) {
            obj[i] = new Object();
        }

        ObjectIdentityOrdinalMap ordinalMap = new ObjectIdentityOrdinalMap();

        for(int i = 0; i < obj.length; i++) {
            ordinalMap.put(obj[i], i);
        }

        for(int i = 0; i < obj.length; i++) {
            Assert.assertEquals(ordinalMap.getEntry(obj[i]).getOrdinal(), i);
        }
    }

}
