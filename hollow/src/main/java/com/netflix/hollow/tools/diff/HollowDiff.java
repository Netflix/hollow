/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaSorter;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Calculate a detailed accounting for the differences between two data states.
 * <p>
 * The differences between two states are broken down by specified types.  Records for each evaluated type are matched based on primary keys.
 * The matched records are traversed in tandem to determine which individual fields/branches in their hierarchies differ.
 * The difference between matched records is measured per field in the hierarchy as an integer value indicating the number
 * of unmatched values in either the <i>from</i> or the <i>to</i> state.
 * <p>
 * Unmatched records are accounted for separately -- for the purposes of the diff, it is sufficient to mark these records as unmatched.
 *
 */
public class HollowDiff {

    private final HollowReadStateEngine fromStateEngine;
    private final HollowReadStateEngine toStateEngine;

    private final DiffEqualityMapping equalityMapping;

    private final List<HollowTypeDiff> typeDiffs;

    /**
     * Instantiate a HollowDiff.  By default, all OBJECT types with a defined PrimaryKey will be
     * configured to be diffed.
     * <p>
     * To calculate the diff, call calculateDiff().
     * 
     * @param from the "from" state
     * @param to the "to" state 
     */
    public HollowDiff(HollowReadStateEngine from, HollowReadStateEngine to) {
        this(from, to, true);
    }

    /**
     * Instantiate a HollowDiff.  
     * <p>
     * To calculate the diff, call calculateDiff().
     * 
     * @param from the "from" state
     * @param to the "to" state
     * @param isAutoDiscoverTypeDiff If true, all OBJECT types with a defined PrimaryKey will be configured to be diffed.
     */
    public HollowDiff(HollowReadStateEngine from, HollowReadStateEngine to, boolean isAutoDiscoverTypeDiff) {
        this.fromStateEngine = from;
        this.toStateEngine = to;
        this.equalityMapping = new DiffEqualityMapping(from, to);
        this.typeDiffs = new ArrayList<HollowTypeDiff>();

        if (isAutoDiscoverTypeDiff) {
            for(HollowSchema schema : from.getSchemas()) {
                if (schema instanceof HollowObjectSchema) {
                    PrimaryKey pKey = ((HollowObjectSchema)schema).getPrimaryKey();
                    if (pKey == null) continue;

                    addTypeDiff(schema.getName(), pKey.getFieldPaths());
                }
            }
        }
    }

    /**
     * Add a type to be included in the diff report
     *
     * @param type the type name
     * @param primaryKeyPaths the path(s) to the field(s) which comprise the type's primary key
     * @return
     */
    public HollowTypeDiff addTypeDiff(String type, String... primaryKeyPaths) {
        HollowTypeDiff typeDiff = new HollowTypeDiff(this, type, primaryKeyPaths);
        if(typeDiff.hasAnyData())
            typeDiffs.add(typeDiff);
        return typeDiff;
    }

    public List<HollowTypeDiff> getTypeDiffs() {
        return typeDiffs;
    }

    /**
     * Retrieve a diff report for a specific type in order to inspect the calculated differences
     * @param type the type name
     * @return
     */
    public HollowTypeDiff getTypeDiff(String type) {
        for(HollowTypeDiff typeDiff : typeDiffs) {
            if(typeDiff.getTypeName().equals(type))
                return typeDiff;
        }
        return null;
    }

    public HollowReadStateEngine getFromStateEngine() {
        return fromStateEngine;
    }

    public HollowReadStateEngine getToStateEngine() {
        return toStateEngine;
    }

    /**
     * Run the diff
     */
    public void calculateDiffs() {
        long startTime = System.currentTimeMillis();

        prepareForDiffCalculation();

        long endTime = System.currentTimeMillis();

        System.out.println("PREPARED IN " + (endTime - startTime) + "ms");

        for(HollowTypeDiff typeDiff : typeDiffs) {
            typeDiff.calculateDiffs();
        }
    }

    public DiffEqualityMapping getEqualityMapping() {
        return equalityMapping;
    }

    private void prepareForDiffCalculation() {
        SimultaneousExecutor executor = new SimultaneousExecutor(1 + typeDiffs.size(), "hollow-diff-prepare");

        Collections.sort(typeDiffs, new Comparator<HollowTypeDiff>() {
            @Override
            public int compare(HollowTypeDiff o1, HollowTypeDiff o2) {
                if(HollowSchemaSorter.typeIsTransitivelyDependent(o1.getFromTypeState().getStateEngine(), o1.getFromTypeState().getSchema().getName(), o2.getFromTypeState().getSchema().getName())) {
                    return 1;
                } else if(HollowSchemaSorter.typeIsTransitivelyDependent(o1.getFromTypeState().getStateEngine(), o2.getFromTypeState().getSchema().getName(), o1.getFromTypeState().getSchema().getName())) {
                    return -1;
                }
                return 0;
            }
        });
        
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for(HollowTypeDiff typeDiff : typeDiffs) {
                    equalityMapping.getEqualOrdinalMap(typeDiff.getTypeName());
                }
            }
        });

        for(final HollowTypeDiff typeDiff : typeDiffs) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    typeDiff.calculateMatches();
                }
            });
        }

        executor.awaitUninterruptibly();
    }

}
