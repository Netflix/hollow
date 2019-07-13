package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class ShowSeasonEpisodeAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final EpisodeTypeAPI episodeTypeAPI;
    private final EpisodeListTypeAPI episodeListTypeAPI;
    private final ISOCountryTypeAPI iSOCountryTypeAPI;
    private final ISOCountryListTypeAPI iSOCountryListTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final SeasonTypeAPI seasonTypeAPI;
    private final SeasonListTypeAPI seasonListTypeAPI;
    private final ShowSeasonEpisodeTypeAPI showSeasonEpisodeTypeAPI;

    private final HollowObjectProvider episodeProvider;
    private final HollowObjectProvider episodeListProvider;
    private final HollowObjectProvider iSOCountryProvider;
    private final HollowObjectProvider iSOCountryListProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider seasonProvider;
    private final HollowObjectProvider seasonListProvider;
    private final HollowObjectProvider showSeasonEpisodeProvider;

    public ShowSeasonEpisodeAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public ShowSeasonEpisodeAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public ShowSeasonEpisodeAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public ShowSeasonEpisodeAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, ShowSeasonEpisodeAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("Episode","EpisodeList","ISOCountry","ISOCountryList","String","Season","SeasonList","ShowSeasonEpisode");

        typeDataAccess = dataAccess.getTypeDataAccess("Episode");
        if(typeDataAccess != null) {
            episodeTypeAPI = new EpisodeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            episodeTypeAPI = new EpisodeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Episode"));
        }
        addTypeAPI(episodeTypeAPI);
        factory = factoryOverrides.get("Episode");
        if(factory == null)
            factory = new EpisodeHollowFactory();
        if(cachedTypes.contains("Episode")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.episodeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.episodeProvider;
            episodeProvider = new HollowObjectCacheProvider(typeDataAccess, episodeTypeAPI, factory, previousCacheProvider);
        } else {
            episodeProvider = new HollowObjectFactoryProvider(typeDataAccess, episodeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("EpisodeList");
        if(typeDataAccess != null) {
            episodeListTypeAPI = new EpisodeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            episodeListTypeAPI = new EpisodeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "EpisodeList"));
        }
        addTypeAPI(episodeListTypeAPI);
        factory = factoryOverrides.get("EpisodeList");
        if(factory == null)
            factory = new EpisodeListHollowFactory();
        if(cachedTypes.contains("EpisodeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.episodeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.episodeListProvider;
            episodeListProvider = new HollowObjectCacheProvider(typeDataAccess, episodeListTypeAPI, factory, previousCacheProvider);
        } else {
            episodeListProvider = new HollowObjectFactoryProvider(typeDataAccess, episodeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ISOCountry");
        if(typeDataAccess != null) {
            iSOCountryTypeAPI = new ISOCountryTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            iSOCountryTypeAPI = new ISOCountryTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ISOCountry"));
        }
        addTypeAPI(iSOCountryTypeAPI);
        factory = factoryOverrides.get("ISOCountry");
        if(factory == null)
            factory = new ISOCountryHollowFactory();
        if(cachedTypes.contains("ISOCountry")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iSOCountryProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iSOCountryProvider;
            iSOCountryProvider = new HollowObjectCacheProvider(typeDataAccess, iSOCountryTypeAPI, factory, previousCacheProvider);
        } else {
            iSOCountryProvider = new HollowObjectFactoryProvider(typeDataAccess, iSOCountryTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ISOCountryList");
        if(typeDataAccess != null) {
            iSOCountryListTypeAPI = new ISOCountryListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            iSOCountryListTypeAPI = new ISOCountryListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ISOCountryList"));
        }
        addTypeAPI(iSOCountryListTypeAPI);
        factory = factoryOverrides.get("ISOCountryList");
        if(factory == null)
            factory = new ISOCountryListHollowFactory();
        if(cachedTypes.contains("ISOCountryList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iSOCountryListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iSOCountryListProvider;
            iSOCountryListProvider = new HollowObjectCacheProvider(typeDataAccess, iSOCountryListTypeAPI, factory, previousCacheProvider);
        } else {
            iSOCountryListProvider = new HollowObjectFactoryProvider(typeDataAccess, iSOCountryListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("Season");
        if(typeDataAccess != null) {
            seasonTypeAPI = new SeasonTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            seasonTypeAPI = new SeasonTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Season"));
        }
        addTypeAPI(seasonTypeAPI);
        factory = factoryOverrides.get("Season");
        if(factory == null)
            factory = new SeasonHollowFactory();
        if(cachedTypes.contains("Season")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.seasonProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.seasonProvider;
            seasonProvider = new HollowObjectCacheProvider(typeDataAccess, seasonTypeAPI, factory, previousCacheProvider);
        } else {
            seasonProvider = new HollowObjectFactoryProvider(typeDataAccess, seasonTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SeasonList");
        if(typeDataAccess != null) {
            seasonListTypeAPI = new SeasonListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            seasonListTypeAPI = new SeasonListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "SeasonList"));
        }
        addTypeAPI(seasonListTypeAPI);
        factory = factoryOverrides.get("SeasonList");
        if(factory == null)
            factory = new SeasonListHollowFactory();
        if(cachedTypes.contains("SeasonList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.seasonListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.seasonListProvider;
            seasonListProvider = new HollowObjectCacheProvider(typeDataAccess, seasonListTypeAPI, factory, previousCacheProvider);
        } else {
            seasonListProvider = new HollowObjectFactoryProvider(typeDataAccess, seasonListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowSeasonEpisode");
        if(typeDataAccess != null) {
            showSeasonEpisodeTypeAPI = new ShowSeasonEpisodeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showSeasonEpisodeTypeAPI = new ShowSeasonEpisodeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowSeasonEpisode"));
        }
        addTypeAPI(showSeasonEpisodeTypeAPI);
        factory = factoryOverrides.get("ShowSeasonEpisode");
        if(factory == null)
            factory = new ShowSeasonEpisodeHollowFactory();
        if(cachedTypes.contains("ShowSeasonEpisode")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showSeasonEpisodeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showSeasonEpisodeProvider;
            showSeasonEpisodeProvider = new HollowObjectCacheProvider(typeDataAccess, showSeasonEpisodeTypeAPI, factory, previousCacheProvider);
        } else {
            showSeasonEpisodeProvider = new HollowObjectFactoryProvider(typeDataAccess, showSeasonEpisodeTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(episodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)episodeProvider).detach();
        if(episodeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)episodeListProvider).detach();
        if(iSOCountryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryProvider).detach();
        if(iSOCountryListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryListProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(seasonProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)seasonProvider).detach();
        if(seasonListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)seasonListProvider).detach();
        if(showSeasonEpisodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showSeasonEpisodeProvider).detach();
    }

    public EpisodeTypeAPI getEpisodeTypeAPI() {
        return episodeTypeAPI;
    }
    public EpisodeListTypeAPI getEpisodeListTypeAPI() {
        return episodeListTypeAPI;
    }
    public ISOCountryTypeAPI getISOCountryTypeAPI() {
        return iSOCountryTypeAPI;
    }
    public ISOCountryListTypeAPI getISOCountryListTypeAPI() {
        return iSOCountryListTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public SeasonTypeAPI getSeasonTypeAPI() {
        return seasonTypeAPI;
    }
    public SeasonListTypeAPI getSeasonListTypeAPI() {
        return seasonListTypeAPI;
    }
    public ShowSeasonEpisodeTypeAPI getShowSeasonEpisodeTypeAPI() {
        return showSeasonEpisodeTypeAPI;
    }
    public Collection<Episode> getAllEpisode() {
        return new AllHollowRecordCollection<Episode>(getDataAccess().getTypeDataAccess("Episode").getTypeState()) {
            protected Episode getForOrdinal(int ordinal) {
                return getEpisode(ordinal);
            }
        };
    }
    public Episode getEpisode(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (Episode)episodeProvider.getHollowObject(ordinal);
    }
    public Collection<EpisodeList> getAllEpisodeList() {
        return new AllHollowRecordCollection<EpisodeList>(getDataAccess().getTypeDataAccess("EpisodeList").getTypeState()) {
            protected EpisodeList getForOrdinal(int ordinal) {
                return getEpisodeList(ordinal);
            }
        };
    }
    public EpisodeList getEpisodeList(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (EpisodeList)episodeListProvider.getHollowObject(ordinal);
    }
    public Collection<ISOCountry> getAllISOCountry() {
        return new AllHollowRecordCollection<ISOCountry>(getDataAccess().getTypeDataAccess("ISOCountry").getTypeState()) {
            protected ISOCountry getForOrdinal(int ordinal) {
                return getISOCountry(ordinal);
            }
        };
    }
    public ISOCountry getISOCountry(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (ISOCountry)iSOCountryProvider.getHollowObject(ordinal);
    }
    public Collection<ISOCountryList> getAllISOCountryList() {
        return new AllHollowRecordCollection<ISOCountryList>(getDataAccess().getTypeDataAccess("ISOCountryList").getTypeState()) {
            protected ISOCountryList getForOrdinal(int ordinal) {
                return getISOCountryList(ordinal);
            }
        };
    }
    public ISOCountryList getISOCountryList(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (ISOCountryList)iSOCountryListProvider.getHollowObject(ordinal);
    }
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<Season> getAllSeason() {
        return new AllHollowRecordCollection<Season>(getDataAccess().getTypeDataAccess("Season").getTypeState()) {
            protected Season getForOrdinal(int ordinal) {
                return getSeason(ordinal);
            }
        };
    }
    public Season getSeason(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (Season)seasonProvider.getHollowObject(ordinal);
    }
    public Collection<SeasonList> getAllSeasonList() {
        return new AllHollowRecordCollection<SeasonList>(getDataAccess().getTypeDataAccess("SeasonList").getTypeState()) {
            protected SeasonList getForOrdinal(int ordinal) {
                return getSeasonList(ordinal);
            }
        };
    }
    public SeasonList getSeasonList(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (SeasonList)seasonListProvider.getHollowObject(ordinal);
    }
    public Collection<ShowSeasonEpisode> getAllShowSeasonEpisode() {
        return new AllHollowRecordCollection<ShowSeasonEpisode>(getDataAccess().getTypeDataAccess("ShowSeasonEpisode").getTypeState()) {
            protected ShowSeasonEpisode getForOrdinal(int ordinal) {
                return getShowSeasonEpisode(ordinal);
            }
        };
    }
    public ShowSeasonEpisode getShowSeasonEpisode(int ordinal) {
        objectCreationSampler.recordCreation(7);
        return (ShowSeasonEpisode)showSeasonEpisodeProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
