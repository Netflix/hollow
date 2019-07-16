package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieSetContentLabelTypeAPI extends HollowObjectTypeAPI {

    private final MovieSetContentLabelDelegateLookupImpl delegateLookupImpl;

    public MovieSetContentLabelTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "description",
            "id",
            "_name"
        });
        this.delegateLookupImpl = new MovieSetContentLabelDelegateLookupImpl(this);
    }

    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieSetContentLabel", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getDescriptionTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("MovieSetContentLabel", ordinal, "id");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getIdBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("MovieSetContentLabel", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public String get_name(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleString("MovieSetContentLabel", ordinal, "_name");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[2]);
    }

    public boolean is_nameEqual(int ordinal, String testValue) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleStringEquals("MovieSetContentLabel", ordinal, "_name", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[2], testValue);
    }

    public MovieSetContentLabelDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}