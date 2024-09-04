package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FlatRecordTraversalObjectNodeEquality {
    private static final Map<String, HollowObjectSchema> commonSchemaCache = new HashMap<>();

    public static boolean equals(FlatRecordTraversalObjectNode left, FlatRecordTraversalObjectNode right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        if (!left.getSchema().getName().equals(right.getSchema().getName())) {
            return false;
        }
        extractCommonObjectSchema(left, right);

        return compare(left, right);
    }

    private static boolean compare(FlatRecordTraversalNode left, FlatRecordTraversalNode right) {
        if(left == null && right == null) {
            return true;
        }
        if(left == null || right == null) {
            return false;
        }
        if(!left.getSchema().getName().equals(right.getSchema().getName())) {
            return false;
        }
        left.setCommonSchema(commonSchemaCache);
        right.setCommonSchema(commonSchemaCache);
        if(left instanceof FlatRecordTraversalObjectNode && right instanceof FlatRecordTraversalObjectNode) {
            FlatRecordTraversalObjectNode leftObjectNode = (FlatRecordTraversalObjectNode) left;
            FlatRecordTraversalObjectNode rightObjectNode = (FlatRecordTraversalObjectNode) right;
            if(leftObjectNode.hashCode() != rightObjectNode.hashCode()) {
                return false;
            }
            for(int i=0;i<commonSchemaCache.get(leftObjectNode.getSchema().getName()).numFields();i++) {
                String fieldName = leftObjectNode.getSchema().getFieldName(i);
                if(leftObjectNode.getSchema().getFieldType(fieldName).equals(HollowObjectSchema.FieldType.REFERENCE)) {
                    FlatRecordTraversalNode leftChildNode = leftObjectNode.getFieldNode(fieldName);
                    FlatRecordTraversalNode rightChildNode = rightObjectNode.getFieldNode(fieldName);
                    if(commonSchemaCache.containsKey(leftObjectNode.getSchema().getName()) && commonSchemaCache.containsKey(rightObjectNode.getSchema().getName())) {
                        if(!compare(leftChildNode, rightChildNode)) {
                            return false;
                        }
                    }
                }
            }
        }
        else if(left instanceof FlatRecordTraversalSetNode && right instanceof FlatRecordTraversalSetNode) {
            if(left.hashCode() != right.hashCode()) {
                return false;
            }
        }
        else if(left instanceof FlatRecordTraversalListNode && right instanceof FlatRecordTraversalListNode) {
            if(left.hashCode() != right.hashCode()) {
                return false;
            }
        }
        else if(left instanceof FlatRecordTraversalMapNode && right instanceof FlatRecordTraversalMapNode) {
            if(left.hashCode() != right.hashCode()) {
                return false;
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported schema type: " + left.getSchema().getSchemaType());
        }
        return true;
    }



    private static void extractCommonObjectSchema(FlatRecordTraversalNode left, FlatRecordTraversalNode right) {
        if (left == null || right == null) {
            return ;
        }
        if (!left.getSchema().getName().equals(right.getSchema().getName())) {
            return ;
        }
        if(left instanceof FlatRecordTraversalObjectNode && right instanceof FlatRecordTraversalObjectNode) {
                FlatRecordTraversalObjectNode leftObjectNode = (FlatRecordTraversalObjectNode) left;
                FlatRecordTraversalObjectNode rightObjectNode = (FlatRecordTraversalObjectNode) right;
                HollowObjectSchema commonSchema = leftObjectNode.getSchema().findCommonSchema(rightObjectNode.getSchema());
                assert leftObjectNode.getSchema().getName().equals(rightObjectNode.getSchema().getName());
                commonSchemaCache.put(left.getSchema().getName(), commonSchema);

                for(int i=0;i<commonSchema.numFields();i++) {
                    String fieldName = commonSchema.getFieldName(i);
                    // same fieldType and equal to reference
                    if(leftObjectNode.getSchema().getFieldType(fieldName).equals(HollowObjectSchema.FieldType.REFERENCE)) {
                        FlatRecordTraversalNode leftChildNode = leftObjectNode.getFieldNode(fieldName);
                        FlatRecordTraversalNode rightChildNode = rightObjectNode.getFieldNode(fieldName);
                        extractCommonObjectSchema(leftChildNode, rightChildNode);
                    }

            }
        }
        else if (left instanceof FlatRecordTraversalSetNode && right instanceof FlatRecordTraversalSetNode) {
                FlatRecordTraversalSetNode leftSetNode = (FlatRecordTraversalSetNode) left;
                FlatRecordTraversalSetNode rightSetNode = (FlatRecordTraversalSetNode) right;
                Iterator<FlatRecordTraversalNode> leftIterator = leftSetNode.iterator();
                Iterator<FlatRecordTraversalNode> rightIterator = rightSetNode.iterator();
                if (leftIterator.hasNext() && rightIterator.hasNext()) {
                    FlatRecordTraversalNode leftChildNode = leftIterator.next();
                    FlatRecordTraversalNode rightChildNode = rightIterator.next();
                    extractCommonObjectSchema(leftChildNode, rightChildNode);
                }
        }
        else if (left instanceof FlatRecordTraversalListNode && right instanceof FlatRecordTraversalListNode) {
                FlatRecordTraversalListNode leftListNode = (FlatRecordTraversalListNode) left;
                FlatRecordTraversalListNode rightListNode = (FlatRecordTraversalListNode) right;
                Iterator<FlatRecordTraversalNode> leftIterator = leftListNode.iterator();
                Iterator<FlatRecordTraversalNode> rightIterator = rightListNode.iterator();
                if (leftIterator.hasNext() && rightIterator.hasNext()) {
                    FlatRecordTraversalNode leftChildNode = leftIterator.next();
                    FlatRecordTraversalNode rightChildNode = rightIterator.next();
                    extractCommonObjectSchema(leftChildNode, rightChildNode);
                }
        }
        else if (left instanceof FlatRecordTraversalMapNode && right instanceof FlatRecordTraversalMapNode) {
                FlatRecordTraversalMapNode leftMapNode = (FlatRecordTraversalMapNode) left;
                FlatRecordTraversalMapNode rightMapNode = (FlatRecordTraversalMapNode) right;
                Iterator<Map.Entry<FlatRecordTraversalNode, FlatRecordTraversalNode>> leftIterator = leftMapNode.entrySet().iterator();
                Iterator<Map.Entry<FlatRecordTraversalNode, FlatRecordTraversalNode>> rightIterator = rightMapNode.entrySet().iterator();
                if (leftIterator.hasNext() && rightIterator.hasNext()) {
                    Map.Entry<FlatRecordTraversalNode, FlatRecordTraversalNode> leftEntry = leftIterator.next();
                    Map.Entry<FlatRecordTraversalNode, FlatRecordTraversalNode> rightEntry = rightIterator.next();
                    extractCommonObjectSchema(leftEntry.getKey(), rightEntry.getKey());
                    extractCommonObjectSchema(leftEntry.getValue(), rightEntry.getValue());
                }
        }
    }
}
