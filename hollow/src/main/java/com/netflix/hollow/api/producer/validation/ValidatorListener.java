/*
 *
 *  Copyright 2018 Netflix, Inc.
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

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.listener.HollowProducerEventListener;

/**
 * A validator of {@link com.netflix.hollow.api.producer.HollowProducer.ReadState read state}.  This type is a
 * listener of a validator event associated with the producer validator stage.
 * <p>
 * A validator instance may be registered when building a {@link HollowProducer producer}
 * (see {@link HollowProducer.Builder#withValidator(ValidatorListener)}} or by registering on the producer itself
 * (see {@link HollowProducer#addListener(HollowProducerEventListener)}.
 */
public interface ValidatorListener extends HollowProducerEventListener {
    /**
     * Gets the name of the validator.
     *
     * @return the name
     */
    String getName();

    /**
     * A receiver of a validation event.  Called when validation is to be performed on read state.
     * <p>
     * If a {@code RuntimeException} is thrown by the validator then validation has failed with an unexpected error.
     * A {@link ValidationResult result} will be built from this validator as if by a call to
     * {@link ValidationResult.ValidationResultBuilder#error(Throwable)} with the {@code RuntimeException} as the
     * argument.  The validator is considered to have returned that result.
     *
     * @param readState the read state.
     * @return the validation result
     */
    ValidationResult onValidate(HollowProducer.ReadState readState);
}
