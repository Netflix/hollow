/*
 *  Copyright 2016-2021 Netflix, Inc.
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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;


public class FieldStatistics {

    private final HollowObjectSchema schema;

    private final int maxBitsForField[];
    private final long nullValueForField[];
    private final long totalSizeOfVarLengthField[];

    private int numBitsPerRecord;
    private final int bitOffsetForField[];

    public FieldStatistics(HollowObjectSchema schema) {
        this.schema = schema;
        this.maxBitsForField = new int[schema.numFields()];
        this.nullValueForField = new long[schema.numFields()];
        this.totalSizeOfVarLengthField = new long[schema.numFields()];
        this.bitOffsetForField = new int[schema.numFields()];
    }

    public int getNumBitsPerRecord() {
        return numBitsPerRecord;
    }

    public int getFieldBitOffset(int fieldIndex) {
        return bitOffsetForField[fieldIndex];
    }

    public int getMaxBitsForField(int fieldIndex) {
        return maxBitsForField[fieldIndex];
    }

    public long getNullValueForField(int fieldIndex) {
        return nullValueForField[fieldIndex];
    }

    public void addFixedLengthFieldRequiredBits(int fieldIndex, int numberOfBits) {
        if(numberOfBits > maxBitsForField[fieldIndex])
            maxBitsForField[fieldIndex] = numberOfBits;
    }

    public void addVarLengthFieldSize(int fieldIndex, int fieldSize) {
        totalSizeOfVarLengthField[fieldIndex] += fieldSize;
    }

    public void completeCalculations() {
        for(int i = 0; i < schema.numFields(); i++) {
            if(schema.getFieldType(i) == FieldType.STRING || schema.getFieldType(i) == FieldType.BYTES) {
                maxBitsForField[i] = bitsRequiredForRepresentation(totalSizeOfVarLengthField[i]) + 1; // one extra bit for null.
            }

            nullValueForField[i] = maxBitsForField[i] == 64 ? -1L : (1L << maxBitsForField[i]) - 1;

            bitOffsetForField[i] = numBitsPerRecord;
            numBitsPerRecord += maxBitsForField[i];
        }
    }

    public long getTotalSizeOfAllVarLengthData() {
        long totalVarLengthDataSize = 0;
        for(int i = 0; i < totalSizeOfVarLengthField.length; i++)
            totalVarLengthDataSize += totalSizeOfVarLengthField[i];
        return totalVarLengthDataSize;
    }

    private int bitsRequiredForRepresentation(long value) {
        return 64 - Long.numberOfLeadingZeros(value + 1);
    }
}
