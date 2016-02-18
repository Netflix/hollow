package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoArtWorkLocalesTypeAPI extends HollowObjectTypeAPI {

    private final VideoArtWorkLocalesDelegateLookupImpl delegateLookupImpl;

    VideoArtWorkLocalesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "territoryCodes",
            "bcp47Code",
            "effectiveDate"
        });
        this.delegateLookupImpl = new VideoArtWorkLocalesDelegateLookupImpl(this);
    }

    public int getTerritoryCodesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkLocales", ordinal, "territoryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public VideoArtWorkLocalesArrayOfTerritoryCodesTypeAPI getTerritoryCodesTypeAPI() {
        return getAPI().getVideoArtWorkLocalesArrayOfTerritoryCodesTypeAPI();
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkLocales", ordinal, "bcp47Code");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getBcp47CodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getEffectiveDateOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkLocales", ordinal, "effectiveDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public DateTypeAPI getEffectiveDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public VideoArtWorkLocalesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}