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
package com.netflix.hollow.core.index.traversal;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.util.IntList;
import java.util.BitSet;

/**
 * Used by the HollowHashIndex to traverse the possible value combinations of individual records given 
 * a set of field paths.
 */
public class HollowIndexerValueTraverser {

    private final String fieldPaths[];
    private final HollowIndexerTraversalNode rootNode;
    private final IntList fieldMatchLists[];
    private final HollowTypeDataAccess fieldTypeDataAccess[];
    private final int fieldSchemaPosition[];

    public HollowIndexerValueTraverser(HollowDataAccess dataAccess, String type, String... fieldPaths) {
        this.fieldPaths = fieldPaths;

        TraversalTreeBuilder builder = new TraversalTreeBuilder(dataAccess, type, fieldPaths);

        this.rootNode = builder.buildTree();
        this.fieldMatchLists = builder.getFieldMatchLists();
        this.fieldTypeDataAccess = builder.getFieldTypeDataAccesses();
        this.fieldSchemaPosition = builder.getFieldSchemaPositions();
    }

    public void traverse(int ordinal) {
        for(int i = 0; i < fieldMatchLists.length; i++)
            fieldMatchLists[i].clear();

        rootNode.traverse(ordinal);
    }

    public int getNumFieldPaths() {
        return fieldPaths.length;
    }

    public String getFieldPath(int idx) {
        return fieldPaths[idx];
    }

    public int getNumMatches() {
        return fieldMatchLists[0].size();
    }

    public Object getMatchedValue(int matchIdx, int fieldIdx) {
        int matchedOrdinal = fieldMatchLists[fieldIdx].get(matchIdx);
        return HollowReadFieldUtils.fieldValueObject((HollowObjectTypeDataAccess) fieldTypeDataAccess[fieldIdx], matchedOrdinal, fieldSchemaPosition[fieldIdx]);
    }

    public boolean isMatchedValueEqual(int matchIdx, int fieldIdx, Object value) {
        int matchedOrdinal = fieldMatchLists[fieldIdx].get(matchIdx);
        return HollowReadFieldUtils.fieldValueEquals((HollowObjectTypeDataAccess) fieldTypeDataAccess[fieldIdx], matchedOrdinal, fieldSchemaPosition[fieldIdx], value);
    }

    public int getMatchHash(int matchIdx) {
        int hashCode = 0;
        for(int i = 0; i < getNumFieldPaths(); i++) {
            hashCode ^= HashCodes.hashInt(HollowReadFieldUtils.fieldHashCode((HollowObjectTypeDataAccess) fieldTypeDataAccess[i], fieldMatchLists[i].get(matchIdx), fieldSchemaPosition[i]));
            hashCode ^= HashCodes.hashInt(hashCode);
        }
        return hashCode;
    }

    public int getMatchHash(int matchIdx, BitSet fields) {
        int hashCode = 0;
        for(int i = 0; i < getNumFieldPaths(); i++) {
            if(fields.get(i)) {
                hashCode ^= HashCodes.hashInt(HollowReadFieldUtils.fieldHashCode((HollowObjectTypeDataAccess) fieldTypeDataAccess[i], fieldMatchLists[i].get(matchIdx), fieldSchemaPosition[i]));
                hashCode ^= HashCodes.hashInt(hashCode);
            }
        }
        return hashCode;
    }


    /**
     * This method assumes the other traverser has the same match fields specified in the same order.
     *
     * @param matchIdx the match index
     * @param otherTraverser the other traverser
     * @param otherMatchIdx the other match index
     * @return true if this and the other traverser are equal
     */
    public boolean isMatchEqual(int matchIdx, HollowIndexerValueTraverser otherTraverser, int otherMatchIdx) {
        for(int i = 0; i < getNumFieldPaths(); i++) {
            if(!HollowReadFieldUtils.fieldsAreEqual((HollowObjectTypeDataAccess) fieldTypeDataAccess[i], fieldMatchLists[i].get(matchIdx), fieldSchemaPosition[i],
                    (HollowObjectTypeDataAccess) otherTraverser.fieldTypeDataAccess[i], otherTraverser.fieldMatchLists[i].get(otherMatchIdx), otherTraverser.fieldSchemaPosition[i]))
                return false;
        }
        return true;
    }

    public boolean isMatchEqual(int matchIdx, HollowIndexerValueTraverser otherTraverser, int otherMatchIdx, BitSet fields) {
        for(int i = 0; i < getNumFieldPaths(); i++) {
            if(fields.get(i)) {
                if(!HollowReadFieldUtils.fieldsAreEqual((HollowObjectTypeDataAccess) fieldTypeDataAccess[i], fieldMatchLists[i].get(matchIdx), fieldSchemaPosition[i],
                        (HollowObjectTypeDataAccess) otherTraverser.fieldTypeDataAccess[i], otherTraverser.fieldMatchLists[i].get(otherMatchIdx), otherTraverser.fieldSchemaPosition[i]))
                    return false;
            }
        }
        return true;
    }

    public int getMatchOrdinal(int matchIdx, int fieldIdx) {
        return fieldMatchLists[fieldIdx].get(matchIdx);
    }

    public HollowTypeDataAccess getFieldTypeDataAccess(int fieldIdx) {
        return fieldTypeDataAccess[fieldIdx];
    }
}
