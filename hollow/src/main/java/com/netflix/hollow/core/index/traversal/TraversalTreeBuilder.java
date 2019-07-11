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

import com.netflix.hollow.core.read.dataaccess.HollowCollectionTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.IntList;
import java.util.ArrayList;
import java.util.List;

/**
 * Not intended for external consumption.
 */
class TraversalTreeBuilder {

    private final HollowDataAccess dataAccess;
    private final String type;
    private final String[] fieldPaths;
    private final IntList[] fieldMatchLists;
    private final HollowTypeDataAccess[] fieldTypeDataAccess;
    private final int[] fieldSchemaPositions;

    public TraversalTreeBuilder(HollowDataAccess dataAccess, String type, String[] fieldPaths) {
        this.dataAccess = dataAccess;
        this.type = type;
        this.fieldPaths = fieldPaths;
        this.fieldMatchLists = new IntList[fieldPaths.length];
        for(int i=0;i<fieldPaths.length;i++)
            fieldMatchLists[i] = new IntList();
        this.fieldTypeDataAccess = new HollowTypeDataAccess[fieldPaths.length];
        this.fieldSchemaPositions = new int[fieldPaths.length];
    }

    public IntList[] getFieldMatchLists() {
        return fieldMatchLists;
    }

    public HollowTypeDataAccess[] getFieldTypeDataAccesses() {
        return fieldTypeDataAccess;
    }

    public int[] getFieldSchemaPositions() {
        return fieldSchemaPositions;
    }

    public HollowIndexerTraversalNode buildTree() {
        HollowTypeDataAccess rootTypeDataAccess = dataAccess.getTypeDataAccess(type);
        HollowIndexerTraversalNode rootNode = createTypeNode(rootTypeDataAccess);

        List<HollowIndexerTraversalNode> allNodes = new ArrayList<HollowIndexerTraversalNode>();
        allNodes.add(rootNode);

        for(int i=0;i<fieldPaths.length;i++) {
            String fieldPath = fieldPaths[i];
            String pathElements[] = "".equals(fieldPath) ? new String[0] : fieldPath.split("\\.");

            if(pathElements.length == 0) {
                rootNode.setIndexedFieldPosition(i);
                fieldTypeDataAccess[i] = rootTypeDataAccess;
            } else {
                HollowTypeDataAccess typeDataAccess = rootTypeDataAccess;
                HollowIndexerTraversalNode currentNode = rootNode;

                for(int j=0;j<pathElements.length;j++) {
                    String pathElement = pathElements[j];
                    HollowIndexerTraversalNode child = currentNode.getChild(pathElement);
                    if(child == null) {
                        child = createChildNode(typeDataAccess, pathElement);
                        currentNode.addChild(pathElement, child);
                        allNodes.add(child);
                    }

                    currentNode = child;

                    if(j == pathElements.length - 1) {
                        currentNode.setIndexedFieldPosition(i);
                        fieldTypeDataAccess[i] = typeDataAccess;
                        if(typeDataAccess instanceof HollowObjectTypeDataAccess) {
                            HollowObjectSchema schema = (HollowObjectSchema)typeDataAccess.getSchema();
                            if(schema.getFieldType(pathElement) == FieldType.REFERENCE) {
                                fieldSchemaPositions[i] = -1;
                                fieldTypeDataAccess[i] = getChildDataAccess(typeDataAccess, pathElement);
                            } else {
                                fieldSchemaPositions[i] = schema.getPosition(pathElement);
                                fieldTypeDataAccess[i] = typeDataAccess;
                            }
                        } else if (typeDataAccess instanceof HollowMapTypeDataAccess) {
                            fieldTypeDataAccess[i] = getChildDataAccess(typeDataAccess, pathElement);
                            fieldSchemaPositions[i] = -1;
                        } else if (typeDataAccess instanceof HollowCollectionTypeDataAccess) {
                            fieldTypeDataAccess[i] = getChildDataAccess(typeDataAccess, pathElement);
                            fieldSchemaPositions[i] = -1;
                        }
                    } else {
                        typeDataAccess = getChildDataAccess(typeDataAccess, pathElement);
                    }
                }
            }
        }

        for(HollowIndexerTraversalNode node : allNodes) {
            node.setUpMultiplication();
            node.setUpChildren();
        }

        return rootNode;
    }


    private HollowIndexerTraversalNode createChildNode(HollowTypeDataAccess typeDataAccess, String childName) {
        if(typeDataAccess instanceof HollowObjectTypeDataAccess) {
            HollowObjectTypeDataAccess objectAccess = (HollowObjectTypeDataAccess) typeDataAccess;
            HollowObjectSchema schema = objectAccess.getSchema();
            int fieldIdx = schema.getPosition(childName);

            if(schema.getFieldType(fieldIdx) == FieldType.REFERENCE) {
                String childType = schema.getReferencedType(fieldIdx);
                HollowTypeDataAccess childTypeAccess = dataAccess.getTypeDataAccess(childType);
                return createTypeNode(childTypeAccess);
            } else {
                return new HollowIndexerObjectFieldTraversalNode(objectAccess, fieldMatchLists);
            }
        } else if(typeDataAccess instanceof HollowCollectionTypeDataAccess) {
            HollowCollectionSchema schema = (HollowCollectionSchema) typeDataAccess.getSchema();
            HollowTypeDataAccess childTypeAccess = dataAccess.getTypeDataAccess(schema.getElementType());
            return createTypeNode(childTypeAccess);
        } else if(typeDataAccess instanceof HollowMapTypeDataAccess) {
            HollowMapSchema schema = (HollowMapSchema) typeDataAccess.getSchema();
            String childType = "key".equals(childName) ? schema.getKeyType() : schema.getValueType();
            HollowTypeDataAccess childTypeAccess = dataAccess.getTypeDataAccess(childType);
            return createTypeNode(childTypeAccess);
        }

        throw new IllegalArgumentException("I can't create a child node for a " + typeDataAccess.getClass());
    }

    private HollowTypeDataAccess getChildDataAccess(HollowTypeDataAccess typeDataAccess, String childName) {
        if(typeDataAccess instanceof HollowObjectTypeDataAccess) {
            HollowObjectSchema schema = (HollowObjectSchema) typeDataAccess.getSchema();
            int fieldIdx = schema.getPosition(childName);
            String childType = schema.getReferencedType(fieldIdx);
            return dataAccess.getTypeDataAccess(childType);
        } else if(typeDataAccess instanceof HollowCollectionTypeDataAccess) {
            HollowCollectionSchema schema = (HollowCollectionSchema) typeDataAccess.getSchema();
            return dataAccess.getTypeDataAccess(schema.getElementType());
        } else if(typeDataAccess instanceof HollowMapTypeDataAccess) {
            HollowMapSchema schema = (HollowMapSchema) typeDataAccess.getSchema();
            String childType = "key".equals(childName) ? schema.getKeyType() : schema.getValueType();
            return dataAccess.getTypeDataAccess(childType);
        }

        throw new IllegalArgumentException("I can't create a child node for a " + typeDataAccess.getClass());
    }

    private HollowIndexerTraversalNode createTypeNode(HollowTypeDataAccess typeDataAccess) {
        if(typeDataAccess instanceof HollowObjectTypeDataAccess)
            return new HollowIndexerObjectTraversalNode((HollowObjectTypeDataAccess) typeDataAccess, fieldMatchLists);
        else if(typeDataAccess instanceof HollowListTypeDataAccess)
            return new HollowIndexerListTraversalNode((HollowListTypeDataAccess) typeDataAccess, fieldMatchLists);
        else if(typeDataAccess instanceof HollowSetTypeDataAccess)
            return new HollowIndexerCollectionTraversalNode(typeDataAccess, fieldMatchLists);
        else if(typeDataAccess instanceof HollowMapTypeDataAccess)
            return new HollowIndexerMapTraversalNode(typeDataAccess, fieldMatchLists);

        throw new IllegalArgumentException("I can't create a type node for a " + typeDataAccess.getClass());
    }

}
