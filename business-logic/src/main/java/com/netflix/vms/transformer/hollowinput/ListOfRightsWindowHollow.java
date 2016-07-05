package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ListOfRightsWindowHollow extends HollowList<RightsWindowHollow> {

    public ListOfRightsWindowHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RightsWindowHollow instantiateElement(int ordinal) {
        return (RightsWindowHollow) api().getRightsWindowHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ListOfRightsWindowTypeAPI typeApi() {
        return (ListOfRightsWindowTypeAPI) delegate.getTypeAPI();
    }

}