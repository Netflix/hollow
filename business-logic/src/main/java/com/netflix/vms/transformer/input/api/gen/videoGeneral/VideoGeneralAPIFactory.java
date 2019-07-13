package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class VideoGeneralAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public VideoGeneralAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public VideoGeneralAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new VideoGeneralAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof VideoGeneralAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of VideoGeneralAPI");        }
        return new VideoGeneralAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (VideoGeneralAPI) previousCycleAPI);
    }

}