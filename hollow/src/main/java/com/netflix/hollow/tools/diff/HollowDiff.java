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

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

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
    private final EnumSet<FieldType> SINGLE_FIELD_SUPPORTED_TYPES = EnumSet.of(FieldType.INT, FieldType.LONG, FieldType.DOUBLE, FieldType.STRING, FieldType.FLOAT, FieldType.BOOLEAN);

    private final Logger log = Logger.getLogger(HollowDiff.class.getName());
    private final HollowReadStateEngine fromStateEngine;
    private final HollowReadStateEngine toStateEngine;

    private final DiffEqualityMapping equalityMapping;

    private final Map<String, HollowTypeDiff> typeDiffs = new LinkedHashMap<>();

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
        this(from, to, true, false);
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
        this(from, to, isAutoDiscoverTypeDiff, false);
    }

    /**
     * Instantiate a HollowDiff.
     * <p>
     * To calculate the diff, call calculateDiff().
     *
     * @param from the "from" state
     * @param to the "to" state
     * @param isAutoDiscoverTypeDiff If true, all OBJECT types with a defined PrimaryKey will be configured to be diffed.
     * @param isIncludeNonPrimaryKeyTypes If true, all OBJECT types without PrimaryKey will also be configured to be diffed.
     */
    public HollowDiff(HollowReadStateEngine from, HollowReadStateEngine to, boolean isAutoDiscoverTypeDiff, boolean isIncludeNonPrimaryKeyTypes) {
        this.fromStateEngine = from;
        this.toStateEngine = to;
        this.equalityMapping = new DiffEqualityMapping(from, to);

        if (isAutoDiscoverTypeDiff) { // Auto Discover TypeDiff from both from and to StateEngine
            List<HollowSchema> schemas = new ArrayList<>();
            schemas.addAll(fromStateEngine.getSchemas());
            schemas.addAll(toStateEngine.getSchemas());
            for (HollowSchema schema : schemas) {
                if (schema instanceof HollowObjectSchema) {
                    HollowObjectSchema objectSchema = ((HollowObjectSchema) schema);
                    PrimaryKey pKey = objectSchema.getPrimaryKey();
                    if (pKey==null && !isIncludeNonPrimaryKeyTypes) continue;

                    // Support basic Single Field Types
                    if (pKey==null && objectSchema.numFields()==1 && SINGLE_FIELD_SUPPORTED_TYPES.contains(objectSchema.getFieldType(0))) {
                        pKey = new PrimaryKey(schema.getName(), objectSchema.getFieldName(0));
                    }

                    addTypeDiff(schema.getName(), pKey==null? null : pKey.getFieldPaths());
                }
            }
        }
    }

    /**
     * Add a type to be included in the diff report
     *
     * @param type the type name
     * @param primaryKeyPaths the path(s) to the field(s) which comprise the type's primary key
     * @return the diff type
     */
    public HollowTypeDiff addTypeDiff(String type, String... primaryKeyPaths) {
        HollowTypeDiff typeDiff = new HollowTypeDiff(this, type, primaryKeyPaths);
        if(typeDiff.hasAnyData())
            typeDiffs.put(type, typeDiff);
        return typeDiff;
    }

    public List<HollowTypeDiff> getTypeDiffs() {
        return new ArrayList<>(typeDiffs.values());
    }

    /**
     * Retrieve a diff report for a specific type in order to inspect the calculated differences
     * @param type the type name
     * @return the diff type
     */
    public HollowTypeDiff getTypeDiff(String type) {
        return typeDiffs.get(type);
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

        log.info("PREPARED IN " + (endTime - startTime) + "ms");

        for(HollowTypeDiff typeDiff : typeDiffs.values()) {
            typeDiff.calculateDiffs();
        }
    }

    public DiffEqualityMapping getEqualityMapping() {
        return equalityMapping;
    }

    private void prepareForDiffCalculation() {
        SimultaneousExecutor executor = new SimultaneousExecutor(1 + typeDiffs.size(), getClass(), "prepare");

        executor.execute(() -> {
            for(HollowTypeDiff typeDiff : typeDiffs.values()) {
                equalityMapping.getEqualOrdinalMap(typeDiff.getTypeName());
            }
        });

        for(final HollowTypeDiff typeDiff : typeDiffs.values()) {
            executor.execute(typeDiff::calculateMatches);
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        equalityMapping.markPrepared();
    }

}