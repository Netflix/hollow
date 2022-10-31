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
package com.netflix.hollow.core.read.dataaccess.proxy;

import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;

/**
 * A {@link HollowTypeDataAccess} which delegates all calls to another {@link HollowTypeDataAccess}
 * 
 * @see HollowProxyDataAccess
 */
public abstract class HollowTypeProxyDataAccess implements HollowTypeDataAccess {

    protected final HollowProxyDataAccess dataAccess;
    protected HollowTypeDataAccess currentDataAccess;

    public HollowTypeProxyDataAccess(HollowProxyDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void setCurrentDataAccess(HollowTypeDataAccess typeDataAccess) {
        this.currentDataAccess = typeDataAccess;
    }

    public HollowTypeDataAccess getCurrentDataAccess() {
        return currentDataAccess;
    }

    @Override
    public HollowDataAccess getDataAccess() {
        return dataAccess;
    }

    @Override
    public HollowTypeReadState getTypeState() {
        return currentDataAccess.getTypeState();
    }

    @Override
    public void setSamplingDirector(HollowSamplingDirector director) {
        currentDataAccess.setSamplingDirector(director);
    }
    
    @Override
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        currentDataAccess.setFieldSpecificSamplingDirector(fieldSpec, director);
    }
    
    @Override
    public void ignoreUpdateThreadForSampling(Thread t) {
        currentDataAccess.ignoreUpdateThreadForSampling(t);
    }

    @Override
    public HollowSampler getSampler() {
        return currentDataAccess.getSampler();
    }

}
