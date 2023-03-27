package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class AwardsAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public AwardsAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public AwardsAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new AwardsAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof AwardsAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of AwardsAPI");        }
        return new AwardsAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (AwardsAPI) previousCycleAPI);
    }

}