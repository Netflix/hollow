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
import com.netflix.hollow.tools.diff.HollowDiff;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class HollowObjectDiffViewGeneratorTest {

    @Test
    public void test() throws IOException {
        HollowDiff diff = new FakeHollowDiffGenerator().createFakeDiff();

        HollowObjectDiffViewGenerator generator = new HollowObjectDiffViewGenerator(diff.getFromStateEngine(), diff.getToStateEngine(), null, "TypeA", 0, 0);

        List<HollowDiffViewRow> hollowDiffViewRows = generator.getHollowDiffViewRows();

        System.out.println(hollowDiffViewRows.size());

        for(HollowDiffViewRow viewRow : hollowDiffViewRows) {
            Object value = viewRow.getFieldPair().getFrom().getValue();
            String valueStr = value instanceof HollowEffigy ? ((HollowEffigy) value).getObjectType() : value.toString();
            String val = viewRow.getFieldPair().getFrom().getFieldNodeIndex().getViaFieldName() + ":" + valueStr;

            System.out.print(viewRow.getRowId());

            System.out.print(viewRow.isUnrolled() ? "u " : "  ");

            for(int i=0;i<viewRow.getIndentation();i++) {
                if(viewRow.hasMoreFromRows(i))
                    System.out.print("|   ");
                else
                    System.out.print("    ");
            }

            System.out.println(val);
        }

    }

}
