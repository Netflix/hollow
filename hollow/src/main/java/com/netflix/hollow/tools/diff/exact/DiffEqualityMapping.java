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
package com.netflix.hollow.tools.diff.exact;

import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.exact.mapper.DiffEqualityCollectionMapper;
import com.netflix.hollow.tools.diff.exact.mapper.DiffEqualityMapMapper;
import com.netflix.hollow.tools.diff.exact.mapper.DiffEqualityObjectMapper;
import com.netflix.hollow.tools.diff.exact.mapper.DiffEqualityOrderedListMapper;
import com.netflix.hollow.tools.diff.exact.mapper.DiffEqualityTypeMapper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The {@link HollowDiff} uses this class to expedite diff processing -- this class determines pairs of records which are exactly equal.
 * 
 * This calculation is relatively easy, and these record pairs can be entirely excluded while diffing a hierarchy. 
 * 
 * Not intended for external consumption.
 *
 */
public class DiffEqualityMapping {

    private final Logger log = Logger.getLogger(DiffEqualityMapping.class.getName());
    private final HollowReadStateEngine fromState;
    private final HollowReadStateEngine toState;
    private final boolean oneToOne;
    private final boolean listOrderingIsImportant;

    private final Map<String, DiffEqualOrdinalMap> map = new HashMap<String, DiffEqualOrdinalMap>();
    private final Set<String> typesWhichRequireMissingFieldTraversal = new HashSet<String>();

    private boolean isPrepared;

    public DiffEqualityMapping(HollowReadStateEngine fromState, HollowReadStateEngine toState) {
        this(fromState, toState, false, true);
    }

    public DiffEqualityMapping(HollowReadStateEngine fromState, HollowReadStateEngine toState, boolean oneToOne, boolean listOrderingIsImportant) {
        this.fromState = fromState;
        this.toState = toState;
        this.oneToOne = oneToOne;
        this.listOrderingIsImportant = listOrderingIsImportant;
    }

    public boolean requiresMissingFieldTraversal(String type) {
        return typesWhichRequireMissingFieldTraversal.contains(type);
    }

    public DiffEqualOrdinalMap getEqualOrdinalMap(String type) {
        DiffEqualOrdinalMap ordinalMap = map.get(type);
        if(ordinalMap != null)
            return ordinalMap;
        return isPrepared ? DiffEqualOrdinalMap.EMPTY_MAP : buildMap(type);
    }

    public void markPrepared() {
        this.isPrepared = true;
    }

    private DiffEqualOrdinalMap buildMap(String type) {
        HollowTypeReadState fromTypeState = fromState.getTypeState(type);
        HollowTypeReadState toTypeState = toState.getTypeState(type);

        if(fromTypeState == null || toTypeState == null)
            return DiffEqualOrdinalMap.EMPTY_MAP;

        log.info("starting to build equality map for " + type);
        DiffEqualOrdinalMap map = buildMap(fromTypeState, toTypeState);
        log.info("finished building equality map for " + type);
        return map;
    }

    private DiffEqualOrdinalMap buildMap(HollowTypeReadState fromTypeState, HollowTypeReadState toTypeState) {
        String typeName = fromTypeState.getSchema().getName();
        DiffEqualityTypeMapper mapper = getTypeMapper(fromTypeState, toTypeState);
        DiffEqualOrdinalMap equalOrdinalMap = mapper.mapEqualObjects();
        if(mapper.requiresTraversalForMissingFields())
            typesWhichRequireMissingFieldTraversal.add(fromTypeState.getSchema().getName());

        equalOrdinalMap.buildToOrdinalIdentityMapping();

        map.put(typeName, equalOrdinalMap);
        return equalOrdinalMap;
    }

    private DiffEqualityTypeMapper getTypeMapper(HollowTypeReadState fromState, HollowTypeReadState toState) {
        if(fromState instanceof HollowObjectTypeReadState)
            return new DiffEqualityObjectMapper(this, (HollowObjectTypeReadState) fromState, (HollowObjectTypeReadState) toState, oneToOne);
        if(listOrderingIsImportant && fromState instanceof HollowListTypeReadState)
            return new DiffEqualityOrderedListMapper(this, (HollowListTypeReadState) fromState, (HollowListTypeReadState) toState, oneToOne);
        if(fromState instanceof HollowCollectionTypeReadState)
            return new DiffEqualityCollectionMapper(this, (HollowCollectionTypeReadState) fromState, (HollowCollectionTypeReadState) toState, oneToOne);
        if(fromState instanceof HollowMapTypeReadState)
            return new DiffEqualityMapMapper(this, (HollowMapTypeReadState) fromState, (HollowMapTypeReadState) toState, oneToOne);

        throw new IllegalArgumentException("I don't know how to map equality for a " + fromState.getClass().getName());
    }

}

