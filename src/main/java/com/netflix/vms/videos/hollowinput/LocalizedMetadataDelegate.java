package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface LocalizedMetadataDelegate extends HollowObjectDelegate {

    public int getTranslatedTextsOrdinal(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getAttributeNameOrdinal(int ordinal);

    public int getLabelOrdinal(int ordinal);

    public LocalizedMetadataTypeAPI getTypeAPI();

}