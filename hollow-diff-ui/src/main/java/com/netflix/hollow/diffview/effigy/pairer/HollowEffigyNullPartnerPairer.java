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
package com.netflix.hollow.diffview.effigy.pairer;

import com.netflix.hollow.diffview.effigy.HollowEffigy;
import java.util.ArrayList;
import java.util.List;

public class HollowEffigyNullPartnerPairer extends HollowEffigyFieldPairer {

    public HollowEffigyNullPartnerPairer(HollowEffigy from, HollowEffigy to) {
        super(from, to);
    }

    @Override
    public List<EffigyFieldPair> pair() {
        List<EffigyFieldPair> pairs = new ArrayList<EffigyFieldPair>();

        if(from != null) {
            for(int i = 0; i < from.getFields().size(); i++) {
                pairs.add(new EffigyFieldPair(from.getFields().get(i), null, i, -1));
            }
        } else if(to != null) {
            for(int i = 0; i < to.getFields().size(); i++) {
                pairs.add(new EffigyFieldPair(null, to.getFields().get(i), -1, i));
            }
        }

        return pairs;
    }

}
