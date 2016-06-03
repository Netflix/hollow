package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class LocaleTerritoryCodeListHollow extends HollowList<LocaleTerritoryCodeHollow> {

    public LocaleTerritoryCodeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public LocaleTerritoryCodeHollow instantiateElement(int ordinal) {
        return (LocaleTerritoryCodeHollow) api().getLocaleTerritoryCodeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public LocaleTerritoryCodeListTypeAPI typeApi() {
        return (LocaleTerritoryCodeListTypeAPI) delegate.getTypeAPI();
    }

}