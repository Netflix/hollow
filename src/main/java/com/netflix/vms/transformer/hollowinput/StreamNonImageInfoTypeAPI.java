package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class StreamNonImageInfoTypeAPI extends HollowObjectTypeAPI {

    private final StreamNonImageInfoDelegateLookupImpl delegateLookupImpl;

    StreamNonImageInfoTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "runtimeSeconds",
            "drmInfo",
            "chunkDurations",
            "codecPrivateData",
            "videoInfo",
            "textInfo",
            "audioInfo"
        });
        this.delegateLookupImpl = new StreamNonImageInfoDelegateLookupImpl(this);
    }

    public long getRuntimeSeconds(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("StreamNonImageInfo", ordinal, "runtimeSeconds");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getRuntimeSecondsBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("StreamNonImageInfo", ordinal, "runtimeSeconds");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDrmInfoOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamNonImageInfo", ordinal, "drmInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StreamDrmInfoTypeAPI getDrmInfoTypeAPI() {
        return getAPI().getStreamDrmInfoTypeAPI();
    }

    public int getChunkDurationsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamNonImageInfo", ordinal, "chunkDurations");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ChunkDurationsStringTypeAPI getChunkDurationsTypeAPI() {
        return getAPI().getChunkDurationsStringTypeAPI();
    }

    public int getCodecPrivateDataOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamNonImageInfo", ordinal, "codecPrivateData");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public CodecPrivateDataStringTypeAPI getCodecPrivateDataTypeAPI() {
        return getAPI().getCodecPrivateDataStringTypeAPI();
    }

    public int getVideoInfoOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamNonImageInfo", ordinal, "videoInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public VideoStreamInfoTypeAPI getVideoInfoTypeAPI() {
        return getAPI().getVideoStreamInfoTypeAPI();
    }

    public int getTextInfoOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamNonImageInfo", ordinal, "textInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public TextStreamInfoTypeAPI getTextInfoTypeAPI() {
        return getAPI().getTextStreamInfoTypeAPI();
    }

    public int getAudioInfoOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamNonImageInfo", ordinal, "audioInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public AudioStreamInfoTypeAPI getAudioInfoTypeAPI() {
        return getAPI().getAudioStreamInfoTypeAPI();
    }

    public StreamNonImageInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}