package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class TrailerTrailersThemesHollow extends HollowObject {

    public TrailerTrailersThemesHollow(TrailerTrailersThemesDelegate delegate, int ordinal) {
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

    public TrailerTrailersThemesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TrailerTrailersThemesDelegate delegate() {
        return (TrailerTrailersThemesDelegate)delegate;
    }

}