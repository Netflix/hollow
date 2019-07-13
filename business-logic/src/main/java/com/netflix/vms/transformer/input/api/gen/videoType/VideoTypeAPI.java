package com.netflix.vms.transformer.input.api.gen.videoType;

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
public class VideoTypeAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final StringTypeAPI stringTypeAPI;
    private final VideoTypeMediaTypeAPI videoTypeMediaTypeAPI;
    private final VideoTypeMediaListTypeAPI videoTypeMediaListTypeAPI;
    private final VideoTypeDescriptorTypeAPI videoTypeDescriptorTypeAPI;
    private final VideoTypeDescriptorSetTypeAPI videoTypeDescriptorSetTypeAPI;
    private final VideoTypeTypeAPI videoTypeTypeAPI;

    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider videoTypeMediaProvider;
    private final HollowObjectProvider videoTypeMediaListProvider;
    private final HollowObjectProvider videoTypeDescriptorProvider;
    private final HollowObjectProvider videoTypeDescriptorSetProvider;
    private final HollowObjectProvider videoTypeProvider;

    public VideoTypeAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public VideoTypeAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public VideoTypeAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public VideoTypeAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, VideoTypeAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("String","VideoTypeMedia","VideoTypeMediaList","VideoTypeDescriptor","VideoTypeDescriptorSet","VideoType");

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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeMedia");
        if(typeDataAccess != null) {
            videoTypeMediaTypeAPI = new VideoTypeMediaTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoTypeMediaTypeAPI = new VideoTypeMediaTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoTypeMedia"));
        }
        addTypeAPI(videoTypeMediaTypeAPI);
        factory = factoryOverrides.get("VideoTypeMedia");
        if(factory == null)
            factory = new VideoTypeMediaHollowFactory();
        if(cachedTypes.contains("VideoTypeMedia")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeMediaProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeMediaProvider;
            videoTypeMediaProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeMediaTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeMediaProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeMediaTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeMediaList");
        if(typeDataAccess != null) {
            videoTypeMediaListTypeAPI = new VideoTypeMediaListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoTypeMediaListTypeAPI = new VideoTypeMediaListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoTypeMediaList"));
        }
        addTypeAPI(videoTypeMediaListTypeAPI);
        factory = factoryOverrides.get("VideoTypeMediaList");
        if(factory == null)
            factory = new VideoTypeMediaListHollowFactory();
        if(cachedTypes.contains("VideoTypeMediaList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeMediaListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeMediaListProvider;
            videoTypeMediaListProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeMediaListTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeMediaListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeMediaListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeDescriptor");
        if(typeDataAccess != null) {
            videoTypeDescriptorTypeAPI = new VideoTypeDescriptorTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoTypeDescriptorTypeAPI = new VideoTypeDescriptorTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoTypeDescriptor"));
        }
        addTypeAPI(videoTypeDescriptorTypeAPI);
        factory = factoryOverrides.get("VideoTypeDescriptor");
        if(factory == null)
            factory = new VideoTypeDescriptorHollowFactory();
        if(cachedTypes.contains("VideoTypeDescriptor")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeDescriptorProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeDescriptorProvider;
            videoTypeDescriptorProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeDescriptorTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeDescriptorProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeDescriptorTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeDescriptorSet");
        if(typeDataAccess != null) {
            videoTypeDescriptorSetTypeAPI = new VideoTypeDescriptorSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            videoTypeDescriptorSetTypeAPI = new VideoTypeDescriptorSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "VideoTypeDescriptorSet"));
        }
        addTypeAPI(videoTypeDescriptorSetTypeAPI);
        factory = factoryOverrides.get("VideoTypeDescriptorSet");
        if(factory == null)
            factory = new VideoTypeDescriptorSetHollowFactory();
        if(cachedTypes.contains("VideoTypeDescriptorSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeDescriptorSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeDescriptorSetProvider;
            videoTypeDescriptorSetProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeDescriptorSetTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeDescriptorSetProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeDescriptorSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoType");
        if(typeDataAccess != null) {
            videoTypeTypeAPI = new VideoTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoTypeTypeAPI = new VideoTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoType"));
        }
        addTypeAPI(videoTypeTypeAPI);
        factory = factoryOverrides.get("VideoType");
        if(factory == null)
            factory = new VideoTypeHollowFactory();
        if(cachedTypes.contains("VideoType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeProvider;
            videoTypeProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(videoTypeMediaProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeMediaProvider).detach();
        if(videoTypeMediaListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeMediaListProvider).detach();
        if(videoTypeDescriptorProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeDescriptorProvider).detach();
        if(videoTypeDescriptorSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeDescriptorSetProvider).detach();
        if(videoTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeProvider).detach();
    }

    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public VideoTypeMediaTypeAPI getVideoTypeMediaTypeAPI() {
        return videoTypeMediaTypeAPI;
    }
    public VideoTypeMediaListTypeAPI getVideoTypeMediaListTypeAPI() {
        return videoTypeMediaListTypeAPI;
    }
    public VideoTypeDescriptorTypeAPI getVideoTypeDescriptorTypeAPI() {
        return videoTypeDescriptorTypeAPI;
    }
    public VideoTypeDescriptorSetTypeAPI getVideoTypeDescriptorSetTypeAPI() {
        return videoTypeDescriptorSetTypeAPI;
    }
    public VideoTypeTypeAPI getVideoTypeTypeAPI() {
        return videoTypeTypeAPI;
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
    public Collection<VideoTypeMedia> getAllVideoTypeMedia() {
        return new AllHollowRecordCollection<VideoTypeMedia>(getDataAccess().getTypeDataAccess("VideoTypeMedia").getTypeState()) {
            protected VideoTypeMedia getForOrdinal(int ordinal) {
                return getVideoTypeMedia(ordinal);
            }
        };
    }
    public VideoTypeMedia getVideoTypeMedia(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (VideoTypeMedia)videoTypeMediaProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeMediaList> getAllVideoTypeMediaList() {
        return new AllHollowRecordCollection<VideoTypeMediaList>(getDataAccess().getTypeDataAccess("VideoTypeMediaList").getTypeState()) {
            protected VideoTypeMediaList getForOrdinal(int ordinal) {
                return getVideoTypeMediaList(ordinal);
            }
        };
    }
    public VideoTypeMediaList getVideoTypeMediaList(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (VideoTypeMediaList)videoTypeMediaListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeDescriptor> getAllVideoTypeDescriptor() {
        return new AllHollowRecordCollection<VideoTypeDescriptor>(getDataAccess().getTypeDataAccess("VideoTypeDescriptor").getTypeState()) {
            protected VideoTypeDescriptor getForOrdinal(int ordinal) {
                return getVideoTypeDescriptor(ordinal);
            }
        };
    }
    public VideoTypeDescriptor getVideoTypeDescriptor(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (VideoTypeDescriptor)videoTypeDescriptorProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeDescriptorSet> getAllVideoTypeDescriptorSet() {
        return new AllHollowRecordCollection<VideoTypeDescriptorSet>(getDataAccess().getTypeDataAccess("VideoTypeDescriptorSet").getTypeState()) {
            protected VideoTypeDescriptorSet getForOrdinal(int ordinal) {
                return getVideoTypeDescriptorSet(ordinal);
            }
        };
    }
    public VideoTypeDescriptorSet getVideoTypeDescriptorSet(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (VideoTypeDescriptorSet)videoTypeDescriptorSetProvider.getHollowObject(ordinal);
    }
    public Collection<VideoType> getAllVideoType() {
        return new AllHollowRecordCollection<VideoType>(getDataAccess().getTypeDataAccess("VideoType").getTypeState()) {
            protected VideoType getForOrdinal(int ordinal) {
                return getVideoType(ordinal);
            }
        };
    }
    public VideoType getVideoType(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (VideoType)videoTypeProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
