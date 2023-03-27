package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.consumer.HollowConsumerAPI;
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
import com.netflix.hollow.core.type.HString;
import com.netflix.hollow.core.type.StringHollowFactory;
import com.netflix.hollow.core.type.StringTypeAPI;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("all")
public class AwardsAPI extends HollowAPI implements  HollowConsumerAPI.StringRetriever {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final StringTypeAPI stringTypeAPI;
    private final MovieTypeAPI movieTypeAPI;
    private final SetOfMovieTypeAPI setOfMovieTypeAPI;
    private final AwardTypeAPI awardTypeAPI;

    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider movieProvider;
    private final HollowObjectProvider setOfMovieProvider;
    private final HollowObjectProvider awardProvider;

    public AwardsAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public AwardsAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public AwardsAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public AwardsAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, AwardsAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("String","Movie","SetOfMovie","Award");

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

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfMovie");
        if(typeDataAccess != null) {
            setOfMovieTypeAPI = new SetOfMovieTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfMovieTypeAPI = new SetOfMovieTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfMovie"));
        }
        addTypeAPI(setOfMovieTypeAPI);
        factory = factoryOverrides.get("SetOfMovie");
        if(factory == null)
            factory = new SetOfMovieHollowFactory();
        if(cachedTypes.contains("SetOfMovie")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfMovieProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfMovieProvider;
            setOfMovieProvider = new HollowObjectCacheProvider(typeDataAccess, setOfMovieTypeAPI, factory, previousCacheProvider);
        } else {
            setOfMovieProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfMovieTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Award");
        if(typeDataAccess != null) {
            awardTypeAPI = new AwardTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            awardTypeAPI = new AwardTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Award"));
        }
        addTypeAPI(awardTypeAPI);
        factory = factoryOverrides.get("Award");
        if(factory == null)
            factory = new AwardHollowFactory();
        if(cachedTypes.contains("Award")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardProvider;
            awardProvider = new HollowObjectCacheProvider(typeDataAccess, awardTypeAPI, factory, previousCacheProvider);
        } else {
            awardProvider = new HollowObjectFactoryProvider(typeDataAccess, awardTypeAPI, factory);
        }

    }

/* * set expectation here*/    public void detachCaches() {
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(movieProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieProvider).detach();
        if(setOfMovieProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfMovieProvider).detach();
        if(awardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardProvider).detach();
    }

    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public MovieTypeAPI getMovieTypeAPI() {
        return movieTypeAPI;
    }
    public SetOfMovieTypeAPI getSetOfMovieTypeAPI() {
        return setOfMovieTypeAPI;
    }
    public AwardTypeAPI getAwardTypeAPI() {
        return awardTypeAPI;
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
    public Collection<SetOfMovie> getAllSetOfMovie() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("SetOfMovie"), "type not loaded or does not exist in dataset; type=SetOfMovie");
        return new AllHollowRecordCollection<SetOfMovie>(tda.getTypeState()) {
            protected SetOfMovie getForOrdinal(int ordinal) {
                return getSetOfMovie(ordinal);
            }
        };
    }
    public SetOfMovie getSetOfMovie(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (SetOfMovie)setOfMovieProvider.getHollowObject(ordinal);
    }
    public Collection<Award> getAllAward() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("Award"), "type not loaded or does not exist in dataset; type=Award");
        return new AllHollowRecordCollection<Award>(tda.getTypeState()) {
            protected Award getForOrdinal(int ordinal) {
                return getAward(ordinal);
            }
        };
    }
    public Award getAward(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (Award)awardProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
