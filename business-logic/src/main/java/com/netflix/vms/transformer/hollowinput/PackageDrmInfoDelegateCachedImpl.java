package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PackageDrmInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PackageDrmInfoDelegate {

    private final int contentPackagerPublicKeyOrdinal;
    private final int keySeedOrdinal;
    private final Long keyId;
    private final Long drmKeyGroup;
    private final int keyOrdinal;
    private final int drmHeaderInfoOrdinal;
    private final Boolean keyDecrypted;
   private PackageDrmInfoTypeAPI typeAPI;

    public PackageDrmInfoDelegateCachedImpl(PackageDrmInfoTypeAPI typeAPI, int ordinal) {
        this.contentPackagerPublicKeyOrdinal = typeAPI.getContentPackagerPublicKeyOrdinal(ordinal);
        this.keySeedOrdinal = typeAPI.getKeySeedOrdinal(ordinal);
        this.keyId = typeAPI.getKeyIdBoxed(ordinal);
        this.drmKeyGroup = typeAPI.getDrmKeyGroupBoxed(ordinal);
        this.keyOrdinal = typeAPI.getKeyOrdinal(ordinal);
        this.drmHeaderInfoOrdinal = typeAPI.getDrmHeaderInfoOrdinal(ordinal);
        this.keyDecrypted = typeAPI.getKeyDecryptedBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getContentPackagerPublicKeyOrdinal(int ordinal) {
        return contentPackagerPublicKeyOrdinal;
    }

    public int getKeySeedOrdinal(int ordinal) {
        return keySeedOrdinal;
    }

    public long getKeyId(int ordinal) {
        return keyId.longValue();
    }

    public Long getKeyIdBoxed(int ordinal) {
        return keyId;
    }

    public long getDrmKeyGroup(int ordinal) {
        return drmKeyGroup.longValue();
    }

    public Long getDrmKeyGroupBoxed(int ordinal) {
        return drmKeyGroup;
    }

    public int getKeyOrdinal(int ordinal) {
        return keyOrdinal;
    }

    public int getDrmHeaderInfoOrdinal(int ordinal) {
        return drmHeaderInfoOrdinal;
    }

    public boolean getKeyDecrypted(int ordinal) {
        return keyDecrypted.booleanValue();
    }

    public Boolean getKeyDecryptedBoxed(int ordinal) {
        return keyDecrypted;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PackageDrmInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PackageDrmInfoTypeAPI) typeAPI;
    }

}