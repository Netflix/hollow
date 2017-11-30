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

import java.util.Arrays;
import java.util.Collection;

import com.netflix.hollow.api.producer.HollowProducer.Nameable;
import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.api.producer.HollowProducer.Validator;
import com.netflix.hollow.api.producer.HollowProducerListener.Status;
import com.netflix.hollow.api.producer.validation.SingleValidationStatus.SingleValidationStatusBuilder;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;

/**
 * 
 * @author lkanchanapalli {@literal<lavanya65@yahoo.com>}
 * 
 * This validator uses HollowPrimaryKey definition to find duplicates for a given type. 
 * Validator fails if no HollowPrimaryKey is defined for the given type or if given type has duplicate objects.
 * 
 * Note that if an object only has primary key fields then the duplicates are deduped by Hollow and will not result
 * in failing this validator. Ex: If Movie type has int id and String name and if primary key is also int id and String name
 * this validator will not fail as the two objects are deduped.
 *
 */
public class DuplicateDataDetectionValidator implements Nameable, Validator {
	private final String NAME = "DuplicateDataDetectionValidator";
	
	private final String dataTypeName;
	private final String[] fieldPathNames;
	// Status is used to track validation details. Helps surface information.
	private SingleValidationStatus lastRunStatus = null;
	
	/**
	 * @param dataTypeName for which this duplicate data detection is needed.
	 */
	public DuplicateDataDetectionValidator(String dataTypeName) {
		this.dataTypeName = dataTypeName;
		this.fieldPathNames = null;
	}
	
	/**
	 * 
	 * @param dataTypeName: for which this duplicate data detection is needed.
	 * @param fieldPathNames: field paths that defined a primary key
	 */
	public DuplicateDataDetectionValidator(String dataTypeName, String[] fieldPathNames) {
		this.dataTypeName = dataTypeName;
		this.fieldPathNames = fieldPathNames;
	}


	/* (non-Javadoc)
	 * @see com.netflix.hollow.api.producer.HollowProducer.Validator#validate(com.netflix.hollow.api.producer.HollowProducer.ReadState)
	 */
	@Override
	public void validate(ReadState readState) {
		SingleValidationStatusBuilder statusBuilder = initializeForValidation(readState);
		
		PrimaryKey primaryKey = getPrimaryKey(readState, statusBuilder);
		String fieldPaths = Arrays.toString(primaryKey.getFieldPaths());
		statusBuilder.addAdditionalInfo(FIELD_PATH_NAME , fieldPaths);
		
		Collection<Object[]> duplicateKeys = getDuplicateKeys(readState.getStateEngine(), primaryKey);
		if(duplicateKeys != null && !duplicateKeys.isEmpty()){
			handleEndValidation(statusBuilder, Status.FAIL, String.format(DUPLICATE_KEYS_FOUND_ERRRO_MSG_FORMAT, dataTypeName, fieldPaths, getDuplicateIDsString(duplicateKeys)));
		}
		handleEndValidation(statusBuilder, Status.SUCCESS, "");
	}

	private Collection<Object[]> getDuplicateKeys(HollowReadStateEngine stateEngine, PrimaryKey primaryKey) {
		HollowTypeReadState typeState = stateEngine.getTypeState(dataTypeName);
		HollowPrimaryKeyIndex hollowPrimaryKeyIndex = typeState.getListener(HollowPrimaryKeyIndex.class);
		if(hollowPrimaryKeyIndex == null)
			hollowPrimaryKeyIndex = new HollowPrimaryKeyIndex(stateEngine, primaryKey );
		return hollowPrimaryKeyIndex.getDuplicateKeys();
	}

	private SingleValidationStatusBuilder initializeForValidation(ReadState readState) {
		lastRunStatus = null;
		SingleValidationStatusBuilder statusBuilder = SingleValidationStatus.builder(getName()).addAdditionalInfo(DATA_TYPE_NAME, dataTypeName);
		return statusBuilder;
	}
	
	@Override
	public String toString(){
		if(lastRunStatus != null) {
			// For now only return message and additional data
			StringBuffer msg = new StringBuffer(lastRunStatus.getMessage());
			return  msg.append(lastRunStatus.getAdditionalInfo()).toString();
		}
		return("DuplicateDataDetectionValidator status for "+dataTypeName+" is null. This is unexpected. Please check validator definition.");
	}
	
	private String getDuplicateIDsString(Collection<Object[]> dupKeysCollection) {
		StringBuilder message = new StringBuilder();
        for (Object[] ids: dupKeysCollection) {
        	message.append(Arrays.toString(ids)).append(",");
        }
        return message.toString();
	}

	private PrimaryKey getPrimaryKey(ReadState readState, SingleValidationStatusBuilder statusBuilder) {
		PrimaryKey primaryKey = null;

		if (fieldPathNames == null) {
			HollowSchema schema = readState.getStateEngine().getSchema(dataTypeName);
			if (schema.getSchemaType() != (SchemaType.OBJECT))
				handleEndValidation(statusBuilder, Status.FAIL,  String.format(NOT_AN_OBJECT_ERROR_MSGR_FORMAT, dataTypeName));
			
			HollowObjectSchema oSchema = (HollowObjectSchema) schema;
			primaryKey = oSchema.getPrimaryKey();
		} else {
			primaryKey = new PrimaryKey(dataTypeName, fieldPathNames);
		}
		if (primaryKey == null)
			handleEndValidation(statusBuilder, Status.FAIL,  String.format(NO_PRIMARY_KEY_ERRRO_MSG_FORMAT, dataTypeName));

		return primaryKey;
	}
	
	private void handleEndValidation(SingleValidationStatusBuilder statusBuilder, Status status, String message) {
		statusBuilder.withMessage((message == null)?"":message);
		if(status == Status.FAIL){
			ValidationException ex = new ValidationException(message);
			this.lastRunStatus = statusBuilder.fail(ex).build();
			throw ex;
		}
		this.lastRunStatus = statusBuilder.success().build();
	}
	
	private static final String DUPLICATE_KEYS_FOUND_ERRRO_MSG_FORMAT = "Duplicate keys found for type %s. Primarykey in schema is %s. "
				+ "Duplicate IDs are: %s";
	private static final String NO_PRIMARY_KEY_ERRRO_MSG_FORMAT = "DuplicateDataDetectionValidator defined but unable to find primary key "
			+ "for data type %s. Please check schema definition.";
	private static final String NOT_AN_OBJECT_ERROR_MSGR_FORMAT = "DuplicateDataDetectionValidator is defined but schema type of %s "
				+ "is not Object. This validation cannot be done.";
	private static final String FIELD_PATH_NAME = "FieldPaths";
	private static final String DATA_TYPE_NAME = "Typename";

	@Override
	public String getName() {
		return NAME+"_"+dataTypeName;
	}
}
