package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MyEntityTypeAPI extends HollowObjectTypeAPI {

    private final MyEntityDelegateLookupImpl delegateLookupImpl;

    public MyEntityTypeAPI(MyNamespaceAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "name",
            "profileId"
        });
        this.delegateLookupImpl = new MyEntityDelegateLookupImpl(this);
    }

    public int getIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MyEntity", ordinal, "id");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public IntegerTypeAPI getIdTypeAPI() {
        return getAPI().getIntegerTypeAPI();
    }

    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("MyEntity", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getProfileIdOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("MyEntity", ordinal, "profileId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ProfileIdTypeAPI getProfileIdTypeAPI() {
        return getAPI().getProfileIdTypeAPI();
    }

    public MyEntityDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public MyNamespaceAPI getAPI() {
        return (MyNamespaceAPI) api;
    }

}