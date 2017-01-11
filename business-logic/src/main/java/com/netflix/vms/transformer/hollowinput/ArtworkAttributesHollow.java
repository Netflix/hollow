package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtworkAttributesHollow extends HollowObject {

    public ArtworkAttributesHollow(ArtworkAttributesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public PassthroughDataHollow _getPassthrough() {
        int refOrdinal = delegate().getPassthroughOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPassthroughDataHollow(refOrdinal);
    }

    public String _getROLLOUT_EXCLUSIVE() {
        return delegate().getROLLOUT_EXCLUSIVE(ordinal);
    }

    public boolean _isROLLOUT_EXCLUSIVEEqual(String testValue) {
        return delegate().isROLLOUT_EXCLUSIVEEqual(ordinal, testValue);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ArtworkAttributesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ArtworkAttributesDelegate delegate() {
        return (ArtworkAttributesDelegate)delegate;
    }

}