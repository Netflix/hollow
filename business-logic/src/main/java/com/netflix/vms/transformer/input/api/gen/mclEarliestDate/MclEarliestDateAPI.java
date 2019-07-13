package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class MclEarliestDateAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final LongTypeAPI longTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final MapOfStringToLongTypeAPI mapOfStringToLongTypeAPI;
    private final FeedMovieCountryLanguagesTypeAPI feedMovieCountryLanguagesTypeAPI;

    private final HollowObjectProvider longProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider mapOfStringToLongProvider;
    private final HollowObjectProvider feedMovieCountryLanguagesProvider;

    public MclEarliestDateAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public MclEarliestDateAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public MclEarliestDateAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public MclEarliestDateAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, MclEarliestDateAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("Long","String","MapOfStringToLong","FeedMovieCountryLanguages");

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

        typeDataAccess = dataAccess.getTypeDataAccess("MapOfStringToLong");
        if(typeDataAccess != null) {
            mapOfStringToLongTypeAPI = new MapOfStringToLongTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            mapOfStringToLongTypeAPI = new MapOfStringToLongTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MapOfStringToLong"));
        }
        addTypeAPI(mapOfStringToLongTypeAPI);
        factory = factoryOverrides.get("MapOfStringToLong");
        if(factory == null)
            factory = new MapOfStringToLongHollowFactory();
        if(cachedTypes.contains("MapOfStringToLong")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapOfStringToLongProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapOfStringToLongProvider;
            mapOfStringToLongProvider = new HollowObjectCacheProvider(typeDataAccess, mapOfStringToLongTypeAPI, factory, previousCacheProvider);
        } else {
            mapOfStringToLongProvider = new HollowObjectFactoryProvider(typeDataAccess, mapOfStringToLongTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FeedMovieCountryLanguages");
        if(typeDataAccess != null) {
            feedMovieCountryLanguagesTypeAPI = new FeedMovieCountryLanguagesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            feedMovieCountryLanguagesTypeAPI = new FeedMovieCountryLanguagesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FeedMovieCountryLanguages"));
        }
        addTypeAPI(feedMovieCountryLanguagesTypeAPI);
        factory = factoryOverrides.get("FeedMovieCountryLanguages");
        if(factory == null)
            factory = new FeedMovieCountryLanguagesHollowFactory();
        if(cachedTypes.contains("FeedMovieCountryLanguages")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.feedMovieCountryLanguagesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.feedMovieCountryLanguagesProvider;
            feedMovieCountryLanguagesProvider = new HollowObjectCacheProvider(typeDataAccess, feedMovieCountryLanguagesTypeAPI, factory, previousCacheProvider);
        } else {
            feedMovieCountryLanguagesProvider = new HollowObjectFactoryProvider(typeDataAccess, feedMovieCountryLanguagesTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(longProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)longProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(mapOfStringToLongProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapOfStringToLongProvider).detach();
        if(feedMovieCountryLanguagesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)feedMovieCountryLanguagesProvider).detach();
    }

    public LongTypeAPI getLongTypeAPI() {
        return longTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public MapOfStringToLongTypeAPI getMapOfStringToLongTypeAPI() {
        return mapOfStringToLongTypeAPI;
    }
    public FeedMovieCountryLanguagesTypeAPI getFeedMovieCountryLanguagesTypeAPI() {
        return feedMovieCountryLanguagesTypeAPI;
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
    public Collection<MapOfStringToLong> getAllMapOfStringToLong() {
        return new AllHollowRecordCollection<MapOfStringToLong>(getDataAccess().getTypeDataAccess("MapOfStringToLong").getTypeState()) {
            protected MapOfStringToLong getForOrdinal(int ordinal) {
                return getMapOfStringToLong(ordinal);
            }
        };
    }
    public MapOfStringToLong getMapOfStringToLong(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (MapOfStringToLong)mapOfStringToLongProvider.getHollowObject(ordinal);
    }
    public Collection<FeedMovieCountryLanguages> getAllFeedMovieCountryLanguages() {
        return new AllHollowRecordCollection<FeedMovieCountryLanguages>(getDataAccess().getTypeDataAccess("FeedMovieCountryLanguages").getTypeState()) {
            protected FeedMovieCountryLanguages getForOrdinal(int ordinal) {
                return getFeedMovieCountryLanguages(ordinal);
            }
        };
    }
    public FeedMovieCountryLanguages getFeedMovieCountryLanguages(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (FeedMovieCountryLanguages)feedMovieCountryLanguagesProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
