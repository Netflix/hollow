package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VmsAttributeFeedEntryDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public long getDealId(int ordinal);

    public Long getDealIdBoxed(int ordinal);

    public int getDealIdOrdinal(int ordinal);

    public String getCountryCode(int ordinal);

    public boolean isCountryCodeEqual(int ordinal, String testValue);

    public int getCountryCodeOrdinal(int ordinal);

    public boolean getDayAfterBroadcast(int ordinal);

    public Boolean getDayAfterBroadcastBoxed(int ordinal);

    public int getDayAfterBroadcastOrdinal(int ordinal);

    public boolean getDayOfBroadcast(int ordinal);

    public Boolean getDayOfBroadcastBoxed(int ordinal);

    public int getDayOfBroadcastOrdinal(int ordinal);

    public long getPrePromotionDays(int ordinal);

    public Long getPrePromotionDaysBoxed(int ordinal);

    public int getPrePromotionDaysOrdinal(int ordinal);

    public int getDisallowedAssetBundlesOrdinal(int ordinal);

    public VmsAttributeFeedEntryTypeAPI getTypeAPI();

}