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
package com.netflix.hollow.tools.combine;

/**
 * Remaps ordinals various operations.  Not intended for external consumption.
 * 
 */
public interface OrdinalRemapper {

    /**
     * @param type the type name
     * @param originalOrdinal the original ordinal
     * @return the remapped ordinal
     */
    public int getMappedOrdinal(String type, int originalOrdinal);

    /**
     * Remap an ordinal.
     * @param type the type name
     * @param originalOrdinal the original ordinal
     * @param mappedOrdinal the mapped ordinal
     */
    public void remapOrdinal(String type, int originalOrdinal, int mappedOrdinal);

    /**
     * @return whether or not a mapping is already defined.
     * @param type the type name
     * @param originalOrdinal the original ordinal
     */
    public boolean ordinalIsMapped(String type, int originalOrdinal);

}
