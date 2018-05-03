package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class ContractsHollow extends HollowObject {

    public ContractsHollow(ContractsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public ListOfContractHollow _getContracts() {
        int refOrdinal = delegate().getContractsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfContractHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ContractsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ContractsDelegate delegate() {
        return (ContractsDelegate)delegate;
    }

}