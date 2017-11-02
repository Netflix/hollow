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
public class OverallValidationStatus {
    private final long version;
    private final Status status;
    private final Throwable throwable;
    private final HollowProducer.ReadState readState;
    private final List<IndividualValidatorStatus> validatorStatusList;

	private OverallValidationStatus(OverallValidationBuilder builder) {
		this.version = builder.version;
		this.status = builder.status;
		this.throwable = builder.throwable;
		this.readState = builder.readState;
		this.validatorStatusList = builder.validatorStatusList;
	}
    
	public OverallValidationStatus(long version, Status status, Throwable throwable, ReadState readState,
			List<IndividualValidatorStatus> validatorStatusList) {
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

	public List<IndividualValidatorStatus> getValidatorStatusList() {
		return validatorStatusList;
	}

	@Override
	public String toString() {
		return "ValidationStatus [version=" + version + ", status=" + status + ", throwable=" + throwable
				+ ", validatorStatusList=" + validatorStatusList + "]";
	}

	/**
	 * Creates builder to build {@link OverallValidationStatus}.
	 * @return created builder
	 */
	public static OverallValidationBuilder builder() {
		return new OverallValidationBuilder();
	}

	/**
	 * Builder to build {@link OverallValidationStatus}.
	 */
	public static final class OverallValidationBuilder {
		private long version;
		private Status status;
		private Throwable throwable;
		private HollowProducer.ReadState readState;
		private List<IndividualValidatorStatus> validatorStatusList;

		private OverallValidationBuilder() {
			validatorStatusList = new ArrayList<>();
		}

		public OverallValidationBuilder withVersion(long version) {
			this.version = version;
			return this;
		}

		public OverallValidationBuilder success() {
			this.status = Status.SUCCESS;
			return this;
		}
		
		public OverallValidationBuilder fail(Throwable th) {
			this.status = Status.FAIL;
			this.throwable = th;
			return this;
		}

		public OverallValidationBuilder withReadState(HollowProducer.ReadState readState) {
			this.readState = readState;
			return this;
		}

		public OverallValidationBuilder addIndividualValidatorStatus(Throwable th, String message) {
			Status success = (th == null)? Status.SUCCESS: Status.FAIL;
			this.validatorStatusList.add(IndividualValidatorStatus.builder().withStatus(success).withMessage(message).withThrowable(th).withVersion(version).build());
			if(status == Status.FAIL){
				this.status = Status.FAIL;
			}
			return this;
		}

		public OverallValidationStatus build() {
			return new OverallValidationStatus(this);
		}
	}
    
}
