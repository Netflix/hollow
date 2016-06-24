package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PackageMomentDelegate extends HollowObjectDelegate {

    public long getClipSpecRuntimeMillis(int ordinal);

    public Long getClipSpecRuntimeMillisBoxed(int ordinal);

    public long getOffsetMillis(int ordinal);

    public Long getOffsetMillisBoxed(int ordinal);

    public int getDownloadableIdsOrdinal(int ordinal);

    public long getBifIndex(int ordinal);

    public Long getBifIndexBoxed(int ordinal);

    public int getMomentTypeOrdinal(int ordinal);

    public long getMomentSeqNumber(int ordinal);

    public Long getMomentSeqNumberBoxed(int ordinal);

    public int getTagsOrdinal(int ordinal);

    public PackageMomentTypeAPI getTypeAPI();

}