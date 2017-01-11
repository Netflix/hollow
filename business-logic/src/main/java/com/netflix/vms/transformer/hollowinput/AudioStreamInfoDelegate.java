package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface AudioStreamInfoDelegate extends HollowObjectDelegate {

    public int getAudioLanguageCodeOrdinal(int ordinal);

    public int getAudioBitrateKBPS(int ordinal);

    public Integer getAudioBitrateKBPSBoxed(int ordinal);

    public AudioStreamInfoTypeAPI getTypeAPI();

}