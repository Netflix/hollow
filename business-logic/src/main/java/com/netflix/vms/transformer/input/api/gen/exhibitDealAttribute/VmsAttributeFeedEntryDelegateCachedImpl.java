package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VmsAttributeFeedEntryDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VmsAttributeFeedEntryDelegate {

    private final Long movieId;
    private final int movieIdOrdinal;
    private final Long dealId;
    private final int dealIdOrdinal;
    private final String countryCode;
    private final int countryCodeOrdinal;
    private final Boolean dayAfterBroadcast;
    private final int dayAfterBroadcastOrdinal;
    private final Boolean dayOfBroadcast;
    private final int dayOfBroadcastOrdinal;
    private final Long prePromotionDays;
    private final int prePromotionDaysOrdinal;
    private final int disallowedAssetBundlesOrdinal;
    private VmsAttributeFeedEntryTypeAPI typeAPI;

    public VmsAttributeFeedEntryDelegateCachedImpl(VmsAttributeFeedEntryTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValue(movieIdTempOrdinal);
        this.dealIdOrdinal = typeAPI.getDealIdOrdinal(ordinal);
        int dealIdTempOrdinal = dealIdOrdinal;
        this.dealId = dealIdTempOrdinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValue(dealIdTempOrdinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        int countryCodeTempOrdinal = countryCodeOrdinal;
        this.countryCode = countryCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(countryCodeTempOrdinal);
        this.dayAfterBroadcastOrdinal = typeAPI.getDayAfterBroadcastOrdinal(ordinal);
        int dayAfterBroadcastTempOrdinal = dayAfterBroadcastOrdinal;
        this.dayAfterBroadcast = dayAfterBroadcastTempOrdinal == -1 ? null : typeAPI.getAPI().getBooleanTypeAPI().getValue(dayAfterBroadcastTempOrdinal);
        this.dayOfBroadcastOrdinal = typeAPI.getDayOfBroadcastOrdinal(ordinal);
        int dayOfBroadcastTempOrdinal = dayOfBroadcastOrdinal;
        this.dayOfBroadcast = dayOfBroadcastTempOrdinal == -1 ? null : typeAPI.getAPI().getBooleanTypeAPI().getValue(dayOfBroadcastTempOrdinal);
        this.prePromotionDaysOrdinal = typeAPI.getPrePromotionDaysOrdinal(ordinal);
        int prePromotionDaysTempOrdinal = prePromotionDaysOrdinal;
        this.prePromotionDays = prePromotionDaysTempOrdinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValue(prePromotionDaysTempOrdinal);
        this.disallowedAssetBundlesOrdinal = typeAPI.getDisallowedAssetBundlesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getMovieIdOrdinal(int ordinal) {
        return movieIdOrdinal;
    }

    public long getDealId(int ordinal) {
        if(dealId == null)
            return Long.MIN_VALUE;
        return dealId.longValue();
    }

    public Long getDealIdBoxed(int ordinal) {
        return dealId;
    }

    public int getDealIdOrdinal(int ordinal) {
        return dealIdOrdinal;
    }

    public String getCountryCode(int ordinal) {
        return countryCode;
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return countryCode == null;
        return testValue.equals(countryCode);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public boolean getDayAfterBroadcast(int ordinal) {
        if(dayAfterBroadcast == null)
            return false;
        return dayAfterBroadcast.booleanValue();
    }

    public Boolean getDayAfterBroadcastBoxed(int ordinal) {
        return dayAfterBroadcast;
    }

    public int getDayAfterBroadcastOrdinal(int ordinal) {
        return dayAfterBroadcastOrdinal;
    }

    public boolean getDayOfBroadcast(int ordinal) {
        if(dayOfBroadcast == null)
            return false;
        return dayOfBroadcast.booleanValue();
    }

    public Boolean getDayOfBroadcastBoxed(int ordinal) {
        return dayOfBroadcast;
    }

    public int getDayOfBroadcastOrdinal(int ordinal) {
        return dayOfBroadcastOrdinal;
    }

    public long getPrePromotionDays(int ordinal) {
        if(prePromotionDays == null)
            return Long.MIN_VALUE;
        return prePromotionDays.longValue();
    }

    public Long getPrePromotionDaysBoxed(int ordinal) {
        return prePromotionDays;
    }

    public int getPrePromotionDaysOrdinal(int ordinal) {
        return prePromotionDaysOrdinal;
    }

    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        return disallowedAssetBundlesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VmsAttributeFeedEntryTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VmsAttributeFeedEntryTypeAPI) typeAPI;
    }

}