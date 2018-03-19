package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class StoriesSynopsesHookHollow extends HollowObject {

    public StoriesSynopsesHookHollow(StoriesSynopsesHookDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getType() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getRank() {
        int refOrdinal = delegate().getRankOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public MapOfTranslatedTextHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMapOfTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StoriesSynopsesHookTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StoriesSynopsesHookDelegate delegate() {
        return (StoriesSynopsesHookDelegate)delegate;
    }

}