package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesDelegateLookupImpl delegateLookupImpl;

    ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ordered",
            "imageOnly",
            "ids"
        });
        this.delegateLookupImpl = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesDelegateLookupImpl(this);
    }

    public boolean getOrdered(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories", ordinal, "ordered") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getOrderedBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories", ordinal, "ordered");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public boolean getImageOnly(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories", ordinal, "imageOnly") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getImageOnlyBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories", ordinal, "imageOnly");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public int getIdsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories", ordinal, "ids");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI getIdsTypeAPI() {
        return getAPI().getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI();
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}