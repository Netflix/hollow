package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesHooksHollow extends HollowObject {

    public Stories_SynopsesHooksHollow(Stories_SynopsesHooksDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Stories_SynopsesHooksMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStories_SynopsesHooksMapOfTranslatedTextsHollow(refOrdinal);
    }

    public StringHollow _getRank() {
        int refOrdinal = delegate().getRankOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getType() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public Stories_SynopsesHooksTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected Stories_SynopsesHooksDelegate delegate() {
        return (Stories_SynopsesHooksDelegate)delegate;
    }

}