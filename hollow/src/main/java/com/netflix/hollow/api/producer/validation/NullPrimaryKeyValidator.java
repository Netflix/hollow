package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowTypeWriteState;
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
 * A validator that fails if any records of a given type have null primary key values.
 * <p>
 * The primary key may be declared on a data model class using the {@link HollowPrimaryKey} annotation, may be
 * declared more directly using {@link PrimaryKey} with {@link HollowObjectSchema}, or declared explicitly when
 * instantiating this validator.
 */
public class NullPrimaryKeyValidator implements ValidatorListener{
    private static final String NAME = NullPrimaryKeyValidator.class.getName();
    private static final String NULL_PRIMARY_KEYS_FOUND_ERROR_MSG_FORMAT =
            "Null primary key fields found for type %s. Primary Key in schema is %s. "
                    + "Null records: [%s]";
    private static final String NO_PRIMARY_KEY_ERROR_MSG_FORMAT =
            "NullPrimaryKeyValidator defined but unable to find primary key for data type %s. "
                    + "Please check schema definition.";

    private static final String NO_SCHEMA_FOUND_MSG_FORMAT =
            "NullPrimaryKeyValidator defined for data type %s but schema not found. "
                    + "Please check that the HollowProducer is initialized with "
                    + "the data type's schema (see initializeDataModel)";
    private static final String NOT_AN_OBJECT_ERROR_MSG_FORMAT =
            "NullPrimaryKeyValidator is defined but schema type of %s is not Object. "
                    + "This validation cannot be done.";

    private static final String FIELD_PATH_NAME = "FieldPaths";
    private static final String DATA_TYPE_NAME = "Typename";

    private final String dataTypeName;
    private final String[] fieldPathNames;


    /**
     * Creates a validator to detect records with null primary keys of the type
     * that corresponds to the given data type class annotated with {@link HollowPrimaryKey}.
     *
     * @param dataType the data type class
     * @throws IllegalArgumentException if the data type class is not annotated with {@link HollowPrimaryKey}
     */
    public NullPrimaryKeyValidator(Class<?> dataType) {
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
     * Creates a validator to detect records with null primary keys of the type
     * that corresponds to the given data type class annotated with {@link HollowPrimaryKey}.
     * <p>
     * The validator will fail, when {@link #onValidate validating}, if a schema with a primary key definition does not
     * exist for the given data type name.
     *
     * @param dataTypeName the data type name
     */
    public NullPrimaryKeyValidator(String dataTypeName) {
        this.dataTypeName = Objects.requireNonNull(dataTypeName);
        this.fieldPathNames = null;
    }

    /**
     * Creates a validator to detect records with null primary keys of the type
     * that corresponds to the given data type class annotated with {@link HollowPrimaryKey}.
     * <p>
     * The validator will fail, when {@link #onValidate validating}, if a schema with a primary key definition does not
     * exist for the given data type name.
     *
     * @param dataTypeName the data type name
     * @param fieldPathNames the field paths defining the primary key
     */
    public NullPrimaryKeyValidator(String dataTypeName, String[] fieldPathNames) {
        this.dataTypeName = Objects.requireNonNull(dataTypeName);
        this.fieldPathNames = fieldPathNames.clone();
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
        if (fieldPathNames == null) {
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
        } else {
            primaryKey = new PrimaryKey(dataTypeName, fieldPathNames);
        }
        String fieldPaths = Arrays.toString(primaryKey.getFieldPaths());
        vrb.detail(FIELD_PATH_NAME, fieldPaths);

        Map<Integer, Object[]> ordinalToNullPkey = getNullPrimaryKeyValues(readState, primaryKey);
        if (!ordinalToNullPkey.isEmpty()) {
            return vrb.failed(String.format(NULL_PRIMARY_KEYS_FOUND_ERROR_MSG_FORMAT,
                    dataTypeName, fieldPaths,
                    nullKeysToString(ordinalToNullPkey)));
        }

        return vrb.passed("message");
    }


    private Map<Integer, Object[]> getNullPrimaryKeyValues(HollowProducer.ReadState readState, PrimaryKey primaryKey) {
        HollowPrimaryKeyValueDeriver primaryKeyValueDeriver = new HollowPrimaryKeyValueDeriver(primaryKey, readState.getStateEngine());
        HollowTypeReadState typeState = readState.getStateEngine().getTypeState(dataTypeName);
        BitSet ordinals = typeState.getPopulatedOrdinals();
        int ordinal = ordinals.nextSetBit(0);
        Map<Integer, Object[]> ordinalToNullPkey = new HashMap<>();
        while (ordinal != ORDINAL_NONE) {
            Object[] primaryKeyValues = primaryKeyValueDeriver.getRecordKey(ordinal);
            if (primaryKeyValues == null || Arrays.stream(primaryKeyValues).anyMatch(Objects::isNull)) {
                ordinalToNullPkey.put(ordinal, primaryKeyValues);
            }
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }
        return ordinalToNullPkey;
    }

    private String nullKeysToString(Map<Integer, Object[]> nullPrimaryKeyValues) {
        return nullPrimaryKeyValues.entrySet().stream()
                .map(entry -> {
                    return "(ordinal=" + entry.getKey() + ", key=" + Arrays.toString(entry.getValue()) + ")";
                })
                .collect(Collectors.joining(", "));
    }

    /**
     * Registers {@code NullPrimaryKeyValidator} validators with the given {@link HollowProducer producer} for
     * all object schema declared with a primary key.
     * <p>
     * This requires that the producer's data model has been initialized
     * (see {@link HollowProducer#initializeDataModel(Class[])} or a prior run cycle has implicitly initialized
     * the data model.
     * <p>
     * For each {@link HollowTypeWriteState write state} that has a {@link HollowObjectSchema object schema}
     * declared with a {@link PrimaryKey primary key} a {@code NullPrimaryKeyValidator} validator
     * is instantiated, with the primary key type name, and registered with the given producer (if a
     * {@code NullPrimaryKeyValidator} validator is not already registered for the same primary key type name).
     *
     * @param producer the producer
     * @apiNote This method registers a {@code NullPrimaryKeyValidator} validator with only the primary key type
     * name and not, in addition, the primary key fields.  This is to ensure, for the common case, duplicate listeners
     * are not registered by this method if listeners with the same type names were explicitly registered when
     * building the producer.
     * @see HollowProducer#initializeDataModel(Class[])
     */
    public static void addValidatorsForSchemaWithPrimaryKey(HollowProducer producer) {
                producer.getWriteEngine().getOrderedTypeStates().stream()
                .filter(ts -> ts.getSchema().getSchemaType() == HollowSchema.SchemaType.OBJECT)
                .map(ts -> (HollowObjectSchema) ts.getSchema())
                .filter(hos -> hos.getPrimaryKey() != null)
                .map(HollowObjectSchema::getPrimaryKey)
                .forEach(k -> producer.addListener(new NullPrimaryKeyValidator(k.getType())));
    }
}
