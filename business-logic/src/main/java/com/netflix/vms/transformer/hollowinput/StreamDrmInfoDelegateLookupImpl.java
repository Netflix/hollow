package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class StreamDrmInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamDrmInfoDelegate {

    private final StreamDrmInfoTypeAPI typeAPI;

    public StreamDrmInfoDelegateLookupImpl(StreamDrmInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getKeyIdOrdinal(int ordinal) {
        return typeAPI.getKeyIdOrdinal(ordinal);
    }

    public int getKeyOrdinal(int ordinal) {
        return typeAPI.getKeyOrdinal(ordinal);
    }

    public int getContentPackagerPublicKeyOrdinal(int ordinal) {
        return typeAPI.getContentPackagerPublicKeyOrdinal(ordinal);
    }

    public int getKeySeedOrdinal(int ordinal) {
        return typeAPI.getKeySeedOrdinal(ordinal);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeAPI.getTypeOrdinal(ordinal);
    }

    public StreamDrmInfoTypeAPI getTypeAPI() {
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