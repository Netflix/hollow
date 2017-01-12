package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamNonImageInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamNonImageInfoDelegate {

    private final Long runtimeSeconds;
    private final int drmInfoOrdinal;
    private final int chunkDurationsOrdinal;
    private final int codecPrivateDataOrdinal;
    private final int videoInfoOrdinal;
    private final int textInfoOrdinal;
    private final int audioInfoOrdinal;
   private StreamNonImageInfoTypeAPI typeAPI;

    public StreamNonImageInfoDelegateCachedImpl(StreamNonImageInfoTypeAPI typeAPI, int ordinal) {
        this.runtimeSeconds = typeAPI.getRuntimeSecondsBoxed(ordinal);
        this.drmInfoOrdinal = typeAPI.getDrmInfoOrdinal(ordinal);
        this.chunkDurationsOrdinal = typeAPI.getChunkDurationsOrdinal(ordinal);
        this.codecPrivateDataOrdinal = typeAPI.getCodecPrivateDataOrdinal(ordinal);
        this.videoInfoOrdinal = typeAPI.getVideoInfoOrdinal(ordinal);
        this.textInfoOrdinal = typeAPI.getTextInfoOrdinal(ordinal);
        this.audioInfoOrdinal = typeAPI.getAudioInfoOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getRuntimeSeconds(int ordinal) {
        return runtimeSeconds.longValue();
    }

    public Long getRuntimeSecondsBoxed(int ordinal) {
        return runtimeSeconds;
    }

    public int getDrmInfoOrdinal(int ordinal) {
        return drmInfoOrdinal;
    }

    public int getChunkDurationsOrdinal(int ordinal) {
        return chunkDurationsOrdinal;
    }

    public int getCodecPrivateDataOrdinal(int ordinal) {
        return codecPrivateDataOrdinal;
    }

    public int getVideoInfoOrdinal(int ordinal) {
        return videoInfoOrdinal;
    }

    public int getTextInfoOrdinal(int ordinal) {
        return textInfoOrdinal;
    }

    public int getAudioInfoOrdinal(int ordinal) {
        return audioInfoOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamNonImageInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamNonImageInfoTypeAPI) typeAPI;
    }

}