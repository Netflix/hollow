package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface LocalizedMetadataDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public String getAttributeName(int ordinal);

    public boolean isAttributeNameEqual(int ordinal, String testValue);

    public int getAttributeNameOrdinal(int ordinal);

    public String getLabel(int ordinal);

    public boolean isLabelEqual(int ordinal, String testValue);

    public int getLabelOrdinal(int ordinal);

    public int getTranslatedTextsOrdinal(int ordinal);

    public LocalizedMetadataTypeAPI getTypeAPI();

}