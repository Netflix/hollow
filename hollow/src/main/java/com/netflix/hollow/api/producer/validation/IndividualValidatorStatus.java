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

import java.util.HashMap;
import java.util.Map;

import com.netflix.hollow.api.producer.HollowProducerListener.Status;
/**
 * *************************************
 * NOTE: Beta API subject to change.   *
 * *************************************
 * 
 * @author lkanchanapalli {@literal<lavanya65@yahoo.com>}
 * 
 * ValidationStatus has one instance per each validator that ran. 
 * For now ValidationStatus builds these and sets toString on validator as the message (where validators provide any details).
 * In next iteration this might be directly returned by validators.
 */
class IndividualValidatorStatus {
    private final long version;
    private final Status status;
    private final String message;
    private final Throwable throwable;
    private final Map<String, String> additionalInfo;

	private IndividualValidatorStatus(IndividualValidationStatusBuilder builder) {
		this.version = builder.version;
		this.status = builder.status;
		this.message = builder.message;
		this.throwable = builder.throwable;
		this.additionalInfo = builder.additionalInfo;
	}

	public IndividualValidatorStatus(long version, Status status, String message,Throwable throwable, Map<String,String> additionalInfo) {
		super();
		this.version = version;
		this.status = status;
		this.message = message;
		this.throwable = throwable;
		this.additionalInfo = additionalInfo;
	}
	public long getVersion() {
		return version;
	}
	public Status getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}

	public Throwable getThrowable() {
		return throwable;
	}
	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ValidatorStatus [version=");
		builder.append(version);
		builder.append(", status=");
		builder.append(status);
		if(message != null && !message.isEmpty()){
			builder.append(", message=");
			builder.append(message);
		}
		if(additionalInfo != null && !additionalInfo.isEmpty()){
			builder.append(", additionalInfo=");
			builder.append(additionalInfo);
		}
		builder.append("]");
		return builder.toString();
	}
	/**
	 * Creates builder to build {@link IndividualValidatorStatus}.
	 * @return created builder
	 */
	public static IndividualValidationStatusBuilder builder() {
		return new IndividualValidationStatusBuilder();
	}
	/**
	 * Builder to build {@link IndividualValidatorStatus}.
	 */
	public static final class IndividualValidationStatusBuilder {
		private long version;
		private Status status;
		private String message;
		private Throwable throwable;
		private Map<String, String> additionalInfo;

		private IndividualValidationStatusBuilder() {
			additionalInfo = new HashMap<>();
		}

		public IndividualValidationStatusBuilder withVersion(long version) {
			this.version = version;
			return this;
		}

		public IndividualValidationStatusBuilder withStatus(Status status) {
			this.status = status;
			return this;
		}

		public IndividualValidationStatusBuilder withMessage(String message) {
			this.message = message;
			return this;
		}

		public IndividualValidationStatusBuilder withThrowable(Throwable throwable) {
			this.throwable = throwable;
			return this;
		}

		public IndividualValidationStatusBuilder addAdditionalInfo(String key, String value) {
			this.additionalInfo.put(key, value);
			return this;
		}

		public IndividualValidatorStatus build() {
			return new IndividualValidatorStatus(this);
		}
	}
}
