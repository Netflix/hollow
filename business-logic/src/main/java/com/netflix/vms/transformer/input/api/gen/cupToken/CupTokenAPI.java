package com.netflix.vms.transformer.input.api.gen.cupToken;

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
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class CupTokenAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final LongTypeAPI longTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final CinderCupTokenRecordTypeAPI cinderCupTokenRecordTypeAPI;

    private final HollowObjectProvider longProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider cinderCupTokenRecordProvider;

    public CupTokenAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public CupTokenAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public CupTokenAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public CupTokenAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, CupTokenAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("Long","String","CinderCupTokenRecord");

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

        typeDataAccess = dataAccess.getTypeDataAccess("CinderCupTokenRecord");
        if(typeDataAccess != null) {
            cinderCupTokenRecordTypeAPI = new CinderCupTokenRecordTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            cinderCupTokenRecordTypeAPI = new CinderCupTokenRecordTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CinderCupTokenRecord"));
        }
        addTypeAPI(cinderCupTokenRecordTypeAPI);
        factory = factoryOverrides.get("CinderCupTokenRecord");
        if(factory == null)
            factory = new CinderCupTokenRecordHollowFactory();
        if(cachedTypes.contains("CinderCupTokenRecord")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.cinderCupTokenRecordProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.cinderCupTokenRecordProvider;
            cinderCupTokenRecordProvider = new HollowObjectCacheProvider(typeDataAccess, cinderCupTokenRecordTypeAPI, factory, previousCacheProvider);
        } else {
            cinderCupTokenRecordProvider = new HollowObjectFactoryProvider(typeDataAccess, cinderCupTokenRecordTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(longProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)longProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(cinderCupTokenRecordProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cinderCupTokenRecordProvider).detach();
    }

    public LongTypeAPI getLongTypeAPI() {
        return longTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public CinderCupTokenRecordTypeAPI getCinderCupTokenRecordTypeAPI() {
        return cinderCupTokenRecordTypeAPI;
    }
    public Collection<HLong> getAllHLong() {
        return new AllHollowRecordCollection<HLong>(getDataAccess().getTypeDataAccess("Long").getTypeState()) {
            protected HLong getForOrdinal(int ordinal) {
                return getHLong(ordinal);
            }
        };
    }
    public HLong getHLong(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (HLong)longProvider.getHollowObject(ordinal);
    }
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<CinderCupTokenRecord> getAllCinderCupTokenRecord() {
        return new AllHollowRecordCollection<CinderCupTokenRecord>(getDataAccess().getTypeDataAccess("CinderCupTokenRecord").getTypeState()) {
            protected CinderCupTokenRecord getForOrdinal(int ordinal) {
                return getCinderCupTokenRecord(ordinal);
            }
        };
    }
    public CinderCupTokenRecord getCinderCupTokenRecord(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (CinderCupTokenRecord)cinderCupTokenRecordProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
