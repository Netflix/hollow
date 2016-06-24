package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamNonImageInfoDelegate extends HollowObjectDelegate {

    public long getRuntimeSeconds(int ordinal);

    public Long getRuntimeSecondsBoxed(int ordinal);

    public int getDrmInfoOrdinal(int ordinal);

    public int getChunkDurationsOrdinal(int ordinal);

    public int getCodecPrivateDataOrdinal(int ordinal);

    public int getVideoInfoOrdinal(int ordinal);

    public int getTextInfoOrdinal(int ordinal);

    public int getAudioInfoOrdinal(int ordinal);

    public StreamNonImageInfoTypeAPI getTypeAPI();

}