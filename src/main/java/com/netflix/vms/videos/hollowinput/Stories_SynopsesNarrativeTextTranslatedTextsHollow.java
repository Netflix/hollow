package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesNarrativeTextTranslatedTextsHollow extends HollowObject {

    public Stories_SynopsesNarrativeTextTranslatedTextsHollow(Stories_SynopsesNarrativeTextTranslatedTextsDelegate delegate, int ordinal) {
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

    public Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected Stories_SynopsesNarrativeTextTranslatedTextsDelegate delegate() {
        return (Stories_SynopsesNarrativeTextTranslatedTextsDelegate)delegate;
    }

}