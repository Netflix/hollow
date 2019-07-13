package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class RolloutAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public RolloutAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public RolloutAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new RolloutAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof RolloutAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of RolloutAPI");        }
        return new RolloutAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (RolloutAPI) previousCycleAPI);
    }

}