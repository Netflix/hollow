package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class MclEarliestDateAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public MclEarliestDateAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public MclEarliestDateAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new MclEarliestDateAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof MclEarliestDateAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of MclEarliestDateAPI");        }
        return new MclEarliestDateAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (MclEarliestDateAPI) previousCycleAPI);
    }

}