package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class VmsAttributeFeedEntryHollowFactory<T extends VmsAttributeFeedEntry> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VmsAttributeFeedEntry(((VmsAttributeFeedEntryTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VmsAttributeFeedEntry(new VmsAttributeFeedEntryDelegateCachedImpl((VmsAttributeFeedEntryTypeAPI)typeAPI, ordinal), ordinal);
    }

}