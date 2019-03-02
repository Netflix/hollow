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

import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

public interface CustomHollowEffigyFactory {

    /**
     * Set the from record, called before generateEffigies
     */
    public void setFromHollowRecord(HollowTypeDataAccess fromState, int ordinal);

    /**
     * Set the to record, called before generateEffigies
     */
    public void setToHollowRecord(HollowTypeDataAccess toState, int ordinal);

    /**
     * Generate the effigies, called before getFromEffigy and getToEffigy
     */
    public void generateEffigies();

    public HollowEffigy getFromEffigy();

    public HollowEffigy getToEffigy();
}
