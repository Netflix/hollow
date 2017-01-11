package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ShowMemberTypesTypeAPI extends HollowObjectTypeAPI {

    private final ShowMemberTypesDelegateLookupImpl delegateLookupImpl;

    ShowMemberTypesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "showMemberTypeId",
            "displayName"
        });
        this.delegateLookupImpl = new ShowMemberTypesDelegateLookupImpl(this);
    }

    public long getShowMemberTypeId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("ShowMemberTypes", ordinal, "showMemberTypeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getShowMemberTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("ShowMemberTypes", ordinal, "showMemberTypeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDisplayNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ShowMemberTypes", ordinal, "displayName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getDisplayNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public ShowMemberTypesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}