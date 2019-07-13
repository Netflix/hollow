package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoGeneralInteractiveDataDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoGeneralInteractiveDataDelegate {

    private final VideoGeneralInteractiveDataTypeAPI typeAPI;

    public VideoGeneralInteractiveDataDelegateLookupImpl(VideoGeneralInteractiveDataTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getInteractiveType(int ordinal) {
        ordinal = typeAPI.getInteractiveTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isInteractiveTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getInteractiveTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getInteractiveTypeOrdinal(int ordinal) {
        return typeAPI.getInteractiveTypeOrdinal(ordinal);
    }

    public int getInteractiveShortestRuntime(int ordinal) {
        return typeAPI.getInteractiveShortestRuntime(ordinal);
    }

    public Integer getInteractiveShortestRuntimeBoxed(int ordinal) {
        return typeAPI.getInteractiveShortestRuntimeBoxed(ordinal);
    }

    public VideoGeneralInteractiveDataTypeAPI getTypeAPI() {
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