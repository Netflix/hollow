package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class TopNAttributesTypeAPI extends HollowObjectTypeAPI {

    private final TopNAttributesDelegateLookupImpl delegateLookupImpl;

    TopNAttributesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "country",
            "viewShare",
            "countryViewHrs"
        });
        this.delegateLookupImpl = new TopNAttributesDelegateLookupImpl(this);
    }

    public int getCountryOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TopNAttributes", ordinal, "country");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCountryTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getViewShareOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("TopNAttributes", ordinal, "viewShare");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getViewShareTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCountryViewHrsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("TopNAttributes", ordinal, "countryViewHrs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCountryViewHrsTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public TopNAttributesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}