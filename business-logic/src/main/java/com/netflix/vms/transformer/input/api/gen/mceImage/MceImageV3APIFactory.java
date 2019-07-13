package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class MceImageV3APIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public MceImageV3APIFactory() {
        this(Collections.<String>emptySet());
    }

    public MceImageV3APIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new MceImageV3API(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof MceImageV3API)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of MceImageV3API");        }
        return new MceImageV3API(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (MceImageV3API) previousCycleAPI);
    }

}