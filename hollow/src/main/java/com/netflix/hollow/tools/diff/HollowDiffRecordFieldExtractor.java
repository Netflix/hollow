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
package com.netflix.hollow.tools.diff;

import com.netflix.hollow.core.read.dataaccess.HollowCollectionTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.IntList;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility to extract the values of a field identified by a {@link HollowDiffNodeIdentifier} for a specific record in a {@link HollowDataAccess}. 
 */
public class HollowDiffRecordFieldExtractor {

    public List<Object> extractValues(HollowDataAccess dataAccess, HollowDiffNodeIdentifier fieldIdentifier, int ordinal) {
        IntList ordinalList = new IntList(1);
        ordinalList.add(ordinal);
        return traverse(dataAccess.getTypeDataAccess(getType(fieldIdentifier)), ordinalList, fieldIdentifier, 0);
    }

    private List<Object> traverse(HollowTypeDataAccess typeDataAccess, IntList ordinals, HollowDiffNodeIdentifier fieldIdentifier, int level) {

        if(level == fieldIdentifier.getParents().size() - 1) {
            return extractValues(typeDataAccess, ordinals, fieldIdentifier);
        } else {
            HollowTypeDataAccess childDataAccess = null;
            IntList childOrdinals = new IntList();

            if(typeDataAccess instanceof HollowObjectTypeDataAccess) {
                HollowObjectTypeDataAccess objectAccess = (HollowObjectTypeDataAccess) typeDataAccess;
                int fieldIdx = objectAccess.getSchema().getPosition(fieldIdentifier.getParents().get(level + 1).getViaFieldName());
                childDataAccess = typeDataAccess.getDataAccess().getTypeDataAccess(objectAccess.getSchema().getReferencedType(fieldIdx));
                for(int i = 0; i < ordinals.size(); i++)
                    childOrdinals.add(objectAccess.readOrdinal(ordinals.get(i), fieldIdx));
            } else if(typeDataAccess instanceof HollowCollectionTypeDataAccess) {
                HollowCollectionTypeDataAccess collectionAccess = (HollowCollectionTypeDataAccess) typeDataAccess;
                childDataAccess = typeDataAccess.getDataAccess().getTypeDataAccess(collectionAccess.getSchema().getElementType());
                for(int i = 0; i < ordinals.size(); i++) {
                    HollowOrdinalIterator iter = collectionAccess.ordinalIterator(ordinals.get(i));
                    int childOrdinal = iter.next();
                    while(childOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                        childOrdinals.add(childOrdinal);
                        childOrdinal = iter.next();
                    }
                }
            } else if(typeDataAccess instanceof HollowMapTypeDataAccess) {
                HollowMapTypeDataAccess mapAccess = (HollowMapTypeDataAccess) typeDataAccess;
                boolean isValue = fieldIdentifier.getParents().get(level + 1).getViaFieldName().equals("value");
                String childType = isValue ? mapAccess.getSchema().getValueType() : mapAccess.getSchema().getKeyType();
                childDataAccess = typeDataAccess.getDataAccess().getTypeDataAccess(childType);
                for(int i = 0; i < ordinals.size(); i++) {
                    HollowMapEntryOrdinalIterator iter = mapAccess.ordinalIterator(ordinals.get(i));
                    while(iter.next()) {
                        childOrdinals.add(isValue ? iter.getValue() : iter.getKey());
                    }
                }
            }

            return traverse(childDataAccess, childOrdinals, fieldIdentifier, level + 1);
        }
    }

    private List<Object> extractValues(HollowTypeDataAccess typeDataAccess, IntList ordinals, HollowDiffNodeIdentifier fieldIdentifier) {
        List<Object> values = new ArrayList<Object>();

        HollowObjectTypeDataAccess objectAccess = (HollowObjectTypeDataAccess) typeDataAccess;
        int fieldIdx = objectAccess.getSchema().getPosition(fieldIdentifier.getViaFieldName());

        for(int i = 0; i < ordinals.size(); i++) {
            switch(objectAccess.getSchema().getFieldType(fieldIdx)) {
                case BOOLEAN:
                    values.add(objectAccess.readBoolean(ordinals.get(i), fieldIdx));
                    break;
                case BYTES:
                    values.add(objectAccess.readBytes(ordinals.get(i), fieldIdx));
                    break;
                case DOUBLE:
                    values.add(objectAccess.readDouble(ordinals.get(i), fieldIdx));
                    break;
                case FLOAT:
                    values.add(objectAccess.readFloat(ordinals.get(i), fieldIdx));
                    break;
                case INT:
                    values.add(objectAccess.readInt(ordinals.get(i), fieldIdx));
                    break;
                case LONG:
                    values.add(objectAccess.readLong(ordinals.get(i), fieldIdx));
                    break;
                case STRING:
                    values.add(objectAccess.readString(ordinals.get(i), fieldIdx));
                    break;
                case REFERENCE:
                    throw new IllegalArgumentException();
            }
        }

        return values;
    }

    private final String getType(HollowDiffNodeIdentifier nodeId) {
        return nodeId.getParents().get(0).getNodeName();
    }

}
