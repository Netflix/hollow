package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MyEntityDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MyEntityDelegate {

    private final Integer id;
    private final int idOrdinal;
    private final String name;
    private final int nameOrdinal;
    private final Integer profileId;
    private final int profileIdOrdinal;
    private MyEntityTypeAPI typeAPI;

    public MyEntityDelegateCachedImpl(MyEntityTypeAPI typeAPI, int ordinal) {
        this.idOrdinal = typeAPI.getIdOrdinal(ordinal);
        int idTempOrdinal = idOrdinal;
        this.id = idTempOrdinal == -1 ? null : typeAPI.getAPI().getIntegerTypeAPI().getValue(idTempOrdinal);
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        int nameTempOrdinal = nameOrdinal;
        this.name = nameTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(nameTempOrdinal);
        this.profileIdOrdinal = typeAPI.getProfileIdOrdinal(ordinal);
        int profileIdTempOrdinal = profileIdOrdinal;
        this.profileId = profileIdTempOrdinal == -1 ? null : typeAPI.getAPI().getProfileIdTypeAPI().getValue(profileIdTempOrdinal);
        this.typeAPI = typeAPI;
    }

    public int getId(int ordinal) {
        if(id == null)
            return Integer.MIN_VALUE;
        return id.intValue();
    }

    public Integer getIdBoxed(int ordinal) {
        return id;
    }

    public int getIdOrdinal(int ordinal) {
        return idOrdinal;
    }

    public String getName(int ordinal) {
        return name;
    }

    public boolean isNameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return name == null;
        return testValue.equals(name);
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public int getProfileId(int ordinal) {
        if(profileId == null)
            return Integer.MIN_VALUE;
        return profileId.intValue();
    }

    public Integer getProfileIdBoxed(int ordinal) {
        return profileId;
    }

    public int getProfileIdOrdinal(int ordinal) {
        return profileIdOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MyEntityTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MyEntityTypeAPI) typeAPI;
    }

}