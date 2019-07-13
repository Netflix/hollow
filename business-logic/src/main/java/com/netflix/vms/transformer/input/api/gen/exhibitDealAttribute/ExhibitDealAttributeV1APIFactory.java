package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class ExhibitDealAttributeV1APIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public ExhibitDealAttributeV1APIFactory() {
        this(Collections.<String>emptySet());
    }

    public ExhibitDealAttributeV1APIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new ExhibitDealAttributeV1API(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof ExhibitDealAttributeV1API)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of ExhibitDealAttributeV1API");        }
        return new ExhibitDealAttributeV1API(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (ExhibitDealAttributeV1API) previousCycleAPI);
    }

}