package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesNarrativeTextHollow extends HollowObject {

    public Stories_SynopsesNarrativeTextHollow(Stories_SynopsesNarrativeTextDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Stories_SynopsesNarrativeTextMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStories_SynopsesNarrativeTextMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public Stories_SynopsesNarrativeTextTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected Stories_SynopsesNarrativeTextDelegate delegate() {
        return (Stories_SynopsesNarrativeTextDelegate)delegate;
    }

}