package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class VideoGeneralAliasHollow extends HollowObject {

    public VideoGeneralAliasHollow(VideoGeneralAliasDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralAliasTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoGeneralAliasDelegate delegate() {
        return (VideoGeneralAliasDelegate)delegate;
    }

}