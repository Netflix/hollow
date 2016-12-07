package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class AudioStreamInfoHollow extends HollowObject {

    public AudioStreamInfoHollow(AudioStreamInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getAudioLanguageCode() {
        int refOrdinal = delegate().getAudioLanguageCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public int _getAudioBitrateKBPS() {
        return delegate().getAudioBitrateKBPS(ordinal);
    }

    public Integer _getAudioBitrateKBPSBoxed() {
        return delegate().getAudioBitrateKBPSBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public AudioStreamInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AudioStreamInfoDelegate delegate() {
        return (AudioStreamInfoDelegate)delegate;
    }

}