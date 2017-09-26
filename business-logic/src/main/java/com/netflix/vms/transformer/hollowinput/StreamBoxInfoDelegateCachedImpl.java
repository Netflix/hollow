package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class StreamBoxInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamBoxInfoDelegate {

    private final Integer boxOffset;
    private final Integer boxSize;
    private final int keyOrdinal;
    private StreamBoxInfoTypeAPI typeAPI;

    public StreamBoxInfoDelegateCachedImpl(StreamBoxInfoTypeAPI typeAPI, int ordinal) {
        this.boxOffset = typeAPI.getBoxOffsetBoxed(ordinal);
        this.boxSize = typeAPI.getBoxSizeBoxed(ordinal);
        this.keyOrdinal = typeAPI.getKeyOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getBoxOffset(int ordinal) {
        if(boxOffset == null)
            return Integer.MIN_VALUE;
        return boxOffset.intValue();
    }

    public Integer getBoxOffsetBoxed(int ordinal) {
        return boxOffset;
    }

    public int getBoxSize(int ordinal) {
        if(boxSize == null)
            return Integer.MIN_VALUE;
        return boxSize.intValue();
    }

    public Integer getBoxSizeBoxed(int ordinal) {
        return boxSize;
    }

    public int getKeyOrdinal(int ordinal) {
        return keyOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamBoxInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamBoxInfoTypeAPI) typeAPI;
    }

}