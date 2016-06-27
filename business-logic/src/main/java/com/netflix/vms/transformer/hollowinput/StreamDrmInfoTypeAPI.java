package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StreamDrmInfoTypeAPI extends HollowObjectTypeAPI {

    private final StreamDrmInfoDelegateLookupImpl delegateLookupImpl;

    StreamDrmInfoTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "keyId",
            "key",
            "contentPackagerPublicKey",
            "keySeed",
            "type"
        });
        this.delegateLookupImpl = new StreamDrmInfoDelegateLookupImpl(this);
    }

    public int getKeyIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamDrmInfo", ordinal, "keyId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public DrmInfoStringTypeAPI getKeyIdTypeAPI() {
        return getAPI().getDrmInfoStringTypeAPI();
    }

    public int getKeyOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamDrmInfo", ordinal, "key");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public DrmInfoStringTypeAPI getKeyTypeAPI() {
        return getAPI().getDrmInfoStringTypeAPI();
    }

    public int getContentPackagerPublicKeyOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamDrmInfo", ordinal, "contentPackagerPublicKey");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public DrmInfoStringTypeAPI getContentPackagerPublicKeyTypeAPI() {
        return getAPI().getDrmInfoStringTypeAPI();
    }

    public int getKeySeedOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamDrmInfo", ordinal, "keySeed");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public DrmInfoStringTypeAPI getKeySeedTypeAPI() {
        return getAPI().getDrmInfoStringTypeAPI();
    }

    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamDrmInfo", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public StreamDrmInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}