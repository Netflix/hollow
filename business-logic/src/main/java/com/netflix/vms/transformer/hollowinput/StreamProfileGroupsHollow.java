package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class StreamProfileGroupsHollow extends HollowObject {

    public StreamProfileGroupsHollow(StreamProfileGroupsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getGroupName() {
        int refOrdinal = delegate().getGroupNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StreamProfileIdListHollow _getStreamProfileIds() {
        int refOrdinal = delegate().getStreamProfileIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamProfileIdListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamProfileGroupsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamProfileGroupsDelegate delegate() {
        return (StreamProfileGroupsDelegate)delegate;
    }

}