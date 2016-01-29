package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ShowMemberTypesHollow extends HollowObject {

    public ShowMemberTypesHollow(ShowMemberTypesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getShowMemberTypeId() {
        return delegate().getShowMemberTypeId(ordinal);
    }

    public Long _getShowMemberTypeIdBoxed() {
        return delegate().getShowMemberTypeIdBoxed(ordinal);
    }

    public ShowMemberTypesDisplayNameHollow _getDisplayName() {
        int refOrdinal = delegate().getDisplayNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getShowMemberTypesDisplayNameHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ShowMemberTypesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ShowMemberTypesDelegate delegate() {
        return (ShowMemberTypesDelegate)delegate;
    }

}