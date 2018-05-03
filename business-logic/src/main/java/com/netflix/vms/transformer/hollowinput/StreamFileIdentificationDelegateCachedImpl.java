package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class StreamFileIdentificationDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamFileIdentificationDelegate {

    private final String filename;
    private final Long fileSizeInBytes;
    private final Long sha1_1;
    private final Long sha1_2;
    private final Long sha1_3;
    private final Long crc32;
    private final Long createdTimeSeconds;
    private StreamFileIdentificationTypeAPI typeAPI;

    public StreamFileIdentificationDelegateCachedImpl(StreamFileIdentificationTypeAPI typeAPI, int ordinal) {
        this.filename = typeAPI.getFilename(ordinal);
        this.fileSizeInBytes = typeAPI.getFileSizeInBytesBoxed(ordinal);
        this.sha1_1 = typeAPI.getSha1_1Boxed(ordinal);
        this.sha1_2 = typeAPI.getSha1_2Boxed(ordinal);
        this.sha1_3 = typeAPI.getSha1_3Boxed(ordinal);
        this.crc32 = typeAPI.getCrc32Boxed(ordinal);
        this.createdTimeSeconds = typeAPI.getCreatedTimeSecondsBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getFilename(int ordinal) {
        return filename;
    }

    public boolean isFilenameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return filename == null;
        return testValue.equals(filename);
    }

    public long getFileSizeInBytes(int ordinal) {
        if(fileSizeInBytes == null)
            return Long.MIN_VALUE;
        return fileSizeInBytes.longValue();
    }

    public Long getFileSizeInBytesBoxed(int ordinal) {
        return fileSizeInBytes;
    }

    public long getSha1_1(int ordinal) {
        if(sha1_1 == null)
            return Long.MIN_VALUE;
        return sha1_1.longValue();
    }

    public Long getSha1_1Boxed(int ordinal) {
        return sha1_1;
    }

    public long getSha1_2(int ordinal) {
        if(sha1_2 == null)
            return Long.MIN_VALUE;
        return sha1_2.longValue();
    }

    public Long getSha1_2Boxed(int ordinal) {
        return sha1_2;
    }

    public long getSha1_3(int ordinal) {
        if(sha1_3 == null)
            return Long.MIN_VALUE;
        return sha1_3.longValue();
    }

    public Long getSha1_3Boxed(int ordinal) {
        return sha1_3;
    }

    public long getCrc32(int ordinal) {
        if(crc32 == null)
            return Long.MIN_VALUE;
        return crc32.longValue();
    }

    public Long getCrc32Boxed(int ordinal) {
        return crc32;
    }

    public long getCreatedTimeSeconds(int ordinal) {
        if(createdTimeSeconds == null)
            return Long.MIN_VALUE;
        return createdTimeSeconds.longValue();
    }

    public Long getCreatedTimeSecondsBoxed(int ordinal) {
        return createdTimeSeconds;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamFileIdentificationTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamFileIdentificationTypeAPI) typeAPI;
    }

}