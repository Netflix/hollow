package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamFileIdentificationDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamFileIdentificationDelegate {

    private final StreamFileIdentificationTypeAPI typeAPI;

    public StreamFileIdentificationDelegateLookupImpl(StreamFileIdentificationTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getFilename(int ordinal) {
        return typeAPI.getFilename(ordinal);
    }

    public boolean isFilenameEqual(int ordinal, String testValue) {
        return typeAPI.isFilenameEqual(ordinal, testValue);
    }

    public long getFileSizeInBytes(int ordinal) {
        return typeAPI.getFileSizeInBytes(ordinal);
    }

    public Long getFileSizeInBytesBoxed(int ordinal) {
        return typeAPI.getFileSizeInBytesBoxed(ordinal);
    }

    public long getSha1_1(int ordinal) {
        return typeAPI.getSha1_1(ordinal);
    }

    public Long getSha1_1Boxed(int ordinal) {
        return typeAPI.getSha1_1Boxed(ordinal);
    }

    public long getSha1_2(int ordinal) {
        return typeAPI.getSha1_2(ordinal);
    }

    public Long getSha1_2Boxed(int ordinal) {
        return typeAPI.getSha1_2Boxed(ordinal);
    }

    public long getSha1_3(int ordinal) {
        return typeAPI.getSha1_3(ordinal);
    }

    public Long getSha1_3Boxed(int ordinal) {
        return typeAPI.getSha1_3Boxed(ordinal);
    }

    public long getCrc32(int ordinal) {
        return typeAPI.getCrc32(ordinal);
    }

    public Long getCrc32Boxed(int ordinal) {
        return typeAPI.getCrc32Boxed(ordinal);
    }

    public long getCreatedTimeSeconds(int ordinal) {
        return typeAPI.getCreatedTimeSeconds(ordinal);
    }

    public Long getCreatedTimeSecondsBoxed(int ordinal) {
        return typeAPI.getCreatedTimeSecondsBoxed(ordinal);
    }

    public StreamFileIdentificationTypeAPI getTypeAPI() {
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