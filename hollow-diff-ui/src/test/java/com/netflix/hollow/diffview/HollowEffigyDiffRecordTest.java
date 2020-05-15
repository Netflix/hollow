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
package com.netflix.hollow.diffview;

import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigyFactory;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyDiffRecord;
import com.netflix.hollow.tools.diff.HollowDiff;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowEffigyDiffRecordTest {

    @Test
    public void test() throws IOException {
        HollowDiff diff = new FakeHollowDiffGenerator().createFakeDiff();

        HollowEffigyFactory effigyFactory = new HollowEffigyFactory();

        HollowEffigy fromEffigy = effigyFactory.effigy(diff.getFromStateEngine(), "TypeA", 0);
        HollowEffigy toEffigy = effigyFactory.effigy(diff.getToStateEngine(), "TypeA", 0);

        HollowEffigyDiffRecord diffRecord = new HollowEffigyDiffRecord(fromEffigy);

        Assert.assertEquals(8, diffRecord.calculateDiff(toEffigy, 8));
    }

}
