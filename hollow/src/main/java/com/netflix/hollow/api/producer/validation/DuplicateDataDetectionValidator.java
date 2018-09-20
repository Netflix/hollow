/*
 *
 *  Copyright 2017 Netflix, Inc.
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

import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This validator uses HollowPrimaryKey definition to find duplicates for a given type.
 * Validator fails if no HollowPrimaryKey is defined for the given type or if given type has duplicate objects.
 * <p>
 * Note that if an object only has primary key fields then the duplicates are deduped by Hollow and will not result
 * in failing this validator. Ex: If Movie type has int id and String name and if primary key is also int id and String
 * name this validator will not fail as the two objects are deduped.
 *
 * @author lkanchanapalli {@literal<lavanya65@yahoo.com>}
 */
public class DuplicateDataDetectionValidator implements ValidatorListener {
    private static final String DUPLICATE_KEYS_FOUND_ERRRO_MSG_FORMAT =
            "Duplicate keys found for type %s. Primarykey in schema is %s. "
                    + "Duplicate IDs are: %s";
    private static final String NO_PRIMARY_KEY_ERRRO_MSG_FORMAT =
            "DuplicateDataDetectionValidator defined but unable to find primary key "
                    + "for data type %s. Please check schema definition.";
    private static final String NOT_AN_OBJECT_ERROR_MSGR_FORMAT =
            "DuplicateDataDetectionValidator is defined but schema type of %s "
                    + "is not Object. This validation cannot be done.";

    private static final String FIELD_PATH_NAME = "FieldPaths";
    private static final String DATA_TYPE_NAME = "Typename";

    private static final String NAME = DuplicateDataDetectionValidator.class.getName();

    private final String dataTypeName;
    private final String[] fieldPathNames;

    /**
     * @param dataTypeName for which this duplicate data detection is needed.
     */
    public DuplicateDataDetectionValidator(String dataTypeName) {
        this.dataTypeName = Objects.requireNonNull(dataTypeName);
        this.fieldPathNames = null;
    }

    /**
     * @param dataTypeName: for which this duplicate data detection is needed.
     * @param fieldPathNames: field paths that defined a primary key
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

        PrimaryKey primaryKey = null;
        if (fieldPathNames == null) {
            HollowSchema schema = readState.getStateEngine().getSchema(dataTypeName);
            if (schema.getSchemaType() != (SchemaType.OBJECT)) {
                return vrb.failed(String.format(NOT_AN_OBJECT_ERROR_MSGR_FORMAT, dataTypeName));
            }

            HollowObjectSchema oSchema = (HollowObjectSchema) schema;
            primaryKey = oSchema.getPrimaryKey();
            if (primaryKey == null) {
                return vrb.failed(String.format(NO_PRIMARY_KEY_ERRRO_MSG_FORMAT, dataTypeName));
            }
        } else {
            primaryKey = new PrimaryKey(dataTypeName, fieldPathNames);
        }

        String fieldPaths = Arrays.toString(primaryKey.getFieldPaths());
        vrb.detail(FIELD_PATH_NAME, fieldPaths);

        Collection<Object[]> duplicateKeys = getDuplicateKeys(readState.getStateEngine(), primaryKey);
        if (!duplicateKeys.isEmpty()) {
            String message = String.format(DUPLICATE_KEYS_FOUND_ERRRO_MSG_FORMAT, dataTypeName, fieldPaths,
                    duplicateKeysToString(duplicateKeys));
            return vrb.failed(message);
        }

        return vrb.passed();
    }

    private Collection<Object[]> getDuplicateKeys(HollowReadStateEngine stateEngine, PrimaryKey primaryKey) {
        HollowTypeReadState typeState = stateEngine.getTypeState(dataTypeName);
        HollowPrimaryKeyIndex hollowPrimaryKeyIndex = typeState.getListener(HollowPrimaryKeyIndex.class);
        if (hollowPrimaryKeyIndex == null) {
            hollowPrimaryKeyIndex = new HollowPrimaryKeyIndex(stateEngine, primaryKey);
        }
        return hollowPrimaryKeyIndex.getDuplicateKeys();
    }

    private String duplicateKeysToString(Collection<Object[]> duplicateKeys) {
        return duplicateKeys.stream().map(Arrays::toString).collect(Collectors.joining(","));
    }
}
