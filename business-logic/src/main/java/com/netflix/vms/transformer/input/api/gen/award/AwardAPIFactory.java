package com.netflix.vms.transformer.input.api.gen.award;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class AwardAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public AwardAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public AwardAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new AwardAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof AwardAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of AwardAPI");        }
        return new AwardAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (AwardAPI) previousCycleAPI);
    }

}