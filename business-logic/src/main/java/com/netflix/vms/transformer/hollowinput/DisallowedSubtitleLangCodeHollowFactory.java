package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

@SuppressWarnings("all")
public class DisallowedSubtitleLangCodeHollowFactory<T extends DisallowedSubtitleLangCodeHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new DisallowedSubtitleLangCodeHollow(((DisallowedSubtitleLangCodeTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new DisallowedSubtitleLangCodeHollow(new DisallowedSubtitleLangCodeDelegateCachedImpl((DisallowedSubtitleLangCodeTypeAPI)typeAPI, ordinal), ordinal);
    }

}