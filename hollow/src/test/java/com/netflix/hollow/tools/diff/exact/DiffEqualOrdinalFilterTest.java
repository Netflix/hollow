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
package com.netflix.hollow.tools.diff.exact;

import com.netflix.hollow.core.util.IntList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DiffEqualOrdinalFilterTest {

    DiffEqualOrdinalFilter filter;

    @Before
    public void setUp() {
        DiffEqualOrdinalMap map = new DiffEqualOrdinalMap(10);

        map.putEqualOrdinals(1, list(100));
        map.putEqualOrdinals(2, list(200));
        map.putEqualOrdinals(3, list(300));
        map.putEqualOrdinals(4, list(400));
        map.putEqualOrdinals(5, list(500));
        map.putEqualOrdinals(6, list(600));
        map.putEqualOrdinals(7, list(700));
        map.putEqualOrdinals(8, list(800));
        map.putEqualOrdinals(9, list(900));
        map.putEqualOrdinals(10, list(1000));

        map.buildToOrdinalIdentityMapping();

        filter = new DiffEqualOrdinalFilter(map);
    }

    @Test
    public void separatesListsIntoMatchedAndUnmatchedLists() {
        IntList fromOrdinals = list(3, 2, 4, 1);
        IntList toOrdinals = list(200, 100, 300, 500);

        filter.filter(fromOrdinals, toOrdinals);

        assertList(filter.getMatchedFromOrdinals(), 3, 2, 1);
        assertList(filter.getMatchedToOrdinals(), 200, 100, 300);
        assertList(filter.getUnmatchedFromOrdinals(), 4);
        assertList(filter.getUnmatchedToOrdinals(), 500);
    }

    @Test
    public void handlesOrdinalsMissingFromMap() {
        IntList fromOrdinals = list(3, 2, 4, 1, 9999);
        IntList toOrdinals = list(200, 100, 300, 500, 9999);

        filter.filter(fromOrdinals, toOrdinals);

        assertList(filter.getMatchedFromOrdinals(), 3, 2, 1);
        assertList(filter.getMatchedToOrdinals(), 200, 100, 300);
        assertList(filter.getUnmatchedFromOrdinals(), 4, 9999);
        assertList(filter.getUnmatchedToOrdinals(), 500, 9999);
    }

    private IntList list(int... ordinals) {
        IntList list = new IntList(ordinals.length);
        for(int i=0;i<ordinals.length;i++) {
            list.add(ordinals[i]);
        }
        return list;
    }

    private void assertList(IntList list, int... expectedEntries) {
        Assert.assertEquals(expectedEntries.length, list.size());

        for(int i=0;i<list.size();i++) {
            Assert.assertEquals(expectedEntries[i], list.get(i));
        }
    }
}
