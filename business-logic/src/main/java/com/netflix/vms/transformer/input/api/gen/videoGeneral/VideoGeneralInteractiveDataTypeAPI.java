package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoGeneralInteractiveDataTypeAPI extends HollowObjectTypeAPI {

    private final VideoGeneralInteractiveDataDelegateLookupImpl delegateLookupImpl;

    public VideoGeneralInteractiveDataTypeAPI(VideoGeneralAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "interactiveType",
            "interactiveShortestRuntime"
        });
        this.delegateLookupImpl = new VideoGeneralInteractiveDataDelegateLookupImpl(this);
    }

    public int getInteractiveTypeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneralInteractiveData", ordinal, "interactiveType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getInteractiveTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getInteractiveShortestRuntime(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("VideoGeneralInteractiveData", ordinal, "interactiveShortestRuntime");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getInteractiveShortestRuntimeBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("VideoGeneralInteractiveData", ordinal, "interactiveShortestRuntime");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public VideoGeneralInteractiveDataDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VideoGeneralAPI getAPI() {
        return (VideoGeneralAPI) api;
    }

}