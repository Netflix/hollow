package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieTypeTypeAPI extends HollowObjectTypeAPI {

    private final MovieTypeDelegateLookupImpl delegateLookupImpl;

    public MovieTypeTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "streamingType",
            "viewable",
            "merchable",
            "_name"
        });
        this.delegateLookupImpl = new MovieTypeDelegateLookupImpl(this);
    }

    public boolean getStreamingType(int ordinal) {
        if(fieldIndex[0] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("MovieType", ordinal, "streamingType"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]));
    }

    public Boolean getStreamingTypeBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("MovieType", ordinal, "streamingType");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public boolean getViewable(int ordinal) {
        if(fieldIndex[1] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("MovieType", ordinal, "viewable"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]));
    }

    public Boolean getViewableBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("MovieType", ordinal, "viewable");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public boolean getMerchable(int ordinal) {
        if(fieldIndex[2] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("MovieType", ordinal, "merchable"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]));
    }

    public Boolean getMerchableBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("MovieType", ordinal, "merchable");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public String get_name(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleString("MovieType", ordinal, "_name");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[3]);
    }

    public boolean is_nameEqual(int ordinal, String testValue) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleStringEquals("MovieType", ordinal, "_name", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[3], testValue);
    }

    public MovieTypeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}