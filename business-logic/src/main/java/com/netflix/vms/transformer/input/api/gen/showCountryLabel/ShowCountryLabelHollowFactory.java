package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class ShowCountryLabelHollowFactory<T extends ShowCountryLabel> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ShowCountryLabel(((ShowCountryLabelTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ShowCountryLabel(new ShowCountryLabelDelegateCachedImpl((ShowCountryLabelTypeAPI)typeAPI, ordinal), ordinal);
    }

}