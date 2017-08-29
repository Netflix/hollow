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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.api.producer.HollowProducer.Validator;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;

/**
 * 
 * @author lkanchanapalli
 *
 */
public class DuplicateDataDetectionValidator implements Validator {
	String dataTypeName;
	private String[] fieldPathNames;
	private final Logger log = Logger.getLogger(DuplicateDataDetectionValidator.class.getName());
	
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
		log.log(Level.INFO, "Running DuplicateDataDetectionValidator for type "+dataTypeName);
		PrimaryKey primaryKey = getPrimaryKey(readState);
		HollowPrimaryKeyIndex hollowPrimaryKeyIndex = new HollowPrimaryKeyIndex(readState.getStateEngine(), primaryKey);
		Collection<Object[]> duplicateKeys = hollowPrimaryKeyIndex.getDuplicateKeys();
		
		if(duplicateKeys != null && !duplicateKeys.isEmpty()){
			String duplicateIds = getDuplicateIDsString(duplicateKeys);
			String errorMsg = String.format("Duplicate keys found for type %s. Unique key is defined as %s. Duplicate IDs are: %s", dataTypeName, 
					Arrays.toString(primaryKey.getFieldPaths()), duplicateIds);
			throw new ValidationException(errorMsg);
		}
	}
	
	private String getDuplicateIDsString(Collection<Object[]> dupKeysCollection) {
		StringBuilder message = new StringBuilder();
        for (Object[] ids: dupKeysCollection) {
        	message.append(Arrays.toString(ids)).append(",");
        }
        return message.toString();
	}

	private PrimaryKey getPrimaryKey(ReadState readState) {
		PrimaryKey primaryKey = null;

		if (fieldPathNames == null) {
			HollowSchema schema = readState.getStateEngine().getSchema(dataTypeName);
			if (schema.getSchemaType() != (SchemaType.OBJECT))
				throw new ValidationException("Primary key validation is defined but schema type of "+ dataTypeName+" is not Object. This validation cannot be done.");
			HollowObjectSchema oSchema = (HollowObjectSchema) schema;
			primaryKey = oSchema.getPrimaryKey();
		} else {
			primaryKey = new PrimaryKey(dataTypeName, fieldPathNames);
		}
		if (primaryKey == null)
			throw new ValidationException(
					"Primary key validation defined but unable to find primary key for data type " + dataTypeName);

		return primaryKey;
	}

}
