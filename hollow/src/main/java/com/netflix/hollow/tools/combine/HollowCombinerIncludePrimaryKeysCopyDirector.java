/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.tools.combine;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;

/**
 * Specifies a set of inclusions for a {@link HollowCombiner}'s operation over one or more inputs.
 * <p>
 * Inclusions are specified based on record primary keys.
 * <p>
 * This is one of the most useful implementations of a {@link HollowCombinerCopyDirector}.
 * 
 */
public class HollowCombinerIncludePrimaryKeysCopyDirector implements HollowCombinerCopyDirector {

    private final HollowCombinerExcludePrimaryKeysCopyDirector inverseCopyDirector;

    public HollowCombinerIncludePrimaryKeysCopyDirector() {
        this.inverseCopyDirector = new HollowCombinerExcludePrimaryKeysCopyDirector();
    }

    public HollowCombinerIncludePrimaryKeysCopyDirector(HollowCombinerCopyDirector baseDirector) {
        this.inverseCopyDirector = new HollowCombinerExcludePrimaryKeysCopyDirector(baseDirector);
    }

    /**
     * Include the record which matches the specified key.
     * 
     * @param idx the index in which to query for the key 
     * @param key the key
     */
    public void includeKey(HollowPrimaryKeyIndex idx, Object... key) {
        inverseCopyDirector.excludeKey(idx, key);
    }

    @Override
    public boolean shouldCopy(HollowTypeReadState typeState, int ordinal) {
        return !inverseCopyDirector.shouldCopy(typeState, ordinal);
    }

}
