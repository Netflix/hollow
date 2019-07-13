package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class LocalizedMetaDataAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public LocalizedMetaDataAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public LocalizedMetaDataAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new LocalizedMetaDataAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof LocalizedMetaDataAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of LocalizedMetaDataAPI");        }
        return new LocalizedMetaDataAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (LocalizedMetaDataAPI) previousCycleAPI);
    }

}