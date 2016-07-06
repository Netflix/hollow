package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class AudioStreamInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements AudioStreamInfoDelegate {

    private final AudioStreamInfoTypeAPI typeAPI;

    public AudioStreamInfoDelegateLookupImpl(AudioStreamInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAudioLanguageCodeOrdinal(int ordinal) {
        return typeAPI.getAudioLanguageCodeOrdinal(ordinal);
    }

    public int getAudioBitrateKBPS(int ordinal) {
        return typeAPI.getAudioBitrateKBPS(ordinal);
    }

    public Integer getAudioBitrateKBPSBoxed(int ordinal) {
        return typeAPI.getAudioBitrateKBPSBoxed(ordinal);
    }

    public AudioStreamInfoTypeAPI getTypeAPI() {
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