package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class ShowCountryLabelAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public ShowCountryLabelAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public ShowCountryLabelAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new ShowCountryLabelAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof ShowCountryLabelAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of ShowCountryLabelAPI");        }
        return new ShowCountryLabelAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (ShowCountryLabelAPI) previousCycleAPI);
    }

}