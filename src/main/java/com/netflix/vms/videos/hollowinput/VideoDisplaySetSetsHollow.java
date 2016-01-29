package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoDisplaySetSetsHollow extends HollowObject {

    public VideoDisplaySetSetsHollow(VideoDisplaySetSetsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public VideoDisplaySetSetsArrayOfChildrenHollow _getChildren() {
        int refOrdinal = delegate().getChildrenOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoDisplaySetSetsArrayOfChildrenHollow(refOrdinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getSetType() {
        int refOrdinal = delegate().getSetTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDisplaySetSetsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoDisplaySetSetsDelegate delegate() {
        return (VideoDisplaySetSetsDelegate)delegate;
    }

}