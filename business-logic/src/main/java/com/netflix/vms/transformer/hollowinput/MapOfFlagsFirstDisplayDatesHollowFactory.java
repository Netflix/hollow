package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowMapCachedDelegate;

@SuppressWarnings("all")
public class MapOfFlagsFirstDisplayDatesHollowFactory<T extends MapOfFlagsFirstDisplayDatesHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MapOfFlagsFirstDisplayDatesHollow(((MapOfFlagsFirstDisplayDatesTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MapOfFlagsFirstDisplayDatesHollow(new HollowMapCachedDelegate((MapOfFlagsFirstDisplayDatesTypeAPI)typeAPI, ordinal), ordinal);
    }

}