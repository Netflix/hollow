package com.netflix.hollow.test.generated;

import java.util.Objects;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.util.AllHollowRecordCollection;

@SuppressWarnings("all")
public class MovieAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final StringTypeAPI stringTypeAPI;
    private final MovieTypeAPI movieTypeAPI;

    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider movieProvider;

    public MovieAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public MovieAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public MovieAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public MovieAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, MovieAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("String","Movie");

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

        typeDataAccess = dataAccess.getTypeDataAccess("Movie");
        if(typeDataAccess != null) {
            movieTypeAPI = new MovieTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieTypeAPI = new MovieTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Movie"));
        }
        addTypeAPI(movieTypeAPI);
        factory = factoryOverrides.get("Movie");
        if(factory == null)
            factory = new MovieHollowFactory();
        if(cachedTypes.contains("Movie")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieProvider;
            movieProvider = new HollowObjectCacheProvider(typeDataAccess, movieTypeAPI, factory, previousCacheProvider);
        } else {
            movieProvider = new HollowObjectFactoryProvider(typeDataAccess, movieTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(movieProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieProvider).detach();
    }

    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public MovieTypeAPI getMovieTypeAPI() {
        return movieTypeAPI;
    }
    public Collection<HString> getAllHString() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("String"), "type not loaded or does not exist in dataset; type=String");
        return new AllHollowRecordCollection<HString>(tda.getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<Movie> getAllMovie() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("Movie"), "type not loaded or does not exist in dataset; type=Movie");
        return new AllHollowRecordCollection<Movie>(tda.getTypeState()) {
            protected Movie getForOrdinal(int ordinal) {
                return getMovie(ordinal);
            }
        };
    }
    public Movie getMovie(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (Movie)movieProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
