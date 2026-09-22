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

import org.junit.Assert;
import org.junit.Test;

public class RecordPrimaryKeyTest {

    @Test
    public void equalByteArrayComponentsCompareEqualAndHashEqual() {
        RecordPrimaryKey first = new RecordPrimaryKey("Type", new Object[] { 1, "one", true, new byte[] { 1, 2, 3 } });
        RecordPrimaryKey second = new RecordPrimaryKey("Type", new Object[] { 1, "one", true, new byte[] { 1, 2, 3 } });

        Assert.assertEquals(first, second);
        Assert.assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void differentByteArrayComponentsCompareUnequal() {
        RecordPrimaryKey first = new RecordPrimaryKey("Type", new Object[] { new byte[] { 1, 2, 3 } });
        RecordPrimaryKey second = new RecordPrimaryKey("Type", new Object[] { new byte[] { 1, 2, 4 } });

        Assert.assertNotEquals(first, second);
    }

    @Test
    public void differentTypeNamesCompareUnequal() {
        Object[] key = new Object[] { 1, "one" };

        Assert.assertNotEquals(new RecordPrimaryKey("TypeA", key), new RecordPrimaryKey("TypeB", key));
    }

    @Test
    public void scalarComponentsRetainValueEquality() {
        Object[] key = new Object[] { false, 1.5d, 2.5f, 3, 4L, "five" };

        Assert.assertEquals(new RecordPrimaryKey("Type", key), new RecordPrimaryKey("Type", key.clone()));
    }
}
