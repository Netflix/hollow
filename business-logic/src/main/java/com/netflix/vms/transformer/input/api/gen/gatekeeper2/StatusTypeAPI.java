package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StatusTypeAPI extends HollowObjectTypeAPI {

    private final StatusDelegateLookupImpl delegateLookupImpl;

    public StatusTypeAPI(Gk2StatusAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "countryCode",
            "rights",
            "flags",
            "availableAssets",
            "hierarchyInfo"
        });
        this.delegateLookupImpl = new StatusDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Status", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Status", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Status", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRightsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Status", ordinal, "rights");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public RightsTypeAPI getRightsTypeAPI() {
        return getAPI().getRightsTypeAPI();
    }

    public int getFlagsOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Status", ordinal, "flags");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public FlagsTypeAPI getFlagsTypeAPI() {
        return getAPI().getFlagsTypeAPI();
    }

    public int getAvailableAssetsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Status", ordinal, "availableAssets");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public AvailableAssetsTypeAPI getAvailableAssetsTypeAPI() {
        return getAPI().getAvailableAssetsTypeAPI();
    }

    public int getHierarchyInfoOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Status", ordinal, "hierarchyInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public VideoHierarchyInfoTypeAPI getHierarchyInfoTypeAPI() {
        return getAPI().getVideoHierarchyInfoTypeAPI();
    }

    public StatusDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI) api;
    }

}