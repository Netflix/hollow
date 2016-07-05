package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class StreamDrmInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamDrmInfoDelegate {

    private final int keyIdOrdinal;
    private final int keyOrdinal;
    private final int contentPackagerPublicKeyOrdinal;
    private final int keySeedOrdinal;
    private final int typeOrdinal;
   private StreamDrmInfoTypeAPI typeAPI;

    public StreamDrmInfoDelegateCachedImpl(StreamDrmInfoTypeAPI typeAPI, int ordinal) {
        this.keyIdOrdinal = typeAPI.getKeyIdOrdinal(ordinal);
        this.keyOrdinal = typeAPI.getKeyOrdinal(ordinal);
        this.contentPackagerPublicKeyOrdinal = typeAPI.getContentPackagerPublicKeyOrdinal(ordinal);
        this.keySeedOrdinal = typeAPI.getKeySeedOrdinal(ordinal);
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getKeyIdOrdinal(int ordinal) {
        return keyIdOrdinal;
    }

    public int getKeyOrdinal(int ordinal) {
        return keyOrdinal;
    }

    public int getContentPackagerPublicKeyOrdinal(int ordinal) {
        return contentPackagerPublicKeyOrdinal;
    }

    public int getKeySeedOrdinal(int ordinal) {
        return keySeedOrdinal;
    }

    public int getTypeOrdinal(int ordinal) {
        return typeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamDrmInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamDrmInfoTypeAPI) typeAPI;
    }

}