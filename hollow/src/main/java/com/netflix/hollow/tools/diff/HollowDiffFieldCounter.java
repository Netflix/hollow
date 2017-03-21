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

public class HollowDiffFieldCounter {
    
    private final HollowDiff diff;
    private final boolean isFrom;
    private final HollowTypeReadState typeState;
    
    public HollowDiffFieldCounter(HollowDiff diff, boolean isFrom, HollowTypeReadState typeState) {
        this.diff = diff;
        this.isFrom = isFrom;
        this.typeState = typeState;
    }
    
    public int countFields(int ordinal) {
        return countFields(diff, isFrom, typeState, ordinal);
    }
    
    private static int countFields(HollowDiff diff, boolean isFrom, HollowTypeReadState typeState, int ordinal) {
        int score = 0;
        
        switch(typeState.getSchema().getSchemaType()) {
        case OBJECT:
            HollowObjectSchema objectSchema = (HollowObjectSchema) typeState.getSchema();
            
            for(int i=0;i<objectSchema.numFields();i++) {
                if(objectSchema.getFieldType(i) == FieldType.REFERENCE)
                    score += lookupOrCountFields(diff, isFrom, objectSchema.getReferencedTypeState(i), ((HollowObjectTypeReadState)typeState).readOrdinal(ordinal, i));
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
                score += lookupOrCountFields(diff, isFrom, elementTypeState, elementOrdinal);
                elementOrdinal = elementIter.next();
            }
            
            break;
            
        case MAP:
            HollowMapSchema mapSchema = (HollowMapSchema)typeState.getSchema();
            HollowTypeReadState keyTypeState = mapSchema.getKeyTypeState();
            HollowTypeReadState valueTypeState = mapSchema.getValueTypeState();
            
            HollowMapEntryOrdinalIterator entryIter = ((HollowMapTypeReadState)typeState).ordinalIterator(ordinal);
            
            while(entryIter.next()) {
                score += lookupOrCountFields(diff, isFrom, keyTypeState, entryIter.getKey());
                score += lookupOrCountFields(diff, isFrom, valueTypeState, entryIter.getValue());
            }
            
            break;
        }
        
        return score;
    }
    
    private static int lookupOrCountFields(HollowDiff diff, boolean isFrom, HollowTypeReadState typeState, int ordinal) {
        if(ordinal == -1)
            return 0;
        
        HollowTypeDiff typeDiff = diff.getTypeDiff(typeState.getSchema().getName());
        
        if(typeDiff != null) {
            return isFrom ? typeDiff.getUnmatchedRecordDiffScoreByFromOrdinal(ordinal) : typeDiff.getUnmatchedRecordDiffScoreByToOrdinal(ordinal);
        }
        
        return countFields(diff, isFrom, typeState, ordinal);
    }

}
