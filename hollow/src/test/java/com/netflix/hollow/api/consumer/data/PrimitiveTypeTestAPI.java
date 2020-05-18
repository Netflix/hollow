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
package com.netflix.hollow.api.consumer.data;

import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.type.BooleanHollowFactory;
import com.netflix.hollow.core.type.BooleanTypeAPI;
import com.netflix.hollow.core.type.DoubleHollowFactory;
import com.netflix.hollow.core.type.DoubleTypeAPI;
import com.netflix.hollow.core.type.FloatHollowFactory;
import com.netflix.hollow.core.type.FloatTypeAPI;
import com.netflix.hollow.core.type.HBoolean;
import com.netflix.hollow.core.type.HDouble;
import com.netflix.hollow.core.type.HFloat;
import com.netflix.hollow.core.type.HInteger;
import com.netflix.hollow.core.type.HLong;
import com.netflix.hollow.core.type.HString;
import com.netflix.hollow.core.type.IntegerHollowFactory;
import com.netflix.hollow.core.type.IntegerTypeAPI;
import com.netflix.hollow.core.type.LongHollowFactory;
import com.netflix.hollow.core.type.LongTypeAPI;
import com.netflix.hollow.core.type.StringHollowFactory;
import com.netflix.hollow.core.type.StringTypeAPI;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class PrimitiveTypeTestAPI extends HollowAPI implements  HollowConsumerAPI.FloatRetriever, HollowConsumerAPI.DoubleRetriever, HollowConsumerAPI.LongRetriever, HollowConsumerAPI.StringRetriever, HollowConsumerAPI.IntegerRetriever, HollowConsumerAPI.BooleanRetriever {

    private final BooleanTypeAPI booleanTypeAPI;
    private final DoubleTypeAPI doubleTypeAPI;
    private final FloatTypeAPI floatTypeAPI;
    private final IntegerTypeAPI integerTypeAPI;
    private final LongTypeAPI longTypeAPI;
    private final StringTypeAPI stringTypeAPI;

    private final HollowObjectProvider booleanProvider;
    private final HollowObjectProvider doubleProvider;
    private final HollowObjectProvider floatProvider;
    private final HollowObjectProvider integerProvider;
    private final HollowObjectProvider longProvider;
    private final HollowObjectProvider stringProvider;

    public PrimitiveTypeTestAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public PrimitiveTypeTestAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public PrimitiveTypeTestAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public PrimitiveTypeTestAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, PrimitiveTypeTestAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        typeDataAccess = dataAccess.getTypeDataAccess("Boolean");
        if(typeDataAccess != null) {
            booleanTypeAPI = new BooleanTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            booleanTypeAPI = new BooleanTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Boolean"));
        }
        addTypeAPI(booleanTypeAPI);
        factory = factoryOverrides.get("Boolean");
        if(factory == null)
            factory = new BooleanHollowFactory();
        if(cachedTypes.contains("Boolean")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.booleanProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.booleanProvider;
            booleanProvider = new HollowObjectCacheProvider(typeDataAccess, booleanTypeAPI, factory, previousCacheProvider);
        } else {
            booleanProvider = new HollowObjectFactoryProvider(typeDataAccess, booleanTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Double");
        if(typeDataAccess != null) {
            doubleTypeAPI = new DoubleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            doubleTypeAPI = new DoubleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Double"));
        }
        addTypeAPI(doubleTypeAPI);
        factory = factoryOverrides.get("Double");
        if(factory == null)
            factory = new DoubleHollowFactory();
        if(cachedTypes.contains("Double")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.doubleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.doubleProvider;
            doubleProvider = new HollowObjectCacheProvider(typeDataAccess, doubleTypeAPI, factory, previousCacheProvider);
        } else {
            doubleProvider = new HollowObjectFactoryProvider(typeDataAccess, doubleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Float");
        if(typeDataAccess != null) {
            floatTypeAPI = new FloatTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            floatTypeAPI = new FloatTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Float"));
        }
        addTypeAPI(floatTypeAPI);
        factory = factoryOverrides.get("Float");
        if(factory == null)
            factory = new FloatHollowFactory();
        if(cachedTypes.contains("Float")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.floatProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.floatProvider;
            floatProvider = new HollowObjectCacheProvider(typeDataAccess, floatTypeAPI, factory, previousCacheProvider);
        } else {
            floatProvider = new HollowObjectFactoryProvider(typeDataAccess, floatTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Integer");
        if(typeDataAccess != null) {
            integerTypeAPI = new IntegerTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            integerTypeAPI = new IntegerTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Integer"));
        }
        addTypeAPI(integerTypeAPI);
        factory = factoryOverrides.get("Integer");
        if(factory == null)
            factory = new IntegerHollowFactory();
        if(cachedTypes.contains("Integer")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.integerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.integerProvider;
            integerProvider = new HollowObjectCacheProvider(typeDataAccess, integerTypeAPI, factory, previousCacheProvider);
        } else {
            integerProvider = new HollowObjectFactoryProvider(typeDataAccess, integerTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Long");
        if(typeDataAccess != null) {
            longTypeAPI = new LongTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            longTypeAPI = new LongTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Long"));
        }
        addTypeAPI(longTypeAPI);
        factory = factoryOverrides.get("Long");
        if(factory == null)
            factory = new LongHollowFactory();
        if(cachedTypes.contains("Long")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.longProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.longProvider;
            longProvider = new HollowObjectCacheProvider(typeDataAccess, longTypeAPI, factory, previousCacheProvider);
        } else {
            longProvider = new HollowObjectFactoryProvider(typeDataAccess, longTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("String");
        if(typeDataAccess != null) {
            stringTypeAPI = new StringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            stringTypeAPI = new StringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "String"));
        }
        addTypeAPI(stringTypeAPI);
        factory = factoryOverrides.get("String");
        if(factory == null)
            factory = new StringHollowFactory();
        if(cachedTypes.contains("String")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stringProvider;
            stringProvider = new HollowObjectCacheProvider(typeDataAccess, stringTypeAPI, factory, previousCacheProvider);
        } else {
            stringProvider = new HollowObjectFactoryProvider(typeDataAccess, stringTypeAPI, factory);
        }
    }

    @Override
    public void detachCaches() {
        if(booleanProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)booleanProvider).detach();
        if(doubleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)doubleProvider).detach();
        if(floatProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)floatProvider).detach();
        if(integerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)integerProvider).detach();
        if(longProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)longProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
    }

    public BooleanTypeAPI getBooleanTypeAPI() {
        return booleanTypeAPI;
    }
    public DoubleTypeAPI getDoubleTypeAPI() {
        return doubleTypeAPI;
    }
    public FloatTypeAPI getFloatTypeAPI() {
        return floatTypeAPI;
    }
    public IntegerTypeAPI getIntegerTypeAPI() {
        return integerTypeAPI;
    }
    public LongTypeAPI getLongTypeAPI() {
        return longTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    @Override
    public Collection<HBoolean> getAllHBoolean() {
        return new AllHollowRecordCollection<HBoolean>(getDataAccess().getTypeDataAccess("Boolean").getTypeState()) {
            @Override
            protected HBoolean getForOrdinal(int ordinal) {
                return getHBoolean(ordinal);
            }
        };
    }
    @Override
    public HBoolean getHBoolean(int ordinal) {
        return (HBoolean)booleanProvider.getHollowObject(ordinal);
    }
    @Override
    public Collection<HDouble> getAllHDouble() {
        return new AllHollowRecordCollection<HDouble>(getDataAccess().getTypeDataAccess("Double").getTypeState()) {
            @Override
            protected HDouble getForOrdinal(int ordinal) {
                return getHDouble(ordinal);
            }
        };
    }
    @Override
    public HDouble getHDouble(int ordinal) {
        return (HDouble)doubleProvider.getHollowObject(ordinal);
    }
    @Override
    public Collection<HFloat> getAllHFloat() {
        return new AllHollowRecordCollection<HFloat>(getDataAccess().getTypeDataAccess("Float").getTypeState()) {
            @Override
            protected HFloat getForOrdinal(int ordinal) {
                return getHFloat(ordinal);
            }
        };
    }
    @Override
    public HFloat getHFloat(int ordinal) {
        return (HFloat)floatProvider.getHollowObject(ordinal);
    }
    @Override
    public Collection<HInteger> getAllHInteger() {
        return new AllHollowRecordCollection<HInteger>(getDataAccess().getTypeDataAccess("Integer").getTypeState()) {
            @Override
            protected HInteger getForOrdinal(int ordinal) {
                return getHInteger(ordinal);
            }
        };
    }
    @Override
    public HInteger getHInteger(int ordinal) {
        return (HInteger)integerProvider.getHollowObject(ordinal);
    }
    @Override
    public Collection<HLong> getAllHLong() {
        return new AllHollowRecordCollection<HLong>(getDataAccess().getTypeDataAccess("Long").getTypeState()) {
            @Override
            protected HLong getForOrdinal(int ordinal) {
                return getHLong(ordinal);
            }
        };
    }
    @Override
    public HLong getHLong(int ordinal) {
        return (HLong)longProvider.getHollowObject(ordinal);
    }

    @Override
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            @Override
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    @Override
    public HString getHString(int ordinal) {
        return (HString)stringProvider.getHollowObject(ordinal);
    }
}