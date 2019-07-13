package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VmsAttributeFeedEntryTypeAPI extends HollowObjectTypeAPI {

    private final VmsAttributeFeedEntryDelegateLookupImpl delegateLookupImpl;

    public VmsAttributeFeedEntryTypeAPI(ExhibitDealAttributeV1API api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "dealId",
            "countryCode",
            "dayAfterBroadcast",
            "dayOfBroadcast",
            "prePromotionDays",
            "disallowedAssetBundles"
        });
        this.delegateLookupImpl = new VmsAttributeFeedEntryDelegateLookupImpl(this);
    }

    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VmsAttributeFeedEntry", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public LongTypeAPI getMovieIdTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public int getDealIdOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VmsAttributeFeedEntry", ordinal, "dealId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public LongTypeAPI getDealIdTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VmsAttributeFeedEntry", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getDayAfterBroadcastOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("VmsAttributeFeedEntry", ordinal, "dayAfterBroadcast");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public BooleanTypeAPI getDayAfterBroadcastTypeAPI() {
        return getAPI().getBooleanTypeAPI();
    }

    public int getDayOfBroadcastOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("VmsAttributeFeedEntry", ordinal, "dayOfBroadcast");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public BooleanTypeAPI getDayOfBroadcastTypeAPI() {
        return getAPI().getBooleanTypeAPI();
    }

    public int getPrePromotionDaysOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("VmsAttributeFeedEntry", ordinal, "prePromotionDays");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public LongTypeAPI getPrePromotionDaysTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("VmsAttributeFeedEntry", ordinal, "disallowedAssetBundles");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public SetOfDisallowedAssetBundleEntryTypeAPI getDisallowedAssetBundlesTypeAPI() {
        return getAPI().getSetOfDisallowedAssetBundleEntryTypeAPI();
    }

    public VmsAttributeFeedEntryDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public ExhibitDealAttributeV1API getAPI() {
        return (ExhibitDealAttributeV1API) api;
    }

}