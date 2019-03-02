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
package com.netflix.hollow.core.read.engine;


/**
 * A HollowTypeStateListener is registered with a specific {@link HollowTypeReadState} 
 * to receive callback notifications when deltas are applied to a {@link HollowReadStateEngine}
 */
public interface HollowTypeStateListener {

    /**
     * Called immediately before a delta update is applied to the state engine
     */
    void beginUpdate();

    /**
     * Called once for each record which is added to the registered type.
     * @param ordinal the ordinal of an object that was added
     */
    void addedOrdinal(int ordinal);

    /**
     * Called once for each record which is removed from the registered type.
     * @param ordinal the ordinal of an object that was removed
     */
    void removedOrdinal(int ordinal);

    /**
     * Called immediately after a delta update is applied to the state engine.
     */
    void endUpdate();

}
