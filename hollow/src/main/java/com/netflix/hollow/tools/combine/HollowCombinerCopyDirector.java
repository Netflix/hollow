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
package com.netflix.hollow.tools.combine;

import com.netflix.hollow.core.read.engine.HollowTypeReadState;

/**
 * Specifies specific records (via their ordinals) to include/exclude while copying.
 * <p>
 * 
 * The most useful implementation of this is likely the {@link HollowCombinerExcludePrimaryKeysCopyDirector}
 * 
 * @author dkoszewnik
 *
 */
public interface HollowCombinerCopyDirector {

    /**
     * @param typeState the read state
     * @param ordinal the ordinal to copy
     * @return whether or not to include the specified ordinal from the supplied {@link HollowTypeReadState} in the output.
     * If this method returns false, then the copier will not attempt to directly copy the matching record.  However, if 
     * the matching record is referenced via <i>another</i> record for which this method returns true, then it will still be copied.  
     */
    boolean shouldCopy(HollowTypeReadState typeState, int ordinal);

    HollowCombinerCopyDirector DEFAULT_DIRECTOR = new HollowCombinerCopyDirector() {
        public boolean shouldCopy(HollowTypeReadState typeName, int ordinal) {
            return true;
        }
    };

}
