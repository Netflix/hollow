package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class OscarAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public OscarAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public OscarAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new OscarAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof OscarAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of OscarAPI");        }
        return new OscarAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (OscarAPI) previousCycleAPI);
    }

}