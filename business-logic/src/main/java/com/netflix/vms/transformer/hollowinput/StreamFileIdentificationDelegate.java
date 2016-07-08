package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamFileIdentificationDelegate extends HollowObjectDelegate {

    public String getFilename(int ordinal);

    public boolean isFilenameEqual(int ordinal, String testValue);

    public long getFileSizeInBytes(int ordinal);

    public Long getFileSizeInBytesBoxed(int ordinal);

    public long getSha1_1(int ordinal);

    public Long getSha1_1Boxed(int ordinal);

    public long getSha1_2(int ordinal);

    public Long getSha1_2Boxed(int ordinal);

    public long getSha1_3(int ordinal);

    public Long getSha1_3Boxed(int ordinal);

    public long getCrc32(int ordinal);

    public Long getCrc32Boxed(int ordinal);

    public long getCreatedTimeSeconds(int ordinal);

    public Long getCreatedTimeSecondsBoxed(int ordinal);

    public StreamFileIdentificationTypeAPI getTypeAPI();

}