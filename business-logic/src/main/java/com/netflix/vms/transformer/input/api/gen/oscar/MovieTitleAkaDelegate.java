package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieTitleAkaDelegate extends HollowObjectDelegate {

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public String getAlias(int ordinal);

    public boolean isAliasEqual(int ordinal, String testValue);

    public int getAliasOrdinal(int ordinal);

    public String getBcpCode(int ordinal);

    public boolean isBcpCodeEqual(int ordinal, String testValue);

    public int getBcpCodeOrdinal(int ordinal);

    public String getSourceType(int ordinal);

    public boolean isSourceTypeEqual(int ordinal, String testValue);

    public int getSourceTypeOrdinal(int ordinal);

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

    public MovieTitleAkaTypeAPI getTypeAPI();

}