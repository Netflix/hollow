package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ShowCountryLabelTypeAPI extends HollowObjectTypeAPI {

    private final ShowCountryLabelDelegateLookupImpl delegateLookupImpl;

    public ShowCountryLabelTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "videoId",
            "showMemberTypes"
        });
        this.delegateLookupImpl = new ShowCountryLabelDelegateLookupImpl(this);
    }

    public long getVideoId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("ShowCountryLabel", ordinal, "videoId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getVideoIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("ShowCountryLabel", ordinal, "videoId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getShowMemberTypesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ShowCountryLabel", ordinal, "showMemberTypes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ShowMemberTypeListTypeAPI getShowMemberTypesTypeAPI() {
        return getAPI().getShowMemberTypeListTypeAPI();
    }

    public ShowCountryLabelDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}