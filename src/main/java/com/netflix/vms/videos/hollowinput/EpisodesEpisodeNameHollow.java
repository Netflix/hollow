package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class EpisodesEpisodeNameHollow extends HollowObject {

    public EpisodesEpisodeNameHollow(EpisodesEpisodeNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public EpisodesEpisodeNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getEpisodesEpisodeNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public EpisodesEpisodeNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected EpisodesEpisodeNameDelegate delegate() {
        return (EpisodesEpisodeNameDelegate)delegate;
    }

}