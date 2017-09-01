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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.api.producer.HollowProducer.Validator;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;

/**
 * @author lkanchanapalli
 *
 */
public class RecordCountVarianceValidator implements Validator {
	private final String typeName;
	private final float allowableVariancePercent;
	private final Logger log = Logger.getLogger(RecordCountVarianceValidator.class.getName());
	
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
		log.log(Level.INFO, "Running RecordCountVarianceValidator for type "+typeName);
		HollowTypeReadState typeState = readState.getStateEngine().getTypeState(typeName);
		int latestCardinality = typeState.getPopulatedOrdinals().cardinality();
		int previousCardinality = typeState.getPreviousOrdinals().cardinality();
		
		// TODO: log message indicating previous state is 0. Can happen for new name space. And also can happen 
		// when a type gets to zero count then the validation will be skipped.  
		if(previousCardinality == 0){
			log.log(Level.WARNING, "Previoud record count is 0. Not running RecordCountVarianceValidator for type "+typeName
					+". This scenario is not expected except when starting a new namespace.");
			return;
		}

		float actualChangePercent = getChangePercent(latestCardinality, previousCardinality);
		if (Float.compare(actualChangePercent, allowableVariancePercent) > 0) {
			throw new ValidationException("RecordCountVarianceValidator for type " + typeName
					+ " failed. Actual variance: " + actualChangePercent + "%; Allowed variance: "
					+ allowableVariancePercent + "%;  previous cycle record count: " + previousCardinality
							+ "; current cycle record count: " + latestCardinality);
					
		}
	}

	float getChangePercent(int latestCardinality, int previousCardinality) {
		int diff = Math.abs(latestCardinality - previousCardinality);
		float changePercent = ((float)100.0* diff)/(float)previousCardinality;
		return changePercent;
	}
}
	
