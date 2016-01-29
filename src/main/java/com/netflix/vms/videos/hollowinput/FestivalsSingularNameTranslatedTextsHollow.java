package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class FestivalsSingularNameTranslatedTextsHollow extends HollowObject {

    public FestivalsSingularNameTranslatedTextsHollow(FestivalsSingularNameTranslatedTextsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public FestivalsSingularNameTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FestivalsSingularNameTranslatedTextsDelegate delegate() {
        return (FestivalsSingularNameTranslatedTextsDelegate)delegate;
    }

}