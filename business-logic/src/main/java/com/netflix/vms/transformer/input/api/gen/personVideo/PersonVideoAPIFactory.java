package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class PersonVideoAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public PersonVideoAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public PersonVideoAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new PersonVideoAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof PersonVideoAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of PersonVideoAPI");        }
        return new PersonVideoAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (PersonVideoAPI) previousCycleAPI);
    }

}