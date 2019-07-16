package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutDelegate extends HollowObjectDelegate {

    public long getRolloutId(int ordinal);

    public Long getRolloutIdBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public String getRolloutName(int ordinal);

    public boolean isRolloutNameEqual(int ordinal, String testValue);

    public int getRolloutNameOrdinal(int ordinal);

    public String getType(int ordinal);

    public boolean isTypeEqual(int ordinal, String testValue);

    public int getTypeOrdinal(int ordinal);

    public String getStatus(int ordinal);

    public boolean isStatusEqual(int ordinal, String testValue);

    public int getStatusOrdinal(int ordinal);

    public int getPhasesOrdinal(int ordinal);

    public int getCountriesOrdinal(int ordinal);

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

    public RolloutTypeAPI getTypeAPI();

}