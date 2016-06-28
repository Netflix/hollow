package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamNonImageInfoHollow extends HollowObject {

    public StreamNonImageInfoHollow(StreamNonImageInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getRuntimeSeconds() {
        return delegate().getRuntimeSeconds(ordinal);
    }

    public Long _getRuntimeSecondsBoxed() {
        return delegate().getRuntimeSecondsBoxed(ordinal);
    }

    public StreamDrmInfoHollow _getDrmInfo() {
        int refOrdinal = delegate().getDrmInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamDrmInfoHollow(refOrdinal);
    }

    public ChunkDurationsStringHollow _getChunkDurations() {
        int refOrdinal = delegate().getChunkDurationsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getChunkDurationsStringHollow(refOrdinal);
    }

    public CodecPrivateDataStringHollow _getCodecPrivateData() {
        int refOrdinal = delegate().getCodecPrivateDataOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCodecPrivateDataStringHollow(refOrdinal);
    }

    public VideoStreamInfoHollow _getVideoInfo() {
        int refOrdinal = delegate().getVideoInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoStreamInfoHollow(refOrdinal);
    }

    public TextStreamInfoHollow _getTextInfo() {
        int refOrdinal = delegate().getTextInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTextStreamInfoHollow(refOrdinal);
    }

    public AudioStreamInfoHollow _getAudioInfo() {
        int refOrdinal = delegate().getAudioInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAudioStreamInfoHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamNonImageInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamNonImageInfoDelegate delegate() {
        return (StreamNonImageInfoDelegate)delegate;
    }

}