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
package com.netflix.hollow.tools.diff.count;

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Counting nodes are used by the HollowDiff to count and aggregate changes for specific record types in a data model.
 * 
 * This type of counting node is applicable to object types.
 * 
 * Not intended for external consumption.
 */
public class HollowDiffObjectCountingNode extends HollowDiffCountingNode {

    private final HollowObjectTypeReadState fromState;
    private final HollowObjectTypeReadState toState;
    private final HollowObjectSchema fromSchema;
    private final HollowObjectSchema toSchema;
    private final HollowObjectSchema unionSchema;

    private final int[] fromFieldMapping;
    private final int[] toFieldMapping;
    private final HollowDiffCountingNode fieldNodes[];
    private final boolean fieldRequiresMissingFieldTraversal[];
    private final DiffEqualOrdinalFilter fieldEqualOrdinalFilters[];

    public HollowDiffObjectCountingNode(HollowDiff diff, HollowTypeDiff topLevelTypeDiff, HollowDiffNodeIdentifier nodeId, HollowObjectTypeReadState fromState, HollowObjectTypeReadState toState) {
        super(diff, topLevelTypeDiff, nodeId);
        this.fromState = fromState;
        this.toState = toState;
        this.fromSchema = fromState == null ? emptySchema(toState.getSchema()) : fromState.getSchema();
        this.toSchema = toState == null ? emptySchema(fromState.getSchema()) : toState.getSchema();

        if(!fromSchema.getName().equals(toSchema.getName()))
            throw new IllegalArgumentException("Cannot diff between two schemas with different names: from '" + fromSchema.getName() + "' to '" + toSchema.getName() + "'");

        this.unionSchema = fromSchema.findUnionSchema(toSchema);
        this.fieldNodes = new HollowDiffCountingNode[unionSchema.numFields()];
        this.fromFieldMapping = createFieldMapping(unionSchema, fromSchema);
        this.toFieldMapping = createFieldMapping(unionSchema, toSchema);

        this.fieldRequiresMissingFieldTraversal = new boolean[unionSchema.numFields()];
        this.fieldEqualOrdinalFilters = new DiffEqualOrdinalFilter[unionSchema.numFields()];

        for(int i = 0; i < unionSchema.numFields(); i++) {
            int fromFieldIndex = fromSchema.getPosition(unionSchema.getFieldName(i));
            int toFieldIndex = toSchema.getPosition(unionSchema.getFieldName(i));

            if(unionSchema.getFieldType(i) == FieldType.REFERENCE) {
                HollowTypeReadState refFromState = fromFieldIndex == -1 ? null : fromSchema.getReferencedTypeState(fromFieldIndex);
                HollowTypeReadState refToState = toFieldIndex == -1 ? null : toSchema.getReferencedTypeState(toFieldIndex);
                fieldNodes[i] = getHollowDiffCountingNode(refFromState, refToState, unionSchema.getFieldName(i));
                fieldEqualOrdinalFilters[i] = new DiffEqualOrdinalFilter(equalityMapping.getEqualOrdinalMap(unionSchema.getReferencedType(i)));
                if(refFromState == null || refToState == null || equalityMapping.requiresMissingFieldTraversal(unionSchema.getReferencedType(i)))
                    fieldRequiresMissingFieldTraversal[i] = true;
            } else {
                HollowDiffNodeIdentifier childNodeId = new HollowDiffNodeIdentifier(nodeId, unionSchema.getFieldName(i), unionSchema.getFieldType(i).toString());

                fieldNodes[i] = new HollowDiffFieldCountingNode(diff, topLevelTypeDiff, childNodeId, fromState, toState, unionSchema, i);
            }
        }
    }

    private HollowObjectSchema emptySchema(HollowObjectSchema other) {
        return new HollowObjectSchema(other.getName(), 0);
    }

    public void prepare(int topLevelFromOrdinal, int topLevelToOrdinal) {
        for(int i = 0; i < fieldNodes.length; i++) {
            fieldNodes[i].prepare(topLevelFromOrdinal, topLevelToOrdinal);
        }
    }

    private final IntList traversalFromOrdinals = new IntList();
    private final IntList traversalToOrdinals = new IntList();

    @Override
    public int traverseDiffs(IntList fromOrdinals, IntList toOrdinals) {
        int score = 0;

        for(int i = 0; i < fieldNodes.length; i++) {
            int fromFieldIdx = fromFieldMapping[i];
            int toFieldIdx = toFieldMapping[i];

            if(unionSchema.getFieldType(i) == FieldType.REFERENCE) {
                traversalFromOrdinals.clear();
                traversalToOrdinals.clear();

                if(fromFieldIdx != -1) {
                    for(int j = 0; j < fromOrdinals.size(); j++) {
                        int fromOrdinal = fromOrdinals.get(j);
                        int refOrdinal = fromState.readOrdinal(fromOrdinal, fromFieldIdx);
                        if(refOrdinal != -1)
                            traversalFromOrdinals.add(refOrdinal);
                    }
                }

                if(toFieldIdx != -1) {
                    for(int j = 0; j < toOrdinals.size(); j++) {
                        int toOrdinal = toOrdinals.get(j);
                        int refOrdinal = toState.readOrdinal(toOrdinal, toFieldIdx);
                        if(refOrdinal != -1)
                            traversalToOrdinals.add(refOrdinal);
                    }
                }

                if(traversalFromOrdinals.size() != 0 || traversalToOrdinals.size() != 0) {
                    fieldEqualOrdinalFilters[i].filter(traversalFromOrdinals, traversalToOrdinals);

                    if(fieldEqualOrdinalFilters[i].getUnmatchedFromOrdinals().size() != 0 || fieldEqualOrdinalFilters[i].getUnmatchedToOrdinals().size() != 0)
                        score += fieldNodes[i].traverseDiffs(fieldEqualOrdinalFilters[i].getUnmatchedFromOrdinals(), fieldEqualOrdinalFilters[i].getUnmatchedToOrdinals());
                    if(fieldRequiresMissingFieldTraversal[i])
                        if(fieldEqualOrdinalFilters[i].getMatchedFromOrdinals().size() != 0 || fieldEqualOrdinalFilters[i].getMatchedToOrdinals().size() != 0)
                            score += fieldNodes[i].traverseMissingFields(fieldEqualOrdinalFilters[i].getMatchedFromOrdinals(), fieldEqualOrdinalFilters[i].getMatchedToOrdinals());
                }

            } else {
                if(fromFieldIdx == -1)
                    score += fieldNodes[i].traverseDiffs(EMPTY_ORDINAL_LIST, toOrdinals);
                else if(toFieldIdx == -1)
                    score += fieldNodes[i].traverseDiffs(fromOrdinals, EMPTY_ORDINAL_LIST);
                else
                    score += fieldNodes[i].traverseDiffs(fromOrdinals, toOrdinals);
            }
        }

        return score;
    }

    public int traverseMissingFields(IntList fromOrdinals, IntList toOrdinals) {
        int score = 0;

        for(int i = 0; i < fieldNodes.length; i++) {
            if(fieldRequiresMissingFieldTraversal[i]) {
                traversalFromOrdinals.clear();
                traversalToOrdinals.clear();

                if(fromFieldMapping[i] != -1) {
                    for(int j = 0; j < fromOrdinals.size(); j++) {
                        int fromOrdinal = fromState.readOrdinal(fromOrdinals.get(j), fromFieldMapping[i]);
                        if(fromOrdinal != -1)
                            traversalFromOrdinals.add(fromOrdinal);
                    }
                }

                if(toFieldMapping[i] != -1) {
                    for(int j = 0; j < toOrdinals.size(); j++) {
                        int toOrdinal = toState.readOrdinal(toOrdinals.get(j), toFieldMapping[i]);
                        if(toOrdinal != -1)
                            traversalToOrdinals.add(toOrdinal);
                    }
                }

                score += fieldNodes[i].traverseMissingFields(traversalFromOrdinals, traversalToOrdinals);
            } else if(fieldNodes[i] instanceof HollowDiffFieldCountingNode) {
                score += fieldNodes[i].traverseMissingFields(fromOrdinals, toOrdinals);
            }
        }

        return score;
    }

    private int[] createFieldMapping(HollowObjectSchema unionSchema, HollowObjectSchema individualSchema) {
        int mapping[] = new int[unionSchema.numFields()];
        for(int i = 0; i < unionSchema.numFields(); i++) {
            String fieldName = unionSchema.getFieldName(i);
            mapping[i] = individualSchema.getPosition(fieldName);
        }
        return mapping;
    }

    @Override
    public List<HollowFieldDiff> getFieldDiffs() {
        List<HollowFieldDiff> list = new ArrayList<HollowFieldDiff>();

        for(HollowDiffCountingNode node : fieldNodes) {
            list.addAll(node.getFieldDiffs());
        }

        return list;
    }
}
