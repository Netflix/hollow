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

import java.util.ArrayList;
import java.util.List;

import com.netflix.hollow.api.producer.HollowProducerListener.Status;
/**
 * *************************************
 * NOTE: Beta API subject to change.   *
 * *************************************
 * @author lkanchanapalli {@literal<lavanya65@yahoo.com>}
 * 
 * Validation listener uses this to provide visibility into validations that ran.
 * This aggregates information across multiple validators per run.
 *
 */
public class AllValidationStatus {
    private final Status status;
    private final List<SingleValidationStatus> validationStatusList;

	private AllValidationStatus(AllValidationStatusBuilder builder) {
		this.status = builder.status;
		this.validationStatusList = builder.validationStatusList;
	}

	public AllValidationStatus(Status status, List<SingleValidationStatus> validatorStatusList) {
		this.status = status;
		this.validationStatusList = validatorStatusList;
	}

	public Status getStatus() {
		return status;
	}

	public List<SingleValidationStatus> getValidationStatusList() {
		return validationStatusList;
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OverallValidationStatus [");
		sb.append(" status=");
		sb.append(status);
		sb.append(", validationStatusList=");
		sb.append(validationStatusList);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Creates builder to build {@link AllValidationStatus}.
	 * @return created builder
	 */
	public static AllValidationStatusBuilder builder() {
		return new AllValidationStatusBuilder();
	}

	/**
	 * Builder to build {@link AllValidationStatus}.
	 */
	public static final class AllValidationStatusBuilder {
		private Status status;
		private List<SingleValidationStatus> validationStatusList = new ArrayList<>();

		private AllValidationStatusBuilder() {
		}
		
		public void addSingelValidationStatus(SingleValidationStatus validationStatus) {
			validationStatusList.add(validationStatus);
		}

		public AllValidationStatusBuilder fail() {
			this.status = Status.FAIL;
			return this;
		}
		
		public AllValidationStatusBuilder success() {
			this.status = Status.SUCCESS;
			return this;
		}
		public AllValidationStatus build() {
			return new AllValidationStatus(this);
		}

	}
    
}
