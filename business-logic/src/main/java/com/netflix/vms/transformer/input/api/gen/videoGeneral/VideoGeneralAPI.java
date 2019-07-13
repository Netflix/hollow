package com.netflix.vms.transformer.input.api.gen.videoGeneral;

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
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class VideoGeneralAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final StringTypeAPI stringTypeAPI;
    private final SetOfStringTypeAPI setOfStringTypeAPI;
    private final VideoGeneralAliasTypeAPI videoGeneralAliasTypeAPI;
    private final VideoGeneralAliasListTypeAPI videoGeneralAliasListTypeAPI;
    private final VideoGeneralEpisodeTypeTypeAPI videoGeneralEpisodeTypeTypeAPI;
    private final VideoGeneralEpisodeTypeListTypeAPI videoGeneralEpisodeTypeListTypeAPI;
    private final VideoGeneralInteractiveDataTypeAPI videoGeneralInteractiveDataTypeAPI;
    private final VideoGeneralTitleTypeTypeAPI videoGeneralTitleTypeTypeAPI;
    private final VideoGeneralTitleTypeListTypeAPI videoGeneralTitleTypeListTypeAPI;
    private final VideoGeneralTypeAPI videoGeneralTypeAPI;

    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider setOfStringProvider;
    private final HollowObjectProvider videoGeneralAliasProvider;
    private final HollowObjectProvider videoGeneralAliasListProvider;
    private final HollowObjectProvider videoGeneralEpisodeTypeProvider;
    private final HollowObjectProvider videoGeneralEpisodeTypeListProvider;
    private final HollowObjectProvider videoGeneralInteractiveDataProvider;
    private final HollowObjectProvider videoGeneralTitleTypeProvider;
    private final HollowObjectProvider videoGeneralTitleTypeListProvider;
    private final HollowObjectProvider videoGeneralProvider;

    public VideoGeneralAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public VideoGeneralAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public VideoGeneralAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public VideoGeneralAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, VideoGeneralAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("String","SetOfString","VideoGeneralAlias","VideoGeneralAliasList","VideoGeneralEpisodeType","VideoGeneralEpisodeTypeList","VideoGeneralInteractiveData","VideoGeneralTitleType","VideoGeneralTitleTypeList","VideoGeneral");

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

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfString");
        if(typeDataAccess != null) {
            setOfStringTypeAPI = new SetOfStringTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfStringTypeAPI = new SetOfStringTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfString"));
        }
        addTypeAPI(setOfStringTypeAPI);
        factory = factoryOverrides.get("SetOfString");
        if(factory == null)
            factory = new SetOfStringHollowFactory();
        if(cachedTypes.contains("SetOfString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfStringProvider;
            setOfStringProvider = new HollowObjectCacheProvider(typeDataAccess, setOfStringTypeAPI, factory, previousCacheProvider);
        } else {
            setOfStringProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfStringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralAlias");
        if(typeDataAccess != null) {
            videoGeneralAliasTypeAPI = new VideoGeneralAliasTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralAliasTypeAPI = new VideoGeneralAliasTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralAlias"));
        }
        addTypeAPI(videoGeneralAliasTypeAPI);
        factory = factoryOverrides.get("VideoGeneralAlias");
        if(factory == null)
            factory = new VideoGeneralAliasHollowFactory();
        if(cachedTypes.contains("VideoGeneralAlias")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralAliasProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralAliasProvider;
            videoGeneralAliasProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralAliasTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralAliasProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralAliasTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralAliasList");
        if(typeDataAccess != null) {
            videoGeneralAliasListTypeAPI = new VideoGeneralAliasListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralAliasListTypeAPI = new VideoGeneralAliasListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoGeneralAliasList"));
        }
        addTypeAPI(videoGeneralAliasListTypeAPI);
        factory = factoryOverrides.get("VideoGeneralAliasList");
        if(factory == null)
            factory = new VideoGeneralAliasListHollowFactory();
        if(cachedTypes.contains("VideoGeneralAliasList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralAliasListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralAliasListProvider;
            videoGeneralAliasListProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralAliasListTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralAliasListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralAliasListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralEpisodeType");
        if(typeDataAccess != null) {
            videoGeneralEpisodeTypeTypeAPI = new VideoGeneralEpisodeTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralEpisodeTypeTypeAPI = new VideoGeneralEpisodeTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralEpisodeType"));
        }
        addTypeAPI(videoGeneralEpisodeTypeTypeAPI);
        factory = factoryOverrides.get("VideoGeneralEpisodeType");
        if(factory == null)
            factory = new VideoGeneralEpisodeTypeHollowFactory();
        if(cachedTypes.contains("VideoGeneralEpisodeType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralEpisodeTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralEpisodeTypeProvider;
            videoGeneralEpisodeTypeProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralEpisodeTypeTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralEpisodeTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralEpisodeTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralEpisodeTypeList");
        if(typeDataAccess != null) {
            videoGeneralEpisodeTypeListTypeAPI = new VideoGeneralEpisodeTypeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralEpisodeTypeListTypeAPI = new VideoGeneralEpisodeTypeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoGeneralEpisodeTypeList"));
        }
        addTypeAPI(videoGeneralEpisodeTypeListTypeAPI);
        factory = factoryOverrides.get("VideoGeneralEpisodeTypeList");
        if(factory == null)
            factory = new VideoGeneralEpisodeTypeListHollowFactory();
        if(cachedTypes.contains("VideoGeneralEpisodeTypeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralEpisodeTypeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralEpisodeTypeListProvider;
            videoGeneralEpisodeTypeListProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralEpisodeTypeListTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralEpisodeTypeListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralEpisodeTypeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralInteractiveData");
        if(typeDataAccess != null) {
            videoGeneralInteractiveDataTypeAPI = new VideoGeneralInteractiveDataTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralInteractiveDataTypeAPI = new VideoGeneralInteractiveDataTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralInteractiveData"));
        }
        addTypeAPI(videoGeneralInteractiveDataTypeAPI);
        factory = factoryOverrides.get("VideoGeneralInteractiveData");
        if(factory == null)
            factory = new VideoGeneralInteractiveDataHollowFactory();
        if(cachedTypes.contains("VideoGeneralInteractiveData")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralInteractiveDataProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralInteractiveDataProvider;
            videoGeneralInteractiveDataProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralInteractiveDataTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralInteractiveDataProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralInteractiveDataTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralTitleType");
        if(typeDataAccess != null) {
            videoGeneralTitleTypeTypeAPI = new VideoGeneralTitleTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralTitleTypeTypeAPI = new VideoGeneralTitleTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralTitleType"));
        }
        addTypeAPI(videoGeneralTitleTypeTypeAPI);
        factory = factoryOverrides.get("VideoGeneralTitleType");
        if(factory == null)
            factory = new VideoGeneralTitleTypeHollowFactory();
        if(cachedTypes.contains("VideoGeneralTitleType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralTitleTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralTitleTypeProvider;
            videoGeneralTitleTypeProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralTitleTypeTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralTitleTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralTitleTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralTitleTypeList");
        if(typeDataAccess != null) {
            videoGeneralTitleTypeListTypeAPI = new VideoGeneralTitleTypeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralTitleTypeListTypeAPI = new VideoGeneralTitleTypeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoGeneralTitleTypeList"));
        }
        addTypeAPI(videoGeneralTitleTypeListTypeAPI);
        factory = factoryOverrides.get("VideoGeneralTitleTypeList");
        if(factory == null)
            factory = new VideoGeneralTitleTypeListHollowFactory();
        if(cachedTypes.contains("VideoGeneralTitleTypeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralTitleTypeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralTitleTypeListProvider;
            videoGeneralTitleTypeListProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralTitleTypeListTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralTitleTypeListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralTitleTypeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneral");
        if(typeDataAccess != null) {
            videoGeneralTypeAPI = new VideoGeneralTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralTypeAPI = new VideoGeneralTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneral"));
        }
        addTypeAPI(videoGeneralTypeAPI);
        factory = factoryOverrides.get("VideoGeneral");
        if(factory == null)
            factory = new VideoGeneralHollowFactory();
        if(cachedTypes.contains("VideoGeneral")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralProvider;
            videoGeneralProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(setOfStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfStringProvider).detach();
        if(videoGeneralAliasProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralAliasProvider).detach();
        if(videoGeneralAliasListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralAliasListProvider).detach();
        if(videoGeneralEpisodeTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralEpisodeTypeProvider).detach();
        if(videoGeneralEpisodeTypeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralEpisodeTypeListProvider).detach();
        if(videoGeneralInteractiveDataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralInteractiveDataProvider).detach();
        if(videoGeneralTitleTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralTitleTypeProvider).detach();
        if(videoGeneralTitleTypeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralTitleTypeListProvider).detach();
        if(videoGeneralProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralProvider).detach();
    }

    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public SetOfStringTypeAPI getSetOfStringTypeAPI() {
        return setOfStringTypeAPI;
    }
    public VideoGeneralAliasTypeAPI getVideoGeneralAliasTypeAPI() {
        return videoGeneralAliasTypeAPI;
    }
    public VideoGeneralAliasListTypeAPI getVideoGeneralAliasListTypeAPI() {
        return videoGeneralAliasListTypeAPI;
    }
    public VideoGeneralEpisodeTypeTypeAPI getVideoGeneralEpisodeTypeTypeAPI() {
        return videoGeneralEpisodeTypeTypeAPI;
    }
    public VideoGeneralEpisodeTypeListTypeAPI getVideoGeneralEpisodeTypeListTypeAPI() {
        return videoGeneralEpisodeTypeListTypeAPI;
    }
    public VideoGeneralInteractiveDataTypeAPI getVideoGeneralInteractiveDataTypeAPI() {
        return videoGeneralInteractiveDataTypeAPI;
    }
    public VideoGeneralTitleTypeTypeAPI getVideoGeneralTitleTypeTypeAPI() {
        return videoGeneralTitleTypeTypeAPI;
    }
    public VideoGeneralTitleTypeListTypeAPI getVideoGeneralTitleTypeListTypeAPI() {
        return videoGeneralTitleTypeListTypeAPI;
    }
    public VideoGeneralTypeAPI getVideoGeneralTypeAPI() {
        return videoGeneralTypeAPI;
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
    public Collection<SetOfString> getAllSetOfString() {
        return new AllHollowRecordCollection<SetOfString>(getDataAccess().getTypeDataAccess("SetOfString").getTypeState()) {
            protected SetOfString getForOrdinal(int ordinal) {
                return getSetOfString(ordinal);
            }
        };
    }
    public SetOfString getSetOfString(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (SetOfString)setOfStringProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralAlias> getAllVideoGeneralAlias() {
        return new AllHollowRecordCollection<VideoGeneralAlias>(getDataAccess().getTypeDataAccess("VideoGeneralAlias").getTypeState()) {
            protected VideoGeneralAlias getForOrdinal(int ordinal) {
                return getVideoGeneralAlias(ordinal);
            }
        };
    }
    public VideoGeneralAlias getVideoGeneralAlias(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (VideoGeneralAlias)videoGeneralAliasProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralAliasList> getAllVideoGeneralAliasList() {
        return new AllHollowRecordCollection<VideoGeneralAliasList>(getDataAccess().getTypeDataAccess("VideoGeneralAliasList").getTypeState()) {
            protected VideoGeneralAliasList getForOrdinal(int ordinal) {
                return getVideoGeneralAliasList(ordinal);
            }
        };
    }
    public VideoGeneralAliasList getVideoGeneralAliasList(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (VideoGeneralAliasList)videoGeneralAliasListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralEpisodeType> getAllVideoGeneralEpisodeType() {
        return new AllHollowRecordCollection<VideoGeneralEpisodeType>(getDataAccess().getTypeDataAccess("VideoGeneralEpisodeType").getTypeState()) {
            protected VideoGeneralEpisodeType getForOrdinal(int ordinal) {
                return getVideoGeneralEpisodeType(ordinal);
            }
        };
    }
    public VideoGeneralEpisodeType getVideoGeneralEpisodeType(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (VideoGeneralEpisodeType)videoGeneralEpisodeTypeProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralEpisodeTypeList> getAllVideoGeneralEpisodeTypeList() {
        return new AllHollowRecordCollection<VideoGeneralEpisodeTypeList>(getDataAccess().getTypeDataAccess("VideoGeneralEpisodeTypeList").getTypeState()) {
            protected VideoGeneralEpisodeTypeList getForOrdinal(int ordinal) {
                return getVideoGeneralEpisodeTypeList(ordinal);
            }
        };
    }
    public VideoGeneralEpisodeTypeList getVideoGeneralEpisodeTypeList(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (VideoGeneralEpisodeTypeList)videoGeneralEpisodeTypeListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralInteractiveData> getAllVideoGeneralInteractiveData() {
        return new AllHollowRecordCollection<VideoGeneralInteractiveData>(getDataAccess().getTypeDataAccess("VideoGeneralInteractiveData").getTypeState()) {
            protected VideoGeneralInteractiveData getForOrdinal(int ordinal) {
                return getVideoGeneralInteractiveData(ordinal);
            }
        };
    }
    public VideoGeneralInteractiveData getVideoGeneralInteractiveData(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (VideoGeneralInteractiveData)videoGeneralInteractiveDataProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralTitleType> getAllVideoGeneralTitleType() {
        return new AllHollowRecordCollection<VideoGeneralTitleType>(getDataAccess().getTypeDataAccess("VideoGeneralTitleType").getTypeState()) {
            protected VideoGeneralTitleType getForOrdinal(int ordinal) {
                return getVideoGeneralTitleType(ordinal);
            }
        };
    }
    public VideoGeneralTitleType getVideoGeneralTitleType(int ordinal) {
        objectCreationSampler.recordCreation(7);
        return (VideoGeneralTitleType)videoGeneralTitleTypeProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralTitleTypeList> getAllVideoGeneralTitleTypeList() {
        return new AllHollowRecordCollection<VideoGeneralTitleTypeList>(getDataAccess().getTypeDataAccess("VideoGeneralTitleTypeList").getTypeState()) {
            protected VideoGeneralTitleTypeList getForOrdinal(int ordinal) {
                return getVideoGeneralTitleTypeList(ordinal);
            }
        };
    }
    public VideoGeneralTitleTypeList getVideoGeneralTitleTypeList(int ordinal) {
        objectCreationSampler.recordCreation(8);
        return (VideoGeneralTitleTypeList)videoGeneralTitleTypeListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneral> getAllVideoGeneral() {
        return new AllHollowRecordCollection<VideoGeneral>(getDataAccess().getTypeDataAccess("VideoGeneral").getTypeState()) {
            protected VideoGeneral getForOrdinal(int ordinal) {
                return getVideoGeneral(ordinal);
            }
        };
    }
    public VideoGeneral getVideoGeneral(int ordinal) {
        objectCreationSampler.recordCreation(9);
        return (VideoGeneral)videoGeneralProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
