package com.netflix.vms.transformer.input.api.gen.videoDate;

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
public class VideoDateAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final StringTypeAPI stringTypeAPI;
    private final ReleaseDateTypeAPI releaseDateTypeAPI;
    private final ListOfReleaseDatesTypeAPI listOfReleaseDatesTypeAPI;
    private final VideoDateWindowTypeAPI videoDateWindowTypeAPI;
    private final VideoDateWindowListTypeAPI videoDateWindowListTypeAPI;
    private final VideoDateTypeAPI videoDateTypeAPI;

    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider releaseDateProvider;
    private final HollowObjectProvider listOfReleaseDatesProvider;
    private final HollowObjectProvider videoDateWindowProvider;
    private final HollowObjectProvider videoDateWindowListProvider;
    private final HollowObjectProvider videoDateProvider;

    public VideoDateAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public VideoDateAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public VideoDateAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public VideoDateAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, VideoDateAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("String","ReleaseDate","ListOfReleaseDates","VideoDateWindow","VideoDateWindowList","VideoDate");

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

        typeDataAccess = dataAccess.getTypeDataAccess("ReleaseDate");
        if(typeDataAccess != null) {
            releaseDateTypeAPI = new ReleaseDateTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            releaseDateTypeAPI = new ReleaseDateTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ReleaseDate"));
        }
        addTypeAPI(releaseDateTypeAPI);
        factory = factoryOverrides.get("ReleaseDate");
        if(factory == null)
            factory = new ReleaseDateHollowFactory();
        if(cachedTypes.contains("ReleaseDate")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.releaseDateProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.releaseDateProvider;
            releaseDateProvider = new HollowObjectCacheProvider(typeDataAccess, releaseDateTypeAPI, factory, previousCacheProvider);
        } else {
            releaseDateProvider = new HollowObjectFactoryProvider(typeDataAccess, releaseDateTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfReleaseDates");
        if(typeDataAccess != null) {
            listOfReleaseDatesTypeAPI = new ListOfReleaseDatesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfReleaseDatesTypeAPI = new ListOfReleaseDatesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfReleaseDates"));
        }
        addTypeAPI(listOfReleaseDatesTypeAPI);
        factory = factoryOverrides.get("ListOfReleaseDates");
        if(factory == null)
            factory = new ListOfReleaseDatesHollowFactory();
        if(cachedTypes.contains("ListOfReleaseDates")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfReleaseDatesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfReleaseDatesProvider;
            listOfReleaseDatesProvider = new HollowObjectCacheProvider(typeDataAccess, listOfReleaseDatesTypeAPI, factory, previousCacheProvider);
        } else {
            listOfReleaseDatesProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfReleaseDatesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDateWindow");
        if(typeDataAccess != null) {
            videoDateWindowTypeAPI = new VideoDateWindowTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoDateWindowTypeAPI = new VideoDateWindowTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoDateWindow"));
        }
        addTypeAPI(videoDateWindowTypeAPI);
        factory = factoryOverrides.get("VideoDateWindow");
        if(factory == null)
            factory = new VideoDateWindowHollowFactory();
        if(cachedTypes.contains("VideoDateWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDateWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDateWindowProvider;
            videoDateWindowProvider = new HollowObjectCacheProvider(typeDataAccess, videoDateWindowTypeAPI, factory, previousCacheProvider);
        } else {
            videoDateWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDateWindowTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDateWindowList");
        if(typeDataAccess != null) {
            videoDateWindowListTypeAPI = new VideoDateWindowListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoDateWindowListTypeAPI = new VideoDateWindowListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoDateWindowList"));
        }
        addTypeAPI(videoDateWindowListTypeAPI);
        factory = factoryOverrides.get("VideoDateWindowList");
        if(factory == null)
            factory = new VideoDateWindowListHollowFactory();
        if(cachedTypes.contains("VideoDateWindowList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDateWindowListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDateWindowListProvider;
            videoDateWindowListProvider = new HollowObjectCacheProvider(typeDataAccess, videoDateWindowListTypeAPI, factory, previousCacheProvider);
        } else {
            videoDateWindowListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDateWindowListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDate");
        if(typeDataAccess != null) {
            videoDateTypeAPI = new VideoDateTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoDateTypeAPI = new VideoDateTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoDate"));
        }
        addTypeAPI(videoDateTypeAPI);
        factory = factoryOverrides.get("VideoDate");
        if(factory == null)
            factory = new VideoDateHollowFactory();
        if(cachedTypes.contains("VideoDate")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDateProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDateProvider;
            videoDateProvider = new HollowObjectCacheProvider(typeDataAccess, videoDateTypeAPI, factory, previousCacheProvider);
        } else {
            videoDateProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDateTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(releaseDateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)releaseDateProvider).detach();
        if(listOfReleaseDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfReleaseDatesProvider).detach();
        if(videoDateWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDateWindowProvider).detach();
        if(videoDateWindowListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDateWindowListProvider).detach();
        if(videoDateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDateProvider).detach();
    }

    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public ReleaseDateTypeAPI getReleaseDateTypeAPI() {
        return releaseDateTypeAPI;
    }
    public ListOfReleaseDatesTypeAPI getListOfReleaseDatesTypeAPI() {
        return listOfReleaseDatesTypeAPI;
    }
    public VideoDateWindowTypeAPI getVideoDateWindowTypeAPI() {
        return videoDateWindowTypeAPI;
    }
    public VideoDateWindowListTypeAPI getVideoDateWindowListTypeAPI() {
        return videoDateWindowListTypeAPI;
    }
    public VideoDateTypeAPI getVideoDateTypeAPI() {
        return videoDateTypeAPI;
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
    public Collection<ReleaseDate> getAllReleaseDate() {
        return new AllHollowRecordCollection<ReleaseDate>(getDataAccess().getTypeDataAccess("ReleaseDate").getTypeState()) {
            protected ReleaseDate getForOrdinal(int ordinal) {
                return getReleaseDate(ordinal);
            }
        };
    }
    public ReleaseDate getReleaseDate(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (ReleaseDate)releaseDateProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfReleaseDates> getAllListOfReleaseDates() {
        return new AllHollowRecordCollection<ListOfReleaseDates>(getDataAccess().getTypeDataAccess("ListOfReleaseDates").getTypeState()) {
            protected ListOfReleaseDates getForOrdinal(int ordinal) {
                return getListOfReleaseDates(ordinal);
            }
        };
    }
    public ListOfReleaseDates getListOfReleaseDates(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (ListOfReleaseDates)listOfReleaseDatesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDateWindow> getAllVideoDateWindow() {
        return new AllHollowRecordCollection<VideoDateWindow>(getDataAccess().getTypeDataAccess("VideoDateWindow").getTypeState()) {
            protected VideoDateWindow getForOrdinal(int ordinal) {
                return getVideoDateWindow(ordinal);
            }
        };
    }
    public VideoDateWindow getVideoDateWindow(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (VideoDateWindow)videoDateWindowProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDateWindowList> getAllVideoDateWindowList() {
        return new AllHollowRecordCollection<VideoDateWindowList>(getDataAccess().getTypeDataAccess("VideoDateWindowList").getTypeState()) {
            protected VideoDateWindowList getForOrdinal(int ordinal) {
                return getVideoDateWindowList(ordinal);
            }
        };
    }
    public VideoDateWindowList getVideoDateWindowList(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (VideoDateWindowList)videoDateWindowListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDate> getAllVideoDate() {
        return new AllHollowRecordCollection<VideoDate>(getDataAccess().getTypeDataAccess("VideoDate").getTypeState()) {
            protected VideoDate getForOrdinal(int ordinal) {
                return getVideoDate(ordinal);
            }
        };
    }
    public VideoDate getVideoDate(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (VideoDate)videoDateProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
