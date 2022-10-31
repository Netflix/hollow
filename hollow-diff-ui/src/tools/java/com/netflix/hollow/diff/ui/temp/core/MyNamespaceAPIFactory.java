package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class MyNamespaceAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public MyNamespaceAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public MyNamespaceAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new MyNamespaceAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof MyNamespaceAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of MyNamespaceAPI");        }
        return new MyNamespaceAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (MyNamespaceAPI) previousCycleAPI);
    }

}