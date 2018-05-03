package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class AudioStreamInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AudioStreamInfoDelegate {

    private final int audioLanguageCodeOrdinal;
    private final Integer audioBitrateKBPS;
    private AudioStreamInfoTypeAPI typeAPI;

    public AudioStreamInfoDelegateCachedImpl(AudioStreamInfoTypeAPI typeAPI, int ordinal) {
        this.audioLanguageCodeOrdinal = typeAPI.getAudioLanguageCodeOrdinal(ordinal);
        this.audioBitrateKBPS = typeAPI.getAudioBitrateKBPSBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAudioLanguageCodeOrdinal(int ordinal) {
        return audioLanguageCodeOrdinal;
    }

    public int getAudioBitrateKBPS(int ordinal) {
        if(audioBitrateKBPS == null)
            return Integer.MIN_VALUE;
        return audioBitrateKBPS.intValue();
    }

    public Integer getAudioBitrateKBPSBoxed(int ordinal) {
        return audioBitrateKBPS;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AudioStreamInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AudioStreamInfoTypeAPI) typeAPI;
    }

}