package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class Gk2StatusAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public Gk2StatusAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public Gk2StatusAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new Gk2StatusAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof Gk2StatusAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of Gk2StatusAPI");        }
        return new Gk2StatusAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (Gk2StatusAPI) previousCycleAPI);
    }

}