package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class CinderCupTokenRecordHollow extends HollowObject {

    public CinderCupTokenRecordHollow(CinderCupTokenRecordDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public LongHollow _getMovieId() {
        int refOrdinal = delegate().getMovieIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getLongHollow(refOrdinal);
    }

    public LongHollow _getContractId() {
        int refOrdinal = delegate().getContractIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getLongHollow(refOrdinal);
    }

    public StringHollow _getCupTokenId() {
        int refOrdinal = delegate().getCupTokenIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CinderCupTokenRecordTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CinderCupTokenRecordDelegate delegate() {
        return (CinderCupTokenRecordDelegate)delegate;
    }

}