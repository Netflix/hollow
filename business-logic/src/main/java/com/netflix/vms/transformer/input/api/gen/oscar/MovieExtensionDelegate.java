package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieExtensionDelegate extends HollowObjectDelegate {

    public long getMovieExtensionId(int ordinal);

    public Long getMovieExtensionIdBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public String getAttributeName(int ordinal);

    public boolean isAttributeNameEqual(int ordinal, String testValue);

    public int getAttributeNameOrdinal(int ordinal);

    public String getAttributeValue(int ordinal);

    public boolean isAttributeValueEqual(int ordinal, String testValue);

    public int getAttributeValueOrdinal(int ordinal);

    public int getOverridesOrdinal(int ordinal);

    public long getDateCreated(int ordinal);

    public Long getDateCreatedBoxed(int ordinal);

    public int getDateCreatedOrdinal(int ordinal);

    public long getLastUpdated(int ordinal);

    public Long getLastUpdatedBoxed(int ordinal);

    public int getLastUpdatedOrdinal(int ordinal);

    public String getCreatedBy(int ordinal);

    public boolean isCreatedByEqual(int ordinal, String testValue);

    public int getCreatedByOrdinal(int ordinal);

    public String getUpdatedBy(int ordinal);

    public boolean isUpdatedByEqual(int ordinal, String testValue);

    public int getUpdatedByOrdinal(int ordinal);

    public MovieExtensionTypeAPI getTypeAPI();

}