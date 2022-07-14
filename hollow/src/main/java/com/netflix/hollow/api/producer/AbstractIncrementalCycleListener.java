/*
 *  Copyright 2016-2019 Netflix, Inc.
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
package com.netflix.hollow.api.producer;

import java.util.concurrent.TimeUnit;

/**
 * Beta API subject to change.
 * @deprecated see {@link com.netflix.hollow.api.producer.listener.IncrementalPopulateListener}
 * @see com.netflix.hollow.api.producer.listener.IncrementalPopulateListener
 */
@Deprecated
public class AbstractIncrementalCycleListener implements IncrementalCycleListener {
    @Override
    public void onCycleComplete(IncrementalCycleStatus status, long elapsed, TimeUnit unit) {
    }

    @Override
    public void onCycleFail(IncrementalCycleStatus status, long elapsed, TimeUnit unit) {
    }
}
