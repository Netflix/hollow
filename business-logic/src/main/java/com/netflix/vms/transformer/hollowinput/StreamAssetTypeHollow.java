package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamAssetTypeHollow extends HollowObject {

    public StreamAssetTypeHollow(StreamAssetTypeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getAssetTypeId() {
        return delegate().getAssetTypeId(ordinal);
    }

    public Long _getAssetTypeIdBoxed() {
        return delegate().getAssetTypeIdBoxed(ordinal);
    }

    public StringHollow _getAssetType() {
        int refOrdinal = delegate().getAssetTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamAssetTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamAssetTypeDelegate delegate() {
        return (StreamAssetTypeDelegate)delegate;
    }

}