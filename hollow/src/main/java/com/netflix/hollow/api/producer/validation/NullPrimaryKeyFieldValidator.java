package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeMapper;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

/**
 * A validator that fails if any records of a given type have null primary key fields.
 * <p>
 * The primary key may be declared on a data model class using the {@link HollowPrimaryKey} annotation, may be
 * declared more directly using {@link PrimaryKey} with {@link HollowObjectSchema}, or declared explicitly when
 * instantiating this validator.
 */
public class NullPrimaryKeyFieldValidator implements ValidatorListener {
    private static final int MAX_DISPLAYED_NULL_KEYS = 100;
    private static final String NAME = NullPrimaryKeyFieldValidator.class.getName();
    private static final String NULL_PRIMARY_KEYS_FOUND_ERROR_MSG_FORMAT =
            "Null primary key fields found for type %s. Primary Key in schema is %s. "
                    + "Null records: [%s]";
    private static final String NO_PRIMARY_KEY_ERROR_MSG_FORMAT =
            "NullPrimaryKeyFieldValidator defined but unable to find primary key for data type %s. "
                    + "Please check schema definition.";

    private static final String NO_SCHEMA_FOUND_MSG_FORMAT =
            "NullPrimaryKeyFieldValidator defined for data type %s but schema not found. "
                    + "Please check that the HollowProducer is initialized with "
                    + "the data type's schema (see initializeDataModel)";
    private static final String NOT_AN_OBJECT_ERROR_MSG_FORMAT =
            "NullPrimaryKeyFieldValidator is defined but schema type of %s is not Object. "
                    + "This validation cannot be done.";

    private static final String FIELD_PATH_NAME = "FieldPaths";
    private static final String DATA_TYPE_NAME = "Typename";

    private final String dataTypeName;

    /**
     * Creates a validator to detect records with null primary key fields for the type
     * that corresponds to the given data type class annotated with {@link HollowPrimaryKey}.
     *
     * @param dataType the data type class
     * @throws IllegalArgumentException if the data type class is not annotated with {@link HollowPrimaryKey}
     */
    public NullPrimaryKeyFieldValidator(Class<?> dataType) {
        Objects.requireNonNull(dataType);

        if (!dataType.isAnnotationPresent(HollowPrimaryKey.class)) {
            throw new IllegalArgumentException("The data class " +
                    dataType.getName() +
                    " must be annotated with @HollowPrimaryKey");
        }

        this.dataTypeName = HollowTypeMapper.getDefaultTypeName(dataType);
    }

    /**
     * Creates a validator to detect records with null primary keys of the type
     * that corresponds to the given data type class annotated with {@link HollowPrimaryKey}.
     * <p>
     * The validator will fail, when {@link #onValidate validating}, if a schema with a
     * primary key definition does not exist for the given data type name.
     *
     * @param dataTypeName the data type name
     */
    public NullPrimaryKeyFieldValidator(String dataTypeName) {
        this.dataTypeName = Objects.requireNonNull(dataTypeName);
    }

    @Override
    public String getName() {
        return NAME + "_" + dataTypeName;
    }

    @Override
    public ValidationResult onValidate(HollowProducer.ReadState readState) {
        ValidationResult.ValidationResultBuilder vrb = ValidationResult.from(this);
        vrb.detail(DATA_TYPE_NAME, dataTypeName);

        PrimaryKey primaryKey;
        HollowSchema schema = readState.getStateEngine().getSchema(dataTypeName);
        if (schema == null) {
            return vrb.failed(String.format(NO_SCHEMA_FOUND_MSG_FORMAT, dataTypeName));
        }
        if (schema.getSchemaType() != HollowSchema.SchemaType.OBJECT) {
            return vrb.failed(String.format(NOT_AN_OBJECT_ERROR_MSG_FORMAT, dataTypeName));
        }

        HollowObjectSchema oSchema = (HollowObjectSchema) schema;
        primaryKey = oSchema.getPrimaryKey();
        if (primaryKey == null) {
            return vrb.failed(String.format(NO_PRIMARY_KEY_ERROR_MSG_FORMAT, dataTypeName));
        }

        String fieldPaths = Arrays.toString(primaryKey.getFieldPaths());
        vrb.detail(FIELD_PATH_NAME, fieldPaths);

        Map<Integer, Object[]> ordinalToNullPkey = getNullPrimaryKeyValues(readState, primaryKey);
        if (!ordinalToNullPkey.isEmpty()) {
            return vrb.failed(String.format(NULL_PRIMARY_KEYS_FOUND_ERROR_MSG_FORMAT,
                    dataTypeName, fieldPaths,
                    nullKeysToString(ordinalToNullPkey)));
        }

        return vrb.passed(getName() + "no records with null primary key fields");
    }

    private Map<Integer, Object[]> getNullPrimaryKeyValues(HollowProducer.ReadState readState, PrimaryKey primaryKey) {
        HollowReadStateEngine stateEngine = readState.getStateEngine();
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) stateEngine.getTypeState(dataTypeName);

        int[][] fieldPathIndexes = new int[primaryKey.getFieldPaths().length][];
        for (int i = 0; i < fieldPathIndexes.length; i++) {
            fieldPathIndexes[i] = primaryKey.getFieldPathIndex(stateEngine, i);
        }

        BitSet ordinals = typeState.getPopulatedOrdinals();
        int ordinal = ordinals.nextSetBit(0);
        Map<Integer, Object[]> ordinalToNullPkey = new HashMap<>();
        while (ordinal != ORDINAL_NONE) {
            Object[] primaryKeyValues = getPrimaryKeyValue(typeState, fieldPathIndexes, ordinal);
            if (Arrays.stream(primaryKeyValues).anyMatch(Objects::isNull)) {
                ordinalToNullPkey.put(ordinal, primaryKeyValues);
            }
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }
        return ordinalToNullPkey;
    }

    private Object[] getPrimaryKeyValue(HollowObjectTypeReadState typeState, int[][] fieldPathIndexes, int ordinal) {
        Object[] results = new Object[fieldPathIndexes.length];
        for (int i = 0; i < fieldPathIndexes.length; i++) {
            results[i] = getPrimaryKeyFieldValue(typeState, fieldPathIndexes[i], ordinal);
        }

        return results;
    }

    private Object getPrimaryKeyFieldValue(HollowObjectTypeReadState typeState, int[] fieldPathIndexes, int ordinal) {
        HollowObjectSchema schema = typeState.getSchema();
        int lastFieldPath = fieldPathIndexes.length - 1;
        for (int i = 0; i < lastFieldPath; i++) {
            if (ordinal == ORDINAL_NONE) {
                // The ordinal must have referenced a null record.
                return null;
            }
            ordinal = typeState.readOrdinal(ordinal, fieldPathIndexes[i]);
            typeState = (HollowObjectTypeReadState) schema.getReferencedTypeState(fieldPathIndexes[i]);
            schema = typeState.getSchema();
        }

        if (ordinal == ORDINAL_NONE) {
            // The ordinal must have referenced a record with a null value for this field.
            return null;
        }

        return HollowReadFieldUtils.fieldValueObject(typeState, ordinal, fieldPathIndexes[lastFieldPath]);
    }

    private String nullKeysToString(Map<Integer, Object[]> nullPrimaryKeyValues) {
        long totalCount = nullPrimaryKeyValues.size();
        String nullPrimaryKeysString = nullPrimaryKeyValues.entrySet().stream()
                .limit(MAX_DISPLAYED_NULL_KEYS)
                .map(entry -> {
                    return "(ordinal=" + entry.getKey() + ", key=" + Arrays.toString(entry.getValue()) + ")";
                })
                .collect(Collectors.joining(", "));

        if (totalCount > MAX_DISPLAYED_NULL_KEYS) {
            return String.format("%s ... (showing %d of %d null keys)",
                    nullPrimaryKeysString, MAX_DISPLAYED_NULL_KEYS, totalCount);
        }
        return nullPrimaryKeysString;
    }
}