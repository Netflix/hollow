package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class PackageDealCountryAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public PackageDealCountryAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public PackageDealCountryAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new PackageDealCountryAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof PackageDealCountryAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of PackageDealCountryAPI");        }
        return new PackageDealCountryAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (PackageDealCountryAPI) previousCycleAPI);
    }

}