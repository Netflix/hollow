package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class LocaleTerritoryCodeListHollow extends HollowList<LocaleTerritoryCodeHollow> {

    public LocaleTerritoryCodeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
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