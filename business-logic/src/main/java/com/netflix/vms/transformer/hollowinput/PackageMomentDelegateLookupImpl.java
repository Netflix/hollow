package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PackageMomentDelegateLookupImpl extends HollowObjectAbstractDelegate implements PackageMomentDelegate {

    private final PackageMomentTypeAPI typeAPI;

    public PackageMomentDelegateLookupImpl(PackageMomentTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getClipSpecRuntimeMillis(int ordinal) {
        return typeAPI.getClipSpecRuntimeMillis(ordinal);
    }

    public Long getClipSpecRuntimeMillisBoxed(int ordinal) {
        return typeAPI.getClipSpecRuntimeMillisBoxed(ordinal);
    }

    public long getOffsetMillis(int ordinal) {
        return typeAPI.getOffsetMillis(ordinal);
    }

    public Long getOffsetMillisBoxed(int ordinal) {
        return typeAPI.getOffsetMillisBoxed(ordinal);
    }

    public int getDownloadableIdsOrdinal(int ordinal) {
        return typeAPI.getDownloadableIdsOrdinal(ordinal);
    }

    public long getBifIndex(int ordinal) {
        return typeAPI.getBifIndex(ordinal);
    }

    public Long getBifIndexBoxed(int ordinal) {
        return typeAPI.getBifIndexBoxed(ordinal);
    }

    public int getMomentTypeOrdinal(int ordinal) {
        return typeAPI.getMomentTypeOrdinal(ordinal);
    }

    public long getMomentSeqNumber(int ordinal) {
        return typeAPI.getMomentSeqNumber(ordinal);
    }

    public Long getMomentSeqNumberBoxed(int ordinal) {
        return typeAPI.getMomentSeqNumberBoxed(ordinal);
    }

    public int getTagsOrdinal(int ordinal) {
        return typeAPI.getTagsOrdinal(ordinal);
    }

    public PackageMomentTypeAPI getTypeAPI() {
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