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
 * An implementation of the OrdinalRemapper for when ordinals are not actually remapped.  Not intended for external consumption.
 * 
 * @author dkoszewnik
 *
 */
public class IdentityOrdinalRemapper implements OrdinalRemapper {

    public static IdentityOrdinalRemapper INSTANCE = new IdentityOrdinalRemapper();

    private IdentityOrdinalRemapper() {
    }

    @Override
    public int getMappedOrdinal(String type, int originalOrdinal) {
        return originalOrdinal;
    }

    @Override
    public void remapOrdinal(String type, int originalOrdinal, int mappedOrdinal) {
        throw new UnsupportedOperationException("Cannot remap ordinals in an IdentityOrdinalRemapper");
    }

    @Override
    public boolean ordinalIsMapped(String type, int originalOrdinal) {
        return true;
    }

}
