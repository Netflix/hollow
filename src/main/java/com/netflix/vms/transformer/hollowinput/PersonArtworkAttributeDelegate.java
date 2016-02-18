package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PersonArtworkAttributeDelegate extends HollowObjectDelegate {

    public int getFile_seqOrdinal(int ordinal);

    public PersonArtworkAttributeTypeAPI getTypeAPI();

}