package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PackageMomentTypeAPI extends HollowObjectTypeAPI {

    private final PackageMomentDelegateLookupImpl delegateLookupImpl;

    PackageMomentTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "clipSpecRuntimeMillis",
            "offsetMillis",
            "downloadableIds",
            "bifIndex",
            "momentType",
            "momentSeqNumber",
            "tags"
        });
        this.delegateLookupImpl = new PackageMomentDelegateLookupImpl(this);
    }

    public long getClipSpecRuntimeMillis(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("PackageMoment", ordinal, "clipSpecRuntimeMillis");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getClipSpecRuntimeMillisBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("PackageMoment", ordinal, "clipSpecRuntimeMillis");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getOffsetMillis(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("PackageMoment", ordinal, "offsetMillis");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getOffsetMillisBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("PackageMoment", ordinal, "offsetMillis");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDownloadableIdsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageMoment", ordinal, "downloadableIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public DownloadableIdListTypeAPI getDownloadableIdsTypeAPI() {
        return getAPI().getDownloadableIdListTypeAPI();
    }

    public long getBifIndex(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("PackageMoment", ordinal, "bifIndex");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getBifIndexBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("PackageMoment", ordinal, "bifIndex");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getMomentTypeOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageMoment", ordinal, "momentType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getMomentTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getMomentSeqNumber(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleLong("PackageMoment", ordinal, "momentSeqNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
    }

    public Long getMomentSeqNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[5] == -1) {
            l = missingDataHandler().handleLong("PackageMoment", ordinal, "momentSeqNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[5]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getTagsOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageMoment", ordinal, "tags");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getTagsTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public PackageMomentDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}