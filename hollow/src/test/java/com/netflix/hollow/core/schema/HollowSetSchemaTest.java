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
package com.netflix.hollow.core.schema;

import com.netflix.hollow.core.index.key.PrimaryKey;
import org.junit.Assert;
import org.junit.Test;

public class HollowSetSchemaTest {

    @Test
    public void testEquals() {
        {
            HollowSetSchema s1 = new HollowSetSchema("Test", "TypeA");
            HollowSetSchema s2 = new HollowSetSchema("Test", "TypeA");

            Assert.assertEquals(s1, s2);
        }

        {
            HollowSetSchema s1 = new HollowSetSchema("Test", "TypeA");
            HollowSetSchema s2 = new HollowSetSchema("Test2", "TypeA");

            Assert.assertNotEquals(s1, s2);
        }

        {
            HollowSetSchema s1 = new HollowSetSchema("Test", "TypeA");
            HollowSetSchema s2 = new HollowSetSchema("Test", "TypeB");

            Assert.assertNotEquals(s1, s2);
        }
    }

    @Test
    public void testEqualsWithKeys() {
        {
            HollowSetSchema s1 = new HollowSetSchema("Test", "TypeA", "f1");
            HollowSetSchema s2 = new HollowSetSchema("Test", "TypeA", "f1");

            Assert.assertEquals(s1, s2);
            Assert.assertEquals(s1.getHashKey(), s2.getHashKey());
            Assert.assertEquals(new PrimaryKey("TypeA", "f1"), s2.getHashKey());
        }

        {
            HollowSetSchema s1 = new HollowSetSchema("Test", "TypeA", "f1", "f2");
            HollowSetSchema s2 = new HollowSetSchema("Test", "TypeA", "f1", "f2");

            Assert.assertEquals(s1, s2);
            Assert.assertEquals(s1.getHashKey(), s2.getHashKey());
            Assert.assertEquals(new PrimaryKey("TypeA", "f1", "f2"), s2.getHashKey());
        }

        {
            HollowSetSchema s1 = new HollowSetSchema("Test", "TypeA");
            HollowSetSchema s2 = new HollowSetSchema("Test", "TypeA", "f1");

            Assert.assertNotEquals(s1, s2);
            Assert.assertNotEquals(s1.getHashKey(), s2.getHashKey());
        }

        {
            HollowSetSchema s1 = new HollowSetSchema("Test", "TypeA", "f1");
            HollowSetSchema s2 = new HollowSetSchema("Test", "TypeA", "f1", "f2");

            Assert.assertNotEquals(s1, s2);
            Assert.assertNotEquals(s1.getHashKey(), s2.getHashKey());
        }
    }
}
