package com.netflix.vms.transformer.input.api.gen.videoAward;

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
public class VideoAwardAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final VideoAwardMappingTypeAPI videoAwardMappingTypeAPI;
    private final VideoAwardListTypeAPI videoAwardListTypeAPI;
    private final VideoAwardTypeAPI videoAwardTypeAPI;

    private final HollowObjectProvider videoAwardMappingProvider;
    private final HollowObjectProvider videoAwardListProvider;
    private final HollowObjectProvider videoAwardProvider;

    public VideoAwardAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public VideoAwardAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public VideoAwardAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public VideoAwardAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, VideoAwardAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("VideoAwardMapping","VideoAwardList","VideoAward");

        typeDataAccess = dataAccess.getTypeDataAccess("VideoAwardMapping");
        if(typeDataAccess != null) {
            videoAwardMappingTypeAPI = new VideoAwardMappingTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoAwardMappingTypeAPI = new VideoAwardMappingTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoAwardMapping"));
        }
        addTypeAPI(videoAwardMappingTypeAPI);
        factory = factoryOverrides.get("VideoAwardMapping");
        if(factory == null)
            factory = new VideoAwardMappingHollowFactory();
        if(cachedTypes.contains("VideoAwardMapping")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoAwardMappingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoAwardMappingProvider;
            videoAwardMappingProvider = new HollowObjectCacheProvider(typeDataAccess, videoAwardMappingTypeAPI, factory, previousCacheProvider);
        } else {
            videoAwardMappingProvider = new HollowObjectFactoryProvider(typeDataAccess, videoAwardMappingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoAwardList");
        if(typeDataAccess != null) {
            videoAwardListTypeAPI = new VideoAwardListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoAwardListTypeAPI = new VideoAwardListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoAwardList"));
        }
        addTypeAPI(videoAwardListTypeAPI);
        factory = factoryOverrides.get("VideoAwardList");
        if(factory == null)
            factory = new VideoAwardListHollowFactory();
        if(cachedTypes.contains("VideoAwardList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoAwardListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoAwardListProvider;
            videoAwardListProvider = new HollowObjectCacheProvider(typeDataAccess, videoAwardListTypeAPI, factory, previousCacheProvider);
        } else {
            videoAwardListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoAwardListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoAward");
        if(typeDataAccess != null) {
            videoAwardTypeAPI = new VideoAwardTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoAwardTypeAPI = new VideoAwardTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoAward"));
        }
        addTypeAPI(videoAwardTypeAPI);
        factory = factoryOverrides.get("VideoAward");
        if(factory == null)
            factory = new VideoAwardHollowFactory();
        if(cachedTypes.contains("VideoAward")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoAwardProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoAwardProvider;
            videoAwardProvider = new HollowObjectCacheProvider(typeDataAccess, videoAwardTypeAPI, factory, previousCacheProvider);
        } else {
            videoAwardProvider = new HollowObjectFactoryProvider(typeDataAccess, videoAwardTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(videoAwardMappingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardMappingProvider).detach();
        if(videoAwardListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardListProvider).detach();
        if(videoAwardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardProvider).detach();
    }

    public VideoAwardMappingTypeAPI getVideoAwardMappingTypeAPI() {
        return videoAwardMappingTypeAPI;
    }
    public VideoAwardListTypeAPI getVideoAwardListTypeAPI() {
        return videoAwardListTypeAPI;
    }
    public VideoAwardTypeAPI getVideoAwardTypeAPI() {
        return videoAwardTypeAPI;
    }
    public Collection<VideoAwardMapping> getAllVideoAwardMapping() {
        return new AllHollowRecordCollection<VideoAwardMapping>(getDataAccess().getTypeDataAccess("VideoAwardMapping").getTypeState()) {
            protected VideoAwardMapping getForOrdinal(int ordinal) {
                return getVideoAwardMapping(ordinal);
            }
        };
    }
    public VideoAwardMapping getVideoAwardMapping(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (VideoAwardMapping)videoAwardMappingProvider.getHollowObject(ordinal);
    }
    public Collection<VideoAwardList> getAllVideoAwardList() {
        return new AllHollowRecordCollection<VideoAwardList>(getDataAccess().getTypeDataAccess("VideoAwardList").getTypeState()) {
            protected VideoAwardList getForOrdinal(int ordinal) {
                return getVideoAwardList(ordinal);
            }
        };
    }
    public VideoAwardList getVideoAwardList(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (VideoAwardList)videoAwardListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoAward> getAllVideoAward() {
        return new AllHollowRecordCollection<VideoAward>(getDataAccess().getTypeDataAccess("VideoAward").getTypeState()) {
            protected VideoAward getForOrdinal(int ordinal) {
                return getVideoAward(ordinal);
            }
        };
    }
    public VideoAward getVideoAward(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (VideoAward)videoAwardProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
