/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.diffview;

import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigy.Field;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyCollectionPairer;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer.EffigyFieldPair;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class HollowEffigyCollectionPairerTest {

    private final HollowDiffNodeIdentifier elementFieldId = new HollowDiffNodeIdentifier("element");

    private final HollowDiffNodeIdentifier field1Id = new HollowDiffNodeIdentifier("field1");
    private final HollowDiffNodeIdentifier field2Id = new HollowDiffNodeIdentifier("field2");
    private final HollowDiffNodeIdentifier field3Id = new HollowDiffNodeIdentifier("field3");

    @Test
    public void test() {
        HollowEffigy list1 = list(
                element("1", 1, 1),
                element("2", 2, 2),
                element("3", 3, 3),
                element("4", 4, 4)
        );

        HollowEffigy list2 = list(
                element("3", 103, 103),
                element("1", 2, 1),
                element("2", 102, 2),
                element("5", 5, 5),
                element("1", 1, 1)
        );

        HollowEffigyCollectionPairer pairer = new HollowEffigyCollectionPairer(list1, list2, System.currentTimeMillis() + 1000L);

        List<EffigyFieldPair> pairs = pairer.pair();

        Assert.assertEquals(6, pairs.size());
        assertPair(pairs.get(0), "1", "1");
        assertPair(pairs.get(1), "2", "2");
        assertPair(pairs.get(2), "3", "3");
        assertPair(pairs.get(3), "4", null);
        assertPair(pairs.get(4), null, "1");
        assertPair(pairs.get(5), null, "5");
    }

    private void assertPair(EffigyFieldPair pair, String expectedFromField1, String expectedToField1) {
        if(expectedFromField1 != null) {
            HollowEffigy element = (HollowEffigy) pair.getFrom().getValue();
            Assert.assertEquals(expectedFromField1, element.getFields().get(0).getValue());
        } else {
            Assert.assertNull(pair.getFrom());
        }

        if(expectedToField1 != null) {
            HollowEffigy element = (HollowEffigy) pair.getTo().getValue();
            Assert.assertEquals(expectedToField1, element.getFields().get(0).getValue());
        } else {
            Assert.assertNull(pair.getTo());
        }
    }

    private HollowEffigy list(HollowEffigy... elements) {
        HollowEffigy list = new HollowEffigy("list");
        for(HollowEffigy element : elements) {
            list.add(new Field(elementFieldId, element));
        }
        return list;
    }

    private HollowEffigy element(String field1, int field2, float field3) {
        HollowEffigy eff = new HollowEffigy("element");
        eff.add(new Field(field1Id, field1));
        eff.add(new Field(field2Id, field2));
        eff.add(new Field(field3Id, field3));
        return eff;
    }
}
