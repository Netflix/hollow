package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoGeneralEpisodeTypesHollow extends HollowObject {

    public VideoGeneralEpisodeTypesHollow(VideoGeneralEpisodeTypesDelegate delegate, int ordinal) {
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

    public VideoGeneralEpisodeTypesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoGeneralEpisodeTypesDelegate delegate() {
        return (VideoGeneralEpisodeTypesDelegate)delegate;
    }

}