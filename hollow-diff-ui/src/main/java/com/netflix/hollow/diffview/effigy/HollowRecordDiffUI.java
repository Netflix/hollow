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
package com.netflix.hollow.diffview.effigy;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.diffview.effigy.pairer.exact.ExactRecordMatcher;
import java.util.Map;


public interface HollowRecordDiffUI {

    public Map<String, PrimaryKey> getMatchHints();

    public CustomHollowEffigyFactory getCustomHollowEffigyFactory(String typeName);

    public ExactRecordMatcher getExactRecordMatcher();
}
