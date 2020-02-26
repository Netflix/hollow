package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class TopNAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public TopNAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public TopNAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new TopNAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof TopNAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of TopNAPI");        }
        return new TopNAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (TopNAPI) previousCycleAPI);
    }

}