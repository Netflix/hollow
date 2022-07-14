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
package com.netflix.hollow.api.metrics;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import java.util.Collection;
import java.util.HashMap;

public abstract class HollowMetrics {

    private HashMap<String, Long> typeHeapFootprint = new HashMap<>();
    private HashMap<String, Integer> typePopulatedOrdinals = new HashMap<>();
    private long currentVersion;
    private long totalHeapFootprint = 0L;
    private int totalPopulatedOrdinals = 0;

    protected void update(long version) {
        setCurrentVersion(version);
    }

    protected void update(HollowReadStateEngine hollowReadStateEngine, long version) {
        setCurrentVersion(version);
        calculateTypeMetrics(hollowReadStateEngine);
    }

    /**
     * Calculates the memory heap footprint and populated ordinals per type and total
     * @param hollowReadStateEngine
     */
    void calculateTypeMetrics(HollowReadStateEngine hollowReadStateEngine) {
        Collection<HollowTypeReadState> typeStates = hollowReadStateEngine.getTypeStates();
        if(typeStates == null)
            return;

        totalHeapFootprint = 0L;
        totalPopulatedOrdinals = 0;
        for(HollowTypeReadState typeState : typeStates) {
            long heapCost = typeState.getApproximateHeapFootprintInBytes();
            totalHeapFootprint += heapCost;

            int populatedOrdinals = typeState.getPopulatedOrdinals().cardinality();
            totalPopulatedOrdinals += populatedOrdinals;
            String type = typeState.getSchema().getName();
            typeHeapFootprint.put(type, heapCost);
            typePopulatedOrdinals.put(type, populatedOrdinals);
        }
    }

    public HashMap<String, Long> getTypeHeapFootprint() {
        return typeHeapFootprint;
    }

    public HashMap<String, Integer> getTypePopulatedOrdinals() {
        return typePopulatedOrdinals;
    }

    public long getCurrentVersion() {
        return this.currentVersion;
    }

    public long getTotalHeapFootprint() {
        return this.totalHeapFootprint;
    }

    public long getTotalPopulatedOrdinals() {
        return this.totalPopulatedOrdinals;
    }

    public void setCurrentVersion(long version) {
        this.currentVersion = version;
    }
}
