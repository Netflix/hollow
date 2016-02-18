package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PackageDrmInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements PackageDrmInfoDelegate {

    private final PackageDrmInfoTypeAPI typeAPI;

    public PackageDrmInfoDelegateLookupImpl(PackageDrmInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getContentPackagerPublicKeyOrdinal(int ordinal) {
        return typeAPI.getContentPackagerPublicKeyOrdinal(ordinal);
    }

    public int getKeySeedOrdinal(int ordinal) {
        return typeAPI.getKeySeedOrdinal(ordinal);
    }

    public long getKeyId(int ordinal) {
        return typeAPI.getKeyId(ordinal);
    }

    public Long getKeyIdBoxed(int ordinal) {
        return typeAPI.getKeyIdBoxed(ordinal);
    }

    public long getDrmKeyGroup(int ordinal) {
        return typeAPI.getDrmKeyGroup(ordinal);
    }

    public Long getDrmKeyGroupBoxed(int ordinal) {
        return typeAPI.getDrmKeyGroupBoxed(ordinal);
    }

    public int getKeyOrdinal(int ordinal) {
        return typeAPI.getKeyOrdinal(ordinal);
    }

    public int getDrmHeaderInfoOrdinal(int ordinal) {
        return typeAPI.getDrmHeaderInfoOrdinal(ordinal);
    }

    public PackageDrmInfoTypeAPI getTypeAPI() {
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