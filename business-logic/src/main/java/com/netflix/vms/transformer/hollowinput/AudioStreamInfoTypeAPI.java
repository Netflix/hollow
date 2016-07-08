package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class AudioStreamInfoTypeAPI extends HollowObjectTypeAPI {

    private final AudioStreamInfoDelegateLookupImpl delegateLookupImpl;

    AudioStreamInfoTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "audioLanguageCode",
            "audioBitrateKBPS"
        });
        this.delegateLookupImpl = new AudioStreamInfoDelegateLookupImpl(this);
    }

    public int getAudioLanguageCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("AudioStreamInfo", ordinal, "audioLanguageCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getAudioLanguageCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAudioBitrateKBPS(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("AudioStreamInfo", ordinal, "audioBitrateKBPS");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getAudioBitrateKBPSBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("AudioStreamInfo", ordinal, "audioBitrateKBPS");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public AudioStreamInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}