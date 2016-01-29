package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsRightsHollow extends HollowObject {

    public VideoRightsRightsHollow(VideoRightsRightsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public VideoRightsRightsArrayOfWindowsHollow _getWindows() {
        int refOrdinal = delegate().getWindowsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsRightsArrayOfWindowsHollow(refOrdinal);
    }

    public VideoRightsRightsArrayOfContractsHollow _getContracts() {
        int refOrdinal = delegate().getContractsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsRightsArrayOfContractsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsRightsDelegate delegate() {
        return (VideoRightsRightsDelegate)delegate;
    }

}