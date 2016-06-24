package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoDateWindowHollow extends HollowObject {

    public VideoDateWindowHollow(VideoDateWindowDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public ListOfReleaseDatesHollow _getReleaseDates() {
        int refOrdinal = delegate().getReleaseDatesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfReleaseDatesHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDateWindowTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoDateWindowDelegate delegate() {
        return (VideoDateWindowDelegate)delegate;
    }

}