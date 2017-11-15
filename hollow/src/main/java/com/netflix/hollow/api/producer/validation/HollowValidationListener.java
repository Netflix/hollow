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

import java.util.EventListener;
import java.util.concurrent.TimeUnit;

import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
/**
 * *************************************
 * NOTE: Beta API subject to change.   *
 * *************************************
 * 
 * @author lkanchanapalli {@literal<lavanya65@yahoo.com>}
 * 
 * Listener provides updates and more visibility into validation run. 
 * For now the visibility depends on toString of each validator. 
 * In next iteration validators provided more structured data.
 *
 */
public interface HollowValidationListener extends EventListener{
	

    /**
     * Called when the {@code HollowProducer} has begun validating the new data state produced this cycle.
     *
     * @param version Version to be validated
     */
    public void onValidationStart(long version);

    /**
     * Called after the validation stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the newly published data state is considered valid.
     *
     * @param status CycleStatus of the publish stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     *   when the publish was successful; @{code FAIL} otherwise.
     * @param elapsed duration of the validation stage in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    public void onValidationComplete(AllValidationStatus status, long elapsed, TimeUnit unit);
}
