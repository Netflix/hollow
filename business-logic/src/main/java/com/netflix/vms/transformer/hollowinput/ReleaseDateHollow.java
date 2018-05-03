package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class ReleaseDateHollow extends HollowObject {

    public ReleaseDateHollow(ReleaseDateDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getReleaseDateType() {
        int refOrdinal = delegate().getReleaseDateTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getDistributorName() {
        int refOrdinal = delegate().getDistributorNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public int _getMonth() {
        return delegate().getMonth(ordinal);
    }

    public Integer _getMonthBoxed() {
        return delegate().getMonthBoxed(ordinal);
    }

    public int _getYear() {
        return delegate().getYear(ordinal);
    }

    public Integer _getYearBoxed() {
        return delegate().getYearBoxed(ordinal);
    }

    public int _getDay() {
        return delegate().getDay(ordinal);
    }

    public Integer _getDayBoxed() {
        return delegate().getDayBoxed(ordinal);
    }

    public StringHollow _getBcp47code() {
        int refOrdinal = delegate().getBcp47codeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ReleaseDateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ReleaseDateDelegate delegate() {
        return (ReleaseDateDelegate)delegate;
    }

}