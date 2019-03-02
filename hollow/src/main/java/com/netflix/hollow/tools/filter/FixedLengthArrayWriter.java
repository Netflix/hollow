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
package com.netflix.hollow.tools.filter;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;

class FixedLengthArrayWriter {

    private final FixedLengthElementArray arr;
    private long bitCursor;

    public FixedLengthArrayWriter(FixedLengthElementArray arr) {
        this.arr = arr;
    }

    public void writeField(long value, int numBits) {
        arr.setElementValue(bitCursor, numBits, value);
        bitCursor += numBits;
    }

}
