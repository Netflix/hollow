package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class VideoTypeAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public VideoTypeAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public VideoTypeAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new VideoTypeAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof VideoTypeAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of VideoTypeAPI");        }
        return new VideoTypeAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (VideoTypeAPI) previousCycleAPI);
    }

}