package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PackageMomentDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PackageMomentDelegate {

    private final Long clipSpecRuntimeMillis;
    private final Long offsetMillis;
    private final int downloadableIdsOrdinal;
    private final Long bifIndex;
    private final int momentTypeOrdinal;
    private final Long momentSeqNumber;
    private final int tagsOrdinal;
   private PackageMomentTypeAPI typeAPI;

    public PackageMomentDelegateCachedImpl(PackageMomentTypeAPI typeAPI, int ordinal) {
        this.clipSpecRuntimeMillis = typeAPI.getClipSpecRuntimeMillisBoxed(ordinal);
        this.offsetMillis = typeAPI.getOffsetMillisBoxed(ordinal);
        this.downloadableIdsOrdinal = typeAPI.getDownloadableIdsOrdinal(ordinal);
        this.bifIndex = typeAPI.getBifIndexBoxed(ordinal);
        this.momentTypeOrdinal = typeAPI.getMomentTypeOrdinal(ordinal);
        this.momentSeqNumber = typeAPI.getMomentSeqNumberBoxed(ordinal);
        this.tagsOrdinal = typeAPI.getTagsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getClipSpecRuntimeMillis(int ordinal) {
        return clipSpecRuntimeMillis.longValue();
    }

    public Long getClipSpecRuntimeMillisBoxed(int ordinal) {
        return clipSpecRuntimeMillis;
    }

    public long getOffsetMillis(int ordinal) {
        return offsetMillis.longValue();
    }

    public Long getOffsetMillisBoxed(int ordinal) {
        return offsetMillis;
    }

    public int getDownloadableIdsOrdinal(int ordinal) {
        return downloadableIdsOrdinal;
    }

    public long getBifIndex(int ordinal) {
        return bifIndex.longValue();
    }

    public Long getBifIndexBoxed(int ordinal) {
        return bifIndex;
    }

    public int getMomentTypeOrdinal(int ordinal) {
        return momentTypeOrdinal;
    }

    public long getMomentSeqNumber(int ordinal) {
        return momentSeqNumber.longValue();
    }

    public Long getMomentSeqNumberBoxed(int ordinal) {
        return momentSeqNumber;
    }

    public int getTagsOrdinal(int ordinal) {
        return tagsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PackageMomentTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PackageMomentTypeAPI) typeAPI;
    }

}