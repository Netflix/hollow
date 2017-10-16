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

import javax.annotation.Generated;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.ReadState;
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
public class ValidationStatus {
    private final long version;
    private final Status status;
    private final Throwable throwable;
    private final HollowProducer.ReadState readState;
    private final List<ValidatorStatus> validatorStatusList;

	@Generated("SparkTools")
	private ValidationStatus(Builder builder) {
		this.version = builder.version;
		this.status = builder.status;
		this.throwable = builder.throwable;
		this.readState = builder.readState;
		this.validatorStatusList = builder.validatorStatusList;
	}
    
	public ValidationStatus(long version, Status status, Throwable throwable, ReadState readState,
			List<ValidatorStatus> validatorStatusList) {
		super();
		this.version = version;
		this.status = status;
		this.throwable = throwable;
		this.readState = readState;
		this.validatorStatusList = validatorStatusList;
	}

	public long getVersion() {
		return version;
	}

	public Status getStatus() {
		return status;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public HollowProducer.ReadState getReadState() {
		return readState;
	}

	public List<ValidatorStatus> getValidatorStatusList() {
		return validatorStatusList;
	}

	@Override
	public String toString() {
		return "ValidationStatus [version=" + version + ", status=" + status + ", throwable=" + throwable
				+ ", validatorStatusList=" + validatorStatusList + "]";
	}

	/**
	 * Creates builder to build {@link ValidationStatus}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link ValidationStatus}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private long version;
		private Status status;
		private Throwable throwable;
		private HollowProducer.ReadState readState;
		private List<ValidatorStatus> validatorStatusList;

		private Builder() {
			validatorStatusList = new ArrayList<>();
		}

		public Builder withVersion(long version) {
			this.version = version;
			return this;
		}

		public Builder success() {
			this.status = Status.SUCCESS;
			return this;
		}
		
		public Builder fail(Throwable th) {
			this.status = Status.FAIL;
			this.throwable = th;
			return this;
		}

		public Builder withThrowable(Throwable throwable) {
			this.throwable = throwable;
			return this;
		}

		public Builder withReadState(HollowProducer.ReadState readState) {
			this.readState = readState;
			return this;
		}

		public Builder addValidatorStatus(Throwable th, String message) {
			Status success = (th == null)? Status.SUCCESS: Status.FAIL;
			this.validatorStatusList.add(ValidatorStatus.builder().withStatus(success).withMessage(message).withThrowable(th).withVersion(version).build());
			return this;
		}

		public ValidationStatus build() {
			return new ValidationStatus(this);
		}
	}
    
}
