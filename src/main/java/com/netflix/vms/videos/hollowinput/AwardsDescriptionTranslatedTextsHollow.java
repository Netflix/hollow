package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AwardsDescriptionTranslatedTextsHollow extends HollowObject {

    public AwardsDescriptionTranslatedTextsHollow(AwardsDescriptionTranslatedTextsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AwardsDescriptionTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AwardsDescriptionTranslatedTextsDelegate delegate() {
        return (AwardsDescriptionTranslatedTextsDelegate)delegate;
    }

}