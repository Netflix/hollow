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
package com.netflix.hollow.core.index.traversal;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.TypeA;
import com.netflix.hollow.core.write.objectmapper.TypeB;
import com.netflix.hollow.core.write.objectmapper.TypeC;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class HollowIndexTraversalTest extends AbstractStateEngineTest {

    @Test
    public void test() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA("two", 2, new TypeB((short) 20, 20000000L, 2.2f, "two".toCharArray(), new byte[]{2, 2, 2}),
                Collections.<TypeC>emptySet()));
        mapper.add(new TypeA("one", 1, new TypeB((short) 10, 10000000L, 1.1f, "one".toCharArray(), new byte[]{1, 1, 1}),
                new HashSet<TypeC>(Arrays.asList(
                        new TypeC('d', map("one.1", 1, "one.2", 1, 1, "one.3", 1, 2, 3)),
                        new TypeC('e', map("one.x", 1, "one.y", 1, 1, "one.z", 1, 2, 3))
                ))));

        roundTripSnapshot();

        TraversalTreeBuilder builder = new TraversalTreeBuilder(readStateEngine, "TypeA", new String[]{"a1.value", "b.b3", "cList.element.c1", "cList.element.map.value.element"});

        HollowIndexerTraversalNode root = builder.buildTree();

        root.traverse(1);
        System.out.println(builder.getFieldMatchLists()[0].size());
        System.out.println(builder.getFieldMatchLists()[0].get(0));
        System.out.println(builder.getFieldMatchLists()[0].get(1));
        System.out.println(builder.getFieldMatchLists()[1].size());
        System.out.println(builder.getFieldMatchLists()[1].get(0));
        System.out.println(builder.getFieldMatchLists()[1].get(1));
        System.out.println(builder.getFieldMatchLists()[2].size());
        System.out.println(builder.getFieldMatchLists()[2].get(0));
        System.out.println(builder.getFieldMatchLists()[2].get(1));
    }

    private Map<String, List<Integer>> map(Object... keyValues) {
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        int i = 0;

        while(i < keyValues.length) {
            String key = (String) keyValues[i];
            List<Integer> values = new ArrayList<Integer>();
            i++;
            while(i < keyValues.length && keyValues[i] instanceof Integer) {
                values.add((Integer) keyValues[i]);
                i++;
            }

            map.put(key, values);
        }

        return map;
    }

    @Override
    protected void initializeTypeStates() {
    }


}
