package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamBoxInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamBoxInfoDelegate {

    private final StreamBoxInfoTypeAPI typeAPI;

    public StreamBoxInfoDelegateLookupImpl(StreamBoxInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getBoxOffset(int ordinal) {
        return typeAPI.getBoxOffset(ordinal);
    }

    public Integer getBoxOffsetBoxed(int ordinal) {
        return typeAPI.getBoxOffsetBoxed(ordinal);
    }

    public int getBoxSize(int ordinal) {
        return typeAPI.getBoxSize(ordinal);
    }

    public Integer getBoxSizeBoxed(int ordinal) {
        return typeAPI.getBoxSizeBoxed(ordinal);
    }

    public int getKeyOrdinal(int ordinal) {
        return typeAPI.getKeyOrdinal(ordinal);
    }

    public StreamBoxInfoTypeAPI getTypeAPI() {
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