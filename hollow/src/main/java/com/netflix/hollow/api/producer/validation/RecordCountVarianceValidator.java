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

import com.netflix.hollow.api.producer.HollowProducer.Nameable;
import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.api.producer.HollowProducer.Validator;
import com.netflix.hollow.api.producer.HollowProducerListener.Status;
import com.netflix.hollow.api.producer.validation.SingleValidationStatus.SingleValidationStatusBuilder;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;

/**
 * @author lkanchanapalli {@literal<lavanya65@yahoo.com>}
 *
 * Used to validate if the cardinality change in current cycle is with in the allowed percent for a given typeName.
 * Ex: 0% allowableVariancePercent ensures type cardinality does not vary at all for cycle to cycle. Ex: Number of state in United States.
 * 10% allowableVariancePercent: from previous cycle any addition or removal within 10% cardinality is valid. 
 * Anything more results in failure of validation.
 */
public class RecordCountVarianceValidator implements Nameable, Validator {
	private final String NAME = "RecordCountVarianceValidator";
	
	private final String typeName;
	private final float allowableVariancePercent;
	// status is used to capture details about validation. Helps surface information.
	private SingleValidationStatus status = null;
	
	/**
	 * 
	 * @param typeName
	 * @param allowableVariancePercent: Used to validate if the cardinality change in current cycle is with in the allowed percent.
	 * Ex: 0% allowableVariancePercent ensures type cardinality does not vary at all for cycle to cycle. Ex: Number of state in United States.
	 * 10% allowableVariancePercent: from previous cycle any addition or removal within 10% cardinality is valid. Anything more results in failure of validation.
	 */
	public RecordCountVarianceValidator(String typeName, float allowableVariancePercent) {
		this.typeName = typeName;
		if(allowableVariancePercent < 0)
			throw new IllegalArgumentException("RecordCountVarianceValidator for type "+typeName+": cannot have allowableVariancePercent less than 0. Value provided: "+allowableVariancePercent);
		this.allowableVariancePercent = allowableVariancePercent;
	}
	
	/* (non-Javadoc)
	 * @see com.netflix.hollow.api.producer.HollowProducer.Validator#validate(com.netflix.hollow.api.producer.HollowProducer.ReadState)
	 */
	@Override
	public void validate(ReadState readState) {
		SingleValidationStatusBuilder builder = initializeForValidation(readState);
		
		HollowTypeReadState typeState = readState.getStateEngine().getTypeState(typeName);
		int latestCardinality = typeState.getPopulatedOrdinals().cardinality();
		int previousCardinality = typeState.getPreviousOrdinals().cardinality();
		builder.addAdditionalInfo(LATEST_CARDINALITY_NAME, String.valueOf(latestCardinality));
		builder.addAdditionalInfo(PREVIOUS_CARDINALITY_NAME, String.valueOf(previousCardinality));
		
		if(previousCardinality  == 0){
			handleEndValidation(builder, Status.SKIP, String.format(ZERO_PREVIOUS_COUNT_WARN_MSG_FORMAT, typeName));
			return;
		}

		float actualChangePercent = getChangePercent(latestCardinality , previousCardinality );
		builder.addAdditionalInfo(ACTUAL_CHANGE_PERCENT_NAME, String.valueOf(actualChangePercent));
		
		if (Float.compare(actualChangePercent , allowableVariancePercent) > 0) {
			String message = String.format(FAILED_RECORD_COUNT_VALIDATION, typeName, actualChangePercent, allowableVariancePercent);
			handleEndValidation(builder, Status.FAIL, message);
		}
		handleEndValidation(builder, Status.SUCCESS, "");
	}
	
	@Override
	public String toString(){
		if(status != null) {
			// For now only return message and additional data
			StringBuffer msg = new StringBuffer(status.getMessage());
			return  msg.append(status.getAdditionalInfo()).toString();
		}
		return("RecordCountVarianceValidator status for "+typeName+" is null. This is unexpected. Please check validator definition.");
	}

	private SingleValidationStatusBuilder initializeForValidation(ReadState readState) {
		status = null;
		SingleValidationStatusBuilder builder = SingleValidationStatus.builder(getName());
		builder.addAdditionalInfo(ALLOWABLE_VARIANCE_PERCENT_NAME, String.valueOf(allowableVariancePercent));
		builder.addAdditionalInfo(DATA_TYPE_NAME, typeName);
		return builder;
	}

	float getChangePercent(int latestCardinality, int previousCardinality) {
		int diff = Math.abs(latestCardinality - previousCardinality);
		float changePercent = ((float)100.0* diff)/(float)previousCardinality;
		return changePercent;
	}
	
	private void handleEndValidation(SingleValidationStatusBuilder builder, Status status, String message){
		builder.withMessage((message == null)?"":message);
		if(Status.FAIL == status){
			ValidationException ex = new ValidationException(message);
			this.status = builder.fail(ex).build();
			throw ex;
		}
		this.status = builder.withStatus(status).build();
	}
	
	private static final String DATA_TYPE_NAME = "Typename";
	private static final String ALLOWABLE_VARIANCE_PERCENT_NAME = "AllowableVariancePercent";
	private static final String LATEST_CARDINALITY_NAME = "LatestRecordCount";
	private static final String PREVIOUS_CARDINALITY_NAME = "PreviousRecordCount";
	private static final String ACTUAL_CHANGE_PERCENT_NAME = "ActualChangePercent";
	private static final String ZERO_PREVIOUS_COUNT_WARN_MSG_FORMAT = "Previous record count is 0. Not running RecordCountVarianceValidator for type %s. "
																		+"This scenario is not expected except when starting a new namespace." ;
	private static final String FAILED_RECORD_COUNT_VALIDATION = "Record count validation for type %s has failed as actual change percent %s "
																	+ "is greater than allowed change percent %s.";

	@Override
	public String getName() {
		return NAME+"_"+typeName;
	}
}
	
