package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class VideoAwardAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public VideoAwardAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public VideoAwardAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new VideoAwardAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof VideoAwardAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of VideoAwardAPI");        }
        return new VideoAwardAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (VideoAwardAPI) previousCycleAPI);
    }

}