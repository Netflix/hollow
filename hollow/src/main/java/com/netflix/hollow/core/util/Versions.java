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
package com.netflix.hollow.core.util;

import static com.netflix.hollow.core.HollowConstants.VERSION_LATEST;
import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;

public final class Versions {

    // visible for testing
    static final String PRETTY_VERSION_NONE = "none";
    static final String PRETTY_VERSION_LATEST = "latest";

    private Versions() {
    }

    public static String prettyVersion(long version) {
        if(version == VERSION_NONE) {
            return PRETTY_VERSION_NONE;
        } else if(version == VERSION_LATEST) {
            return PRETTY_VERSION_LATEST;
        } else {
            return String.valueOf(version);
        }
    }
}
