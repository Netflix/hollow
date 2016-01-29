package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkRecipesHollow extends HollowObject {

    public VideoArtWorkRecipesHollow(VideoArtWorkRecipesDelegate delegate, int ordinal) {
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

    public VideoArtWorkRecipesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoArtWorkRecipesDelegate delegate() {
        return (VideoArtWorkRecipesDelegate)delegate;
    }

}