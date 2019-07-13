package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VmsAttributeFeedEntryDelegateLookupImpl extends HollowObjectAbstractDelegate implements VmsAttributeFeedEntryDelegate {

    private final VmsAttributeFeedEntryTypeAPI typeAPI;

    public VmsAttributeFeedEntryDelegateLookupImpl(VmsAttributeFeedEntryTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getLongTypeAPI().getValue(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValueBoxed(ordinal);
    }

    public int getMovieIdOrdinal(int ordinal) {
        return typeAPI.getMovieIdOrdinal(ordinal);
    }

    public long getDealId(int ordinal) {
        ordinal = typeAPI.getDealIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getLongTypeAPI().getValue(ordinal);
    }

    public Long getDealIdBoxed(int ordinal) {
        ordinal = typeAPI.getDealIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValueBoxed(ordinal);
    }

    public int getDealIdOrdinal(int ordinal) {
        return typeAPI.getDealIdOrdinal(ordinal);
    }

    public String getCountryCode(int ordinal) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public boolean getDayAfterBroadcast(int ordinal) {
        ordinal = typeAPI.getDayAfterBroadcastOrdinal(ordinal);
        return ordinal == -1 ? false : typeAPI.getAPI().getBooleanTypeAPI().getValue(ordinal);
    }

    public Boolean getDayAfterBroadcastBoxed(int ordinal) {
        ordinal = typeAPI.getDayAfterBroadcastOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBooleanTypeAPI().getValueBoxed(ordinal);
    }

    public int getDayAfterBroadcastOrdinal(int ordinal) {
        return typeAPI.getDayAfterBroadcastOrdinal(ordinal);
    }

    public boolean getDayOfBroadcast(int ordinal) {
        ordinal = typeAPI.getDayOfBroadcastOrdinal(ordinal);
        return ordinal == -1 ? false : typeAPI.getAPI().getBooleanTypeAPI().getValue(ordinal);
    }

    public Boolean getDayOfBroadcastBoxed(int ordinal) {
        ordinal = typeAPI.getDayOfBroadcastOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBooleanTypeAPI().getValueBoxed(ordinal);
    }

    public int getDayOfBroadcastOrdinal(int ordinal) {
        return typeAPI.getDayOfBroadcastOrdinal(ordinal);
    }

    public long getPrePromotionDays(int ordinal) {
        ordinal = typeAPI.getPrePromotionDaysOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getLongTypeAPI().getValue(ordinal);
    }

    public Long getPrePromotionDaysBoxed(int ordinal) {
        ordinal = typeAPI.getPrePromotionDaysOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValueBoxed(ordinal);
    }

    public int getPrePromotionDaysOrdinal(int ordinal) {
        return typeAPI.getPrePromotionDaysOrdinal(ordinal);
    }

    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        return typeAPI.getDisallowedAssetBundlesOrdinal(ordinal);
    }

    public VmsAttributeFeedEntryTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}