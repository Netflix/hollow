package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

public class VMSHollowInputAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public VMSHollowInputAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public VMSHollowInputAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new VMSHollowInputAPI(dataAccess);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        return new VMSHollowInputAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (VMSHollowInputAPI) previousCycleAPI);
    }

}