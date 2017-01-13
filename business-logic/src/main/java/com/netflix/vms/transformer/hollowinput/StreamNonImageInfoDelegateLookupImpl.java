package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamNonImageInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamNonImageInfoDelegate {

    private final StreamNonImageInfoTypeAPI typeAPI;

    public StreamNonImageInfoDelegateLookupImpl(StreamNonImageInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getRuntimeSeconds(int ordinal) {
        return typeAPI.getRuntimeSeconds(ordinal);
    }

    public Long getRuntimeSecondsBoxed(int ordinal) {
        return typeAPI.getRuntimeSecondsBoxed(ordinal);
    }

    public int getDrmInfoOrdinal(int ordinal) {
        return typeAPI.getDrmInfoOrdinal(ordinal);
    }

    public int getChunkDurationsOrdinal(int ordinal) {
        return typeAPI.getChunkDurationsOrdinal(ordinal);
    }

    public int getCodecPrivateDataOrdinal(int ordinal) {
        return typeAPI.getCodecPrivateDataOrdinal(ordinal);
    }

    public int getVideoInfoOrdinal(int ordinal) {
        return typeAPI.getVideoInfoOrdinal(ordinal);
    }

    public int getTextInfoOrdinal(int ordinal) {
        return typeAPI.getTextInfoOrdinal(ordinal);
    }

    public int getAudioInfoOrdinal(int ordinal) {
        return typeAPI.getAudioInfoOrdinal(ordinal);
    }

    public StreamNonImageInfoTypeAPI getTypeAPI() {
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