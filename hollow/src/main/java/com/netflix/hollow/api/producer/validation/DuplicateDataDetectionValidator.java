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
package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex.DuplicateKeyInfo;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A validator that fails if two or more records of the same type are present and they have the same primary key.
 * <p>
 * The primary key may be declared on a data model class using the {@link HollowPrimaryKey} annotation, may be
 * declared more directly using {@link PrimaryKey} with {@link HollowObjectSchema}, or declared explicitly when
 * instantiating this validator.
 *
 * @apiNote If all a record's fields are associated with a primary key (in other words, the record has no
 * fields that are not associated the primary key) then any duplicates added during the population
 * stage will be naturally de-duped resulting in just one record present.  Therefore a validator created for such a
 * record's type will never fail.
 */
public class DuplicateDataDetectionValidator implements ValidatorListener {
    private static final Logger LOG = Logger.getLogger(DuplicateDataDetectionValidator.class.getName());
    private static final int MAX_DISPLAYED_DUPLICATE_KEYS = 100;
    private static final String DUPLICATE_KEYS_FOUND_ERRRO_MSG_FORMAT =
            "Duplicate keys found for type %s. Primarykey in schema is %s. "
                    + "Duplicate IDs are: %s";
    private static final String DUPLICATE_KEYS_FOUND_DELTA_MSG_FORMAT =
            "Duplicate keys found for type %s (delta check - exact counts available on snapshot cycles). "
                    + "Primarykey in schema is %s. Duplicate keys: %s";
    private static final String NO_PRIMARY_KEY_ERROR_MSG_FORMAT =
            "DuplicateDataDetectionValidator defined but unable to find primary key "
                    + "for data type %s. Please check schema definition.";

    private static final String NO_SCHEMA_FOUND_MSG_FORMAT =
            "DuplicateDataDetectionValidator defined for data type %s but schema not found."
            + "Please check that the HollowProducer is initialized with the data type's schema "
            + "(see initializeDataModel)";
    private static final String NOT_AN_OBJECT_ERROR_MSG_FORMAT =
            "DuplicateDataDetectionValidator is defined but schema type of %s "
                    + "is not Object. This validation cannot be done.";

    private static final String FIELD_PATH_NAME = "FieldPaths";
    private static final String DATA_TYPE_NAME = "Typename";

    private static final String NAME = DuplicateDataDetectionValidator.class.getName();

    private final String dataTypeName;
    private final String[] fieldPathNames;

    /** Lagged index reflecting the last successful cycle; advanced via {@code endUpdate()} on pass. */
    private HollowPrimaryKeyIndex previousCycleIndex;

    /**
     * Creates a validator to detect duplicate records of the type that corresponds to the given data type class
     * annotated with {@link HollowPrimaryKey}.
     *
     * @param dataType the data type class
     * @throws IllegalArgumentException if the data type class is not annotated with {@link HollowPrimaryKey}
     */
    public DuplicateDataDetectionValidator(Class<?> dataType) {
        Objects.requireNonNull(dataType);

        if (!dataType.isAnnotationPresent(HollowPrimaryKey.class)) {
            throw new IllegalArgumentException("The data class " +
                    dataType.getName() +
                    " must be annotated with @HollowPrimaryKey");
        }

        this.dataTypeName = HollowTypeMapper.getDefaultTypeName(dataType);
        this.fieldPathNames = null;
    }

    /**
     * Creates a validator to detect duplicate records of the type that corresponds to the given data type name.
     * <p>
     * The validator will fail, when {@link #onValidate validating}, if a schema with a primary key definition does not
     * exist for the given data type name.
     *
     * @param dataTypeName the data type name
     */
    public DuplicateDataDetectionValidator(String dataTypeName) {
        this.dataTypeName = Objects.requireNonNull(dataTypeName);
        this.fieldPathNames = null;
    }

    /**
     * Creates a validator to detect duplicate records of the type that corresponds to the given data type name
     * with a primary key composed from the given field paths.
     * <p>
     * The validator will fail, when {@link #onValidate validating}, if a schema does not exist for the given data
     * type name or any field path is incorrectly specified.
     *
     * @param dataTypeName the data type name
     * @param fieldPathNames the field paths defining the primary key
     */
    public DuplicateDataDetectionValidator(String dataTypeName, String[] fieldPathNames) {
        this.dataTypeName = Objects.requireNonNull(dataTypeName);
        this.fieldPathNames = fieldPathNames.clone();
    }

    @Override
    public String getName() {
        return NAME + "_" + dataTypeName;
    }

    @Override
    public ValidationResult onValidate(ReadState readState) {
        ValidationResult.ValidationResultBuilder vrb = ValidationResult.from(this);
        vrb.detail(DATA_TYPE_NAME, dataTypeName);

        PrimaryKey primaryKey;
        if (fieldPathNames == null) {
            HollowSchema schema = readState.getStateEngine().getSchema(dataTypeName);
            if (schema == null) {
                return vrb.failed(String.format(NO_SCHEMA_FOUND_MSG_FORMAT, dataTypeName));
            }
            if (schema.getSchemaType() != SchemaType.OBJECT) {
                return vrb.failed(String.format(NOT_AN_OBJECT_ERROR_MSG_FORMAT, dataTypeName));
            }

            HollowObjectSchema oSchema = (HollowObjectSchema) schema;
            primaryKey = oSchema.getPrimaryKey();
            if (primaryKey == null) {
                return vrb.failed(String.format(NO_PRIMARY_KEY_ERROR_MSG_FORMAT, dataTypeName));
            }
        } else {
            primaryKey = new PrimaryKey(dataTypeName, fieldPathNames);
        }

        String fieldPaths = Arrays.toString(primaryKey.getFieldPaths());
        vrb.detail(FIELD_PATH_NAME, fieldPaths);

        HollowReadStateEngine stateEngine = readState.getStateEngine();
        ValidationResult result;

        if (previousCycleIndex == null) {
            // Snapshot path
            previousCycleIndex = new HollowPrimaryKeyIndex(stateEngine, primaryKey);
            LOG.info(String.format("Duplicate detection for type '%s': snapshot mode (full scan).", dataTypeName));

            Collection<DuplicateKeyInfo> duplicateKeys = previousCycleIndex.getDuplicateKeys(MAX_DISPLAYED_DUPLICATE_KEYS);
            if (!duplicateKeys.isEmpty()) {
                String message = String.format(DUPLICATE_KEYS_FOUND_ERRRO_MSG_FORMAT, dataTypeName, fieldPaths,
                        duplicateKeysToString(duplicateKeys));
                result = vrb.failed(message);
            } else {
                result = vrb.passed();
            }
        } else {
            // Delta path
            HollowTypeReadState typeState = stateEngine.getTypeState(dataTypeName);
            PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
            BitSet populatedOrdinals = listener.getPopulatedOrdinals();

            BitSet newOrdinals = new BitSet();
            newOrdinals.or(populatedOrdinals);
            newOrdinals.andNot(listener.getPreviousOrdinals());

            LOG.info(String.format("Duplicate detection for type '%s': delta mode (%d total, %d new ordinals).",
                    dataTypeName, populatedOrdinals.cardinality(), newOrdinals.cardinality()));

            if (!newOrdinals.isEmpty()) {
                List<Object[]> duplicateKeys = findDuplicateKeysInDelta(
                        previousCycleIndex, newOrdinals, populatedOrdinals);
                if (!duplicateKeys.isEmpty()) {
                    String message = String.format(DUPLICATE_KEYS_FOUND_DELTA_MSG_FORMAT, dataTypeName, fieldPaths,
                            deltaDuplicateKeysToString(duplicateKeys));
                    result = vrb.failed(message);
                } else {
                    result = vrb.passed();
                }
            } else {
                result = vrb.passed();
            }
        }

        // Advance index only on pass — failed cycles don't change published state
        if (result.isPassed()) {
            previousCycleIndex.endUpdate();
        }

        return result;
    }

    /**
     * Delta-aware duplicate detection using a lagged index. Detects new-vs-new
     * and new-vs-existing duplicates among the given new ordinals.
     *
     * @param laggedIndex an index whose hash table reflects the previous cycle's state
     * @param newOrdinals ordinals added in the current delta
     * @param populatedOrdinals all currently populated ordinals
     * @return duplicate keys found among new ordinals
     */
    List<Object[]> findDuplicateKeysInDelta(
            HollowPrimaryKeyIndex laggedIndex, BitSet newOrdinals, BitSet populatedOrdinals) {
        // Maps each key to its first ordinal, or -1 if already reported as duplicate
        Map<List<Object>, Integer> seen = new HashMap<>();
        List<Object[]> duplicateKeys = new ArrayList<>();

        int ordinal = newOrdinals.nextSetBit(0);
        while (ordinal != -1 && duplicateKeys.size() < MAX_DISPLAYED_DUPLICATE_KEYS) {
            Object[] key = laggedIndex.getRecordKey(ordinal);
            List<Object> keyList = Arrays.asList(key);

            Integer existingOrdinal = seen.get(keyList);
            if (existingOrdinal != null) {
                if (existingOrdinal >= 0) {
                    duplicateKeys.add(key);
                    seen.put(keyList, -1);
                }
            } else {
                seen.put(keyList, ordinal);
            }
            ordinal = newOrdinals.nextSetBit(ordinal + 1);
        }

        // Check new-vs-existing: matched ordinal must still be populated (not removed in this delta)
        for (Map.Entry<List<Object>, Integer> entry : seen.entrySet()) {
            if (duplicateKeys.size() >= MAX_DISPLAYED_DUPLICATE_KEYS) break;
            int newOrdinal = entry.getValue();
            if (newOrdinal < 0) continue;

            Object[] key = entry.getKey().toArray();
            int matched = laggedIndex.getMatchingOrdinal(key);
            if (matched != -1 && populatedOrdinals.get(matched)) {
                duplicateKeys.add(key);
            }
        }

        return duplicateKeys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DuplicateDataDetectionValidator that = (DuplicateDataDetectionValidator) o;
        return dataTypeName.equals(that.dataTypeName) &&
                Arrays.equals(fieldPathNames, that.fieldPathNames);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(dataTypeName);
        result = 31 * result + Arrays.hashCode(fieldPathNames);
        return result;
    }

    private String duplicateKeysToString(Collection<DuplicateKeyInfo> duplicateKeys) {
        long totalUniqueKeys = duplicateKeys.size();
        long totalAffectedRecords = duplicateKeys.stream()
                .mapToLong(DuplicateKeyInfo::getCount)
                .sum();

        String duplicateKeysString = duplicateKeys.stream()
                .map(DuplicateKeyInfo::toString)
                .collect(Collectors.joining(", "));

        return String.format("%s ... (%d distinct keys that each have duplicate records affecting %d records; showing at most %d keys)",
                duplicateKeysString, totalUniqueKeys, totalAffectedRecords, MAX_DISPLAYED_DUPLICATE_KEYS);
    }

    private String deltaDuplicateKeysToString(List<Object[]> duplicateKeys) {
        String keysString = duplicateKeys.stream()
                .map(Arrays::toString)
                .collect(Collectors.joining(", "));

        return String.format("%s ... (%d distinct duplicate keys detected; showing at most %d keys)",
                keysString, duplicateKeys.size(), MAX_DISPLAYED_DUPLICATE_KEYS);
    }

    /**
     * Registers {@code DuplicateDataDetectionValidator} validators with the given {@link HollowProducer producer} for
     * all object schema declared with a primary key.
     * <p>
     * This requires that the producer's data model has been initialized
     * (see {@link HollowProducer#initializeDataModel(Class[])} or a prior run cycle has implicitly initialized
     * the data model.
     * <p>
     * For each {@link HollowTypeWriteState write state} that has a {@link HollowObjectSchema object schema}
     * declared with a {@link PrimaryKey primary key} a {@code DuplicateDataDetectionValidator} validator
     * is instantiated, with the primary key type name, and registered with the given producer (if a
     * {@code DuplicateDataDetectionValidator} validator is not already registered for the same primary key type name).
     *
     * @param producer the producer
     * @apiNote This method registers a {@code DuplicateDataDetectionValidator} validator with only the primary key type
     * name and not, in addition, the primary key fields.  This is to ensure, for the common case, duplicate listeners
     * are not registered by this method if listeners with the same type names were explicitly registered when
     * building the producer.
     * @see HollowProducer#initializeDataModel(Class[])
     */
    public static void addValidatorsForSchemaWithPrimaryKey(HollowProducer producer) {
        producer.getWriteEngine().getOrderedTypeStates().stream()
                .filter(ts -> ts.getSchema().getSchemaType() == SchemaType.OBJECT)
                .map(ts -> (HollowObjectSchema) ts.getSchema())
                .filter(hos -> hos.getPrimaryKey() != null)
                .map(HollowObjectSchema::getPrimaryKey)
                .forEach(k -> producer.addListener(new DuplicateDataDetectionValidator(k.getType())));
    }
}
