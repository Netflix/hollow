package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ShowMemberTypeTypeAPI extends HollowObjectTypeAPI {

    private final ShowMemberTypeDelegateLookupImpl delegateLookupImpl;

    public ShowMemberTypeTypeAPI(ShowCountryLabelAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryCodes",
            "sequenceLabelId"
        });
        this.delegateLookupImpl = new ShowMemberTypeDelegateLookupImpl(this);
    }

    public int getCountryCodesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ShowMemberType", ordinal, "countryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ISOCountryListTypeAPI getCountryCodesTypeAPI() {
        return getAPI().getISOCountryListTypeAPI();
    }

    public long getSequenceLabelId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("ShowMemberType", ordinal, "sequenceLabelId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getSequenceLabelIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("ShowMemberType", ordinal, "sequenceLabelId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public ShowMemberTypeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public ShowCountryLabelAPI getAPI() {
        return (ShowCountryLabelAPI) api;
    }

}