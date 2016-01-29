package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

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

    public StreamProfileGroupsArrayOfStreamProfileIdsHollow _getStreamProfileIds() {
        int refOrdinal = delegate().getStreamProfileIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamProfileGroupsArrayOfStreamProfileIdsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamProfileGroupsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamProfileGroupsDelegate delegate() {
        return (StreamProfileGroupsDelegate)delegate;
    }

}