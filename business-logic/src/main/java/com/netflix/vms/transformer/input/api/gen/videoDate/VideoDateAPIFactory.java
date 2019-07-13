package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class VideoDateAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public VideoDateAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public VideoDateAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new VideoDateAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof VideoDateAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of VideoDateAPI");        }
        return new VideoDateAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (VideoDateAPI) previousCycleAPI);
    }

}