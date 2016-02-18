package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkAttributesHollow extends HollowObject {

    public VideoArtWorkAttributesHollow(VideoArtWorkAttributesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
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

    public VideoArtWorkAttributesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoArtWorkAttributesDelegate delegate() {
        return (VideoArtWorkAttributesDelegate)delegate;
    }

}