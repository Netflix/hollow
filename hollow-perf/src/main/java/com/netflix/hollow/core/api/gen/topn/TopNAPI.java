package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class TopNAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final com.netflix.hollow.core.api.gen.topn.StringTypeAPI stringTypeAPI;
    private final com.netflix.hollow.core.api.gen.topn.TopNAttributeTypeAPI topNAttributeTypeAPI;
    private final com.netflix.hollow.core.api.gen.topn.SetOfTopNAttributeTypeAPI setOfTopNAttributeTypeAPI;
    private final com.netflix.hollow.core.api.gen.topn.TopNTypeAPI topNTypeAPI;

    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider topNAttributeProvider;
    private final HollowObjectProvider setOfTopNAttributeProvider;
    private final HollowObjectProvider topNProvider;

    public TopNAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public TopNAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public TopNAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public TopNAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, TopNAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("String","TopNAttribute","SetOfTopNAttribute","TopN");

        typeDataAccess = dataAccess.getTypeDataAccess("String");
        if(typeDataAccess != null) {
            stringTypeAPI = new com.netflix.hollow.core.api.gen.topn.StringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            stringTypeAPI = new com.netflix.hollow.core.api.gen.topn.StringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "String"));
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

        typeDataAccess = dataAccess.getTypeDataAccess("TopNAttribute");
        if(typeDataAccess != null) {
            topNAttributeTypeAPI = new TopNAttributeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            topNAttributeTypeAPI = new TopNAttributeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TopNAttribute"));
        }
        addTypeAPI(topNAttributeTypeAPI);
        factory = factoryOverrides.get("TopNAttribute");
        if(factory == null)
            factory = new TopNAttributeHollowFactory();
        if(cachedTypes.contains("TopNAttribute")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.topNAttributeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.topNAttributeProvider;
            topNAttributeProvider = new HollowObjectCacheProvider(typeDataAccess, topNAttributeTypeAPI, factory, previousCacheProvider);
        } else {
            topNAttributeProvider = new HollowObjectFactoryProvider(typeDataAccess, topNAttributeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfTopNAttribute");
        if(typeDataAccess != null) {
            setOfTopNAttributeTypeAPI = new SetOfTopNAttributeTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfTopNAttributeTypeAPI = new SetOfTopNAttributeTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfTopNAttribute"));
        }
        addTypeAPI(setOfTopNAttributeTypeAPI);
        factory = factoryOverrides.get("SetOfTopNAttribute");
        if(factory == null)
            factory = new SetOfTopNAttributeHollowFactory();
        if(cachedTypes.contains("SetOfTopNAttribute")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfTopNAttributeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfTopNAttributeProvider;
            setOfTopNAttributeProvider = new HollowObjectCacheProvider(typeDataAccess, setOfTopNAttributeTypeAPI, factory, previousCacheProvider);
        } else {
            setOfTopNAttributeProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfTopNAttributeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TopN");
        if(typeDataAccess != null) {
            topNTypeAPI = new TopNTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            topNTypeAPI = new TopNTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TopN"));
        }
        addTypeAPI(topNTypeAPI);
        factory = factoryOverrides.get("TopN");
        if(factory == null)
            factory = new TopNHollowFactory();
        if(cachedTypes.contains("TopN")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.topNProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.topNProvider;
            topNProvider = new HollowObjectCacheProvider(typeDataAccess, topNTypeAPI, factory, previousCacheProvider);
        } else {
            topNProvider = new HollowObjectFactoryProvider(typeDataAccess, topNTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(topNAttributeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNAttributeProvider).detach();
        if(setOfTopNAttributeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfTopNAttributeProvider).detach();
        if(topNProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNProvider).detach();
    }

    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public TopNAttributeTypeAPI getTopNAttributeTypeAPI() {
        return topNAttributeTypeAPI;
    }
    public SetOfTopNAttributeTypeAPI getSetOfTopNAttributeTypeAPI() {
        return setOfTopNAttributeTypeAPI;
    }
    public TopNTypeAPI getTopNTypeAPI() {
        return topNTypeAPI;
    }
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<TopNAttribute> getAllTopNAttribute() {
        return new AllHollowRecordCollection<TopNAttribute>(getDataAccess().getTypeDataAccess("TopNAttribute").getTypeState()) {
            protected TopNAttribute getForOrdinal(int ordinal) {
                return getTopNAttribute(ordinal);
            }
        };
    }
    public TopNAttribute getTopNAttribute(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (TopNAttribute)topNAttributeProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfTopNAttribute> getAllSetOfTopNAttribute() {
        return new AllHollowRecordCollection<SetOfTopNAttribute>(getDataAccess().getTypeDataAccess("SetOfTopNAttribute").getTypeState()) {
            protected SetOfTopNAttribute getForOrdinal(int ordinal) {
                return getSetOfTopNAttribute(ordinal);
            }
        };
    }
    public SetOfTopNAttribute getSetOfTopNAttribute(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (SetOfTopNAttribute)setOfTopNAttributeProvider.getHollowObject(ordinal);
    }
    public Collection<TopN> getAllTopN() {
        return new AllHollowRecordCollection<TopN>(getDataAccess().getTypeDataAccess("TopN").getTypeState()) {
            protected TopN getForOrdinal(int ordinal) {
                return getTopN(ordinal);
            }
        };
    }
    public TopN getTopN(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (TopN)topNProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
