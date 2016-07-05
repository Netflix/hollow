package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PassthroughDataHollow extends HollowObject {

    public PassthroughDataHollow(PassthroughDataDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public SingleValuePassthroughMapHollow _getSingleValues() {
        int refOrdinal = delegate().getSingleValuesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSingleValuePassthroughMapHollow(refOrdinal);
    }

    public MultiValuePassthroughMapHollow _getMultiValues() {
        int refOrdinal = delegate().getMultiValuesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMultiValuePassthroughMapHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PassthroughDataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PassthroughDataDelegate delegate() {
        return (PassthroughDataDelegate)delegate;
    }

}