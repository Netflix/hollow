package com.netflix.vms.transformer.input.api.gen.cupToken;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class CupTokenAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public CupTokenAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public CupTokenAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new CupTokenAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof CupTokenAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of CupTokenAPI");        }
        return new CupTokenAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (CupTokenAPI) previousCycleAPI);
    }

}