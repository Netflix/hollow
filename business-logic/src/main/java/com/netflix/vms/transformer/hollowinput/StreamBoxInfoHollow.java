package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class StreamBoxInfoHollow extends HollowObject {

    public StreamBoxInfoHollow(StreamBoxInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int _getBoxOffset() {
        return delegate().getBoxOffset(ordinal);
    }

    public Integer _getBoxOffsetBoxed() {
        return delegate().getBoxOffsetBoxed(ordinal);
    }

    public int _getBoxSize() {
        return delegate().getBoxSize(ordinal);
    }

    public Integer _getBoxSizeBoxed() {
        return delegate().getBoxSizeBoxed(ordinal);
    }

    public StreamBoxInfoKeyHollow _getKey() {
        int refOrdinal = delegate().getKeyOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamBoxInfoKeyHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamBoxInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamBoxInfoDelegate delegate() {
        return (StreamBoxInfoDelegate)delegate;
    }

}