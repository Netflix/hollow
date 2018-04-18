package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;

@SuppressWarnings("all")
public class CinderCupTokenRecordHollowFactory<T extends CinderCupTokenRecordHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new CinderCupTokenRecordHollow(((CinderCupTokenRecordTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new CinderCupTokenRecordHollow(new CinderCupTokenRecordDelegateCachedImpl((CinderCupTokenRecordTypeAPI)typeAPI, ordinal), ordinal);
    }

}