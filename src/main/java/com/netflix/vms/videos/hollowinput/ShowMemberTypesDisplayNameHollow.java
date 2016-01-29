package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ShowMemberTypesDisplayNameHollow extends HollowObject {

    public ShowMemberTypesDisplayNameHollow(ShowMemberTypesDisplayNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ShowMemberTypesDisplayNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getShowMemberTypesDisplayNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ShowMemberTypesDisplayNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ShowMemberTypesDisplayNameDelegate delegate() {
        return (ShowMemberTypesDisplayNameDelegate)delegate;
    }

}