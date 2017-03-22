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
package com.netflix.hollow.tools.diff;

import java.util.Arrays;

import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class HollowDiffUnmatchedRecordFieldCounter {
    
    private final HollowDiff diff;
    private final HollowTypeReadState fromState;
    private final HollowTypeReadState toState;

    private final int unmatchedDiffScoresByFromOrdinal[];
    private final int unmatchedDiffScoresByToOrdinal[];

    public HollowDiffUnmatchedRecordFieldCounter(HollowDiff diff, HollowTypeReadState fromState, HollowTypeReadState toState) {
        this.diff = diff;
        this.fromState = fromState;
        this.toState = toState;
        this.unmatchedDiffScoresByFromOrdinal = new int[fromState.maxOrdinal()+1];
        this.unmatchedDiffScoresByToOrdinal = new int[toState.maxOrdinal()+1];
        
        Arrays.fill(unmatchedDiffScoresByFromOrdinal, -1);
        Arrays.fill(unmatchedDiffScoresByToOrdinal, -1);
    }
    
    
    public int getUnmatchedFromRecordDiffScore(int fromOrdinal) {
        if(unmatchedDiffScoresByFromOrdinal[fromOrdinal] == -1)
            unmatchedDiffScoresByFromOrdinal[fromOrdinal] = countFields(true, fromState, fromOrdinal);
        return unmatchedDiffScoresByFromOrdinal[fromOrdinal];
    }
    
    public int getUnmatchedToRecordDiffScore(int toOrdinal) {
        if(unmatchedDiffScoresByToOrdinal[toOrdinal] == -1)
            unmatchedDiffScoresByToOrdinal[toOrdinal] = countFields(false, toState, toOrdinal);
        return unmatchedDiffScoresByToOrdinal[toOrdinal];
    }
    
    
    private int countFields(boolean isFrom, HollowTypeReadState typeState, int ordinal) {
        int score = 0;
        
        switch(typeState.getSchema().getSchemaType()) {
        case OBJECT:
            HollowObjectSchema objectSchema = (HollowObjectSchema) typeState.getSchema();
            
            for(int i=0;i<objectSchema.numFields();i++) {
                if(objectSchema.getFieldType(i) == FieldType.REFERENCE)
                    score += lookupOrCountFields(isFrom, objectSchema.getReferencedTypeState(i), ((HollowObjectTypeReadState)typeState).readOrdinal(ordinal, i));
                else
                    score++;
            }
            
            break;
            
        case LIST:
        case SET:
            
            HollowTypeReadState elementTypeState = ((HollowCollectionSchema)typeState.getSchema()).getElementTypeState();
            
            HollowOrdinalIterator elementIter = ((HollowCollectionTypeReadState)typeState).ordinalIterator(ordinal);
            
            int elementOrdinal = elementIter.next();
            while(elementOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                score += lookupOrCountFields(isFrom, elementTypeState, elementOrdinal);
                elementOrdinal = elementIter.next();
            }
            
            break;
            
        case MAP:
            HollowMapSchema mapSchema = (HollowMapSchema)typeState.getSchema();
            HollowTypeReadState keyTypeState = mapSchema.getKeyTypeState();
            HollowTypeReadState valueTypeState = mapSchema.getValueTypeState();
            
            HollowMapEntryOrdinalIterator entryIter = ((HollowMapTypeReadState)typeState).ordinalIterator(ordinal);
            
            while(entryIter.next()) {
                score += lookupOrCountFields(isFrom, keyTypeState, entryIter.getKey());
                score += lookupOrCountFields(isFrom, valueTypeState, entryIter.getValue());
            }
            
            break;
        }
        
        return score;
    }
    
    private int lookupOrCountFields(boolean isFrom, HollowTypeReadState typeState, int ordinal) {
        if(ordinal == -1)
            return 0;
        
        HollowTypeDiff typeDiff = diff.getTypeDiff(typeState.getSchema().getName());
        
        if(typeDiff != null) {
            HollowDiffUnmatchedRecordFieldCounter unmatchedRecordFieldCounter = typeDiff.getUnmatchedRecordFieldCounter();
            return isFrom ? unmatchedRecordFieldCounter.getUnmatchedFromRecordDiffScore(ordinal) : unmatchedRecordFieldCounter.getUnmatchedToRecordDiffScore(ordinal);
        }
        
        return countFields(isFrom, typeState, ordinal);
    }

}
