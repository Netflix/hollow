package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtworkAttributesDelegateLookupImpl extends HollowObjectAbstractDelegate implements ArtworkAttributesDelegate {

    private final ArtworkAttributesTypeAPI typeAPI;

    public ArtworkAttributesDelegateLookupImpl(ArtworkAttributesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getPassthroughOrdinal(int ordinal) {
        return typeAPI.getPassthroughOrdinal(ordinal);
    }

    public boolean getROLLOUT_EXCLUSIVE(int ordinal) {
        return typeAPI.getROLLOUT_EXCLUSIVE(ordinal);
    }

    public Boolean getROLLOUT_EXCLUSIVEBoxed(int ordinal) {
        return typeAPI.getROLLOUT_EXCLUSIVEBoxed(ordinal);
    }

    public ArtworkAttributesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}