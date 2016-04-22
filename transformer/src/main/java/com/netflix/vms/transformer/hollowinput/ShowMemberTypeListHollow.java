package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ShowMemberTypeListHollow extends HollowList<ShowMemberTypeHollow> {

    public ShowMemberTypeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ShowMemberTypeHollow instantiateElement(int ordinal) {
        return (ShowMemberTypeHollow) api().getShowMemberTypeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ShowMemberTypeListTypeAPI typeApi() {
        return (ShowMemberTypeListTypeAPI) delegate.getTypeAPI();
    }

}