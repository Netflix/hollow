package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class SupplementalAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public SupplementalAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public SupplementalAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new SupplementalAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof SupplementalAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of SupplementalAPI");        }
        return new SupplementalAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (SupplementalAPI) previousCycleAPI);
    }

}