package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PhaseRequiredImageTypeTypeAPI extends HollowObjectTypeAPI {

    private final PhaseRequiredImageTypeDelegateLookupImpl delegateLookupImpl;

    public PhaseRequiredImageTypeTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "imageType",
            "imageSwapRequired",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new PhaseRequiredImageTypeDelegateLookupImpl(this);
    }

    public int getImageTypeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PhaseRequiredImageType", ordinal, "imageType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ImageTypeTypeAPI getImageTypeTypeAPI() {
        return getAPI().getImageTypeTypeAPI();
    }

    public boolean getImageSwapRequired(int ordinal) {
        if(fieldIndex[1] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("PhaseRequiredImageType", ordinal, "imageSwapRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]));
    }

    public Boolean getImageSwapRequiredBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("PhaseRequiredImageType", ordinal, "imageSwapRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("PhaseRequiredImageType", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("PhaseRequiredImageType", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("PhaseRequiredImageType", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("PhaseRequiredImageType", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public PhaseRequiredImageTypeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}