package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StreamFileIdentificationTypeAPI extends HollowObjectTypeAPI {

    private final StreamFileIdentificationDelegateLookupImpl delegateLookupImpl;

    public StreamFileIdentificationTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "filename",
            "fileSizeInBytes",
            "sha1_1",
            "sha1_2",
            "sha1_3",
            "crc32",
            "createdTimeSeconds"
        });
        this.delegateLookupImpl = new StreamFileIdentificationDelegateLookupImpl(this);
    }

    public String getFilename(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleString("StreamFileIdentification", ordinal, "filename");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[0]);
    }

    public boolean isFilenameEqual(int ordinal, String testValue) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleStringEquals("StreamFileIdentification", ordinal, "filename", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[0], testValue);
    }

    public long getFileSizeInBytes(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("StreamFileIdentification", ordinal, "fileSizeInBytes");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getFileSizeInBytesBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("StreamFileIdentification", ordinal, "fileSizeInBytes");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getSha1_1(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("StreamFileIdentification", ordinal, "sha1_1");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getSha1_1Boxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("StreamFileIdentification", ordinal, "sha1_1");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getSha1_2(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("StreamFileIdentification", ordinal, "sha1_2");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getSha1_2Boxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("StreamFileIdentification", ordinal, "sha1_2");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getSha1_3(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("StreamFileIdentification", ordinal, "sha1_3");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getSha1_3Boxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("StreamFileIdentification", ordinal, "sha1_3");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getCrc32(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleLong("StreamFileIdentification", ordinal, "crc32");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
    }

    public Long getCrc32Boxed(int ordinal) {
        long l;
        if(fieldIndex[5] == -1) {
            l = missingDataHandler().handleLong("StreamFileIdentification", ordinal, "crc32");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[5]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getCreatedTimeSeconds(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleLong("StreamFileIdentification", ordinal, "createdTimeSeconds");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
    }

    public Long getCreatedTimeSecondsBoxed(int ordinal) {
        long l;
        if(fieldIndex[6] == -1) {
            l = missingDataHandler().handleLong("StreamFileIdentification", ordinal, "createdTimeSeconds");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[6]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public StreamFileIdentificationDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}