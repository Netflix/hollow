/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.disabled.HollowDisabledDataAccess;
import com.netflix.hollow.core.read.dataaccess.disabled.HollowListDisabledDataAccess;
import com.netflix.hollow.core.read.dataaccess.disabled.HollowMapDisabledDataAccess;
import com.netflix.hollow.core.read.dataaccess.disabled.HollowObjectDisabledDataAccess;
import com.netflix.hollow.core.read.dataaccess.disabled.HollowSetDisabledDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.tools.history.HollowHistoricalStateDataAccess;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A HollowProxyDataAccess delegates all calls to another {@link HollowDataAccess}.
 * <p>
 * This is useful when a {@link com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevityConfig} calls for the object longevity feature to be enabled.
 * In this case, when a state transition occurs, all existing objects backed by the latest {@link HollowReadStateEngine} 
 * will need to be backed by a {@link HollowHistoricalStateDataAccess}. 
 *   
 */
public class HollowProxyDataAccess implements HollowDataAccess {

    private HollowDataAccess currentDataAccess;
    private final ConcurrentHashMap<String, HollowTypeProxyDataAccess> typeDataAccessMap;

    public HollowProxyDataAccess() {
        this.typeDataAccessMap = new ConcurrentHashMap<String, HollowTypeProxyDataAccess>();
    }

    public void setDataAccess(HollowDataAccess currentDataAccess) {
        this.currentDataAccess = currentDataAccess;
        for(String type : currentDataAccess.getAllTypes()) {
            HollowTypeDataAccess typeDataAccess = currentDataAccess.getTypeDataAccess(type);
            HollowTypeProxyDataAccess proxyDataAccess = typeDataAccessMap.get(type);

            if(proxyDataAccess == null) {
                if(typeDataAccess instanceof HollowObjectTypeDataAccess) {
                    proxyDataAccess = new HollowObjectProxyDataAccess(this);
                } else if(typeDataAccess instanceof HollowListTypeDataAccess) {
                    proxyDataAccess = new HollowListProxyDataAccess(this);
                } else if(typeDataAccess instanceof HollowSetTypeDataAccess) {
                    proxyDataAccess = new HollowSetProxyDataAccess(this);
                } else if(typeDataAccess instanceof HollowMapTypeDataAccess) {
                    proxyDataAccess = new HollowMapProxyDataAccess(this);
                }

                typeDataAccessMap.put(type, proxyDataAccess);
            }

            proxyDataAccess.setCurrentDataAccess(typeDataAccess);
        }
    }

    public void disableDataAccess() {
        this.currentDataAccess = HollowDisabledDataAccess.INSTANCE;
        for(Map.Entry<String, HollowTypeProxyDataAccess> entry : typeDataAccessMap.entrySet()) {
            HollowTypeProxyDataAccess proxy = entry.getValue();
            if(proxy instanceof HollowObjectProxyDataAccess) {
                proxy.setCurrentDataAccess(HollowObjectDisabledDataAccess.INSTANCE);
            } else if(proxy instanceof HollowListProxyDataAccess) {
                proxy.setCurrentDataAccess(HollowListDisabledDataAccess.INSTANCE);
            } else if(proxy instanceof HollowSetProxyDataAccess) {
                proxy.setCurrentDataAccess(HollowSetDisabledDataAccess.INSTANCE);
            } else if(proxy instanceof HollowMapProxyDataAccess) {
                proxy.setCurrentDataAccess(HollowMapDisabledDataAccess.INSTANCE);
            }

        }
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String typeName) {
        return typeDataAccessMap.get(typeName);
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String typeName, int ordinal) {
        return typeDataAccessMap.get(typeName);
    }

    @Override
    public HollowObjectHashCodeFinder getHashCodeFinder() {
        return currentDataAccess.getHashCodeFinder();
    }

    @Override
    public MissingDataHandler getMissingDataHandler() {
        return currentDataAccess.getMissingDataHandler();
    }

    @Override
    public Collection<String> getAllTypes() {
        return typeDataAccessMap.keySet();
    }

    @Override
    public List<HollowSchema> getSchemas() {
        return currentDataAccess.getSchemas();
    }

    @Override
    public HollowSchema getSchema(String name) {
        return currentDataAccess.getSchema(name);
    }

    @Override
    public void resetSampling() {
        currentDataAccess.resetSampling();
    }

    @Override
    public boolean hasSampleResults() {
        return currentDataAccess.hasSampleResults();
    }

    public HollowDataAccess getProxiedDataAccess() {
        return currentDataAccess;
    }

}
