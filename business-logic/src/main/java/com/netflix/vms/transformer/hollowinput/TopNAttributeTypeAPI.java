package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TopNAttributeTypeAPI extends HollowObjectTypeAPI {

    private final TopNAttributeDelegateLookupImpl delegateLookupImpl;

    TopNAttributeTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "country",
            "viewShare",
            "countryViewHrs"
        });
        this.delegateLookupImpl = new TopNAttributeDelegateLookupImpl(this);
    }

    public int getCountryOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TopNAttribute", ordinal, "country");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCountryTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getViewShareOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("TopNAttribute", ordinal, "viewShare");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getViewShareTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCountryViewHrsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("TopNAttribute", ordinal, "countryViewHrs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCountryViewHrsTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public TopNAttributeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}