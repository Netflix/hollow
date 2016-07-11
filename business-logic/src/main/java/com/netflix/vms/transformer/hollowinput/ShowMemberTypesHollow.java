package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
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

    public TranslatedTextHollow _getDisplayName() {
        int refOrdinal = delegate().getDisplayNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ShowMemberTypesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ShowMemberTypesDelegate delegate() {
        return (ShowMemberTypesDelegate)delegate;
    }

}