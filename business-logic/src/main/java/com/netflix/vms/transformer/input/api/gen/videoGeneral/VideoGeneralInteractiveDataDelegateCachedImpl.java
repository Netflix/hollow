package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoGeneralInteractiveDataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoGeneralInteractiveDataDelegate {

    private final String interactiveType;
    private final int interactiveTypeOrdinal;
    private final Integer interactiveShortestRuntime;
    private VideoGeneralInteractiveDataTypeAPI typeAPI;

    public VideoGeneralInteractiveDataDelegateCachedImpl(VideoGeneralInteractiveDataTypeAPI typeAPI, int ordinal) {
        this.interactiveTypeOrdinal = typeAPI.getInteractiveTypeOrdinal(ordinal);
        int interactiveTypeTempOrdinal = interactiveTypeOrdinal;
        this.interactiveType = interactiveTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(interactiveTypeTempOrdinal);
        this.interactiveShortestRuntime = typeAPI.getInteractiveShortestRuntimeBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getInteractiveType(int ordinal) {
        return interactiveType;
    }

    public boolean isInteractiveTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return interactiveType == null;
        return testValue.equals(interactiveType);
    }

    public int getInteractiveTypeOrdinal(int ordinal) {
        return interactiveTypeOrdinal;
    }

    public int getInteractiveShortestRuntime(int ordinal) {
        if(interactiveShortestRuntime == null)
            return Integer.MIN_VALUE;
        return interactiveShortestRuntime.intValue();
    }

    public Integer getInteractiveShortestRuntimeBoxed(int ordinal) {
        return interactiveShortestRuntime;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoGeneralInteractiveDataTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoGeneralInteractiveDataTypeAPI) typeAPI;
    }

}