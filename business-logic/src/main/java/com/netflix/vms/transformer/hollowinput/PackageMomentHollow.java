package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PackageMomentHollow extends HollowObject {

    public PackageMomentHollow(PackageMomentDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getClipSpecRuntimeMillis() {
        return delegate().getClipSpecRuntimeMillis(ordinal);
    }

    public Long _getClipSpecRuntimeMillisBoxed() {
        return delegate().getClipSpecRuntimeMillisBoxed(ordinal);
    }

    public long _getOffsetMillis() {
        return delegate().getOffsetMillis(ordinal);
    }

    public Long _getOffsetMillisBoxed() {
        return delegate().getOffsetMillisBoxed(ordinal);
    }

    public DownloadableIdListHollow _getDownloadableIds() {
        int refOrdinal = delegate().getDownloadableIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDownloadableIdListHollow(refOrdinal);
    }

    public long _getBifIndex() {
        return delegate().getBifIndex(ordinal);
    }

    public Long _getBifIndexBoxed() {
        return delegate().getBifIndexBoxed(ordinal);
    }

    public StringHollow _getMomentType() {
        int refOrdinal = delegate().getMomentTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getMomentSeqNumber() {
        return delegate().getMomentSeqNumber(ordinal);
    }

    public Long _getMomentSeqNumberBoxed() {
        return delegate().getMomentSeqNumberBoxed(ordinal);
    }

    public StringHollow _getTags() {
        int refOrdinal = delegate().getTagsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PackageMomentTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PackageMomentDelegate delegate() {
        return (PackageMomentDelegate)delegate;
    }

}