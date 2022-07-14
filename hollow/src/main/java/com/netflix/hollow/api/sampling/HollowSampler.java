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
package com.netflix.hollow.api.sampling;

import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import java.util.Collection;

public interface HollowSampler {

    public void setSamplingDirector(HollowSamplingDirector director);

    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director);

    public void setUpdateThread(Thread t);

    public boolean hasSampleResults();

    public Collection<SampleResult> getSampleResults();

    public void reset();

}
