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
 * SingleValidationStatus has one instance per each validator that ran. 
 * For now ValidationStatus builds these and sets toString on validator as the message (where validators provide any details).
 * In next iteration this might be directly returned by validators.
 */
public class SingleValidationStatus {
    private final long version;
    private final Status status;
    private final String message;
    private final Throwable throwable;
    private final Map<String, String> additionalInfo;

	private SingleValidationStatus(SingleValidationStatusBuilder builder) {
		this.version = builder.version;
		this.status = builder.status;
		this.message = builder.message;
		this.throwable = builder.throwable;
		this.additionalInfo = builder.additionalInfo;
	}
    
	public SingleValidationStatus(long version, Status status, String message, Throwable throwable,
			Map<String, String> additionalInfo) {
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
		builder.append("IndividualValidatorStatus [version=");
		builder.append(version);
		builder.append(", status=");
		builder.append(status);
		builder.append(", message=");
		builder.append(message);
		builder.append(", throwable=");
		builder.append(throwable);
		builder.append(", additionalInfo=");
		builder.append(additionalInfo);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Creates builder to build {@link SingleValidationStatus}.
	 * @return created builder
	 */
	public static SingleValidationStatusBuilder builder(long version) {
		return new SingleValidationStatusBuilder(version);
	}

	/**
	 * Builder to build {@link SingleValidationStatus}.
	 */
	public static final class SingleValidationStatusBuilder {
		private long version;
		private Status status;
		private String message;
		private Throwable throwable;
		private Map<String, String> additionalInfo;

		private SingleValidationStatusBuilder(long version) {
			this.version = version;
		}


		public SingleValidationStatusBuilder withMessage(String message) {
			this.message = message;
			return this;
		}

		public SingleValidationStatusBuilder fail(Throwable th) {
			this.throwable = th;
			this.status = Status.FAIL;
			return this;
		}
		
		public SingleValidationStatusBuilder success() {
			this.status = Status.SUCCESS;
			return this;
		}
		
		public SingleValidationStatusBuilder withStatus(Status status) {
			this.status = status;
			return this;
		}
		
		public SingleValidationStatusBuilder addAdditionalInfo(String key, String value) {
			if(this.additionalInfo == null) 
				this.additionalInfo = new HashMap<>();
			this.additionalInfo.put(key, value);
			return this;
		}

		public SingleValidationStatus build() {
			return new SingleValidationStatus(this);
		}
	}
	
	
}
