package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PersonArtworkAttributesDelegate extends HollowObjectDelegate {

    public int getFile_seqOrdinal(int ordinal);

    public PersonArtworkAttributesTypeAPI getTypeAPI();

}