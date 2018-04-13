package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ShowMemberTypeListHollow extends HollowList<ShowMemberTypeHollow> {

    public ShowMemberTypeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
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