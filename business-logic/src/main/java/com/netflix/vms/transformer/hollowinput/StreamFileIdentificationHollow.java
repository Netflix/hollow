package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamFileIdentificationHollow extends HollowObject {

    public StreamFileIdentificationHollow(StreamFileIdentificationDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String _getFilename() {
        return delegate().getFilename(ordinal);
    }

    public boolean _isFilenameEqual(String testValue) {
        return delegate().isFilenameEqual(ordinal, testValue);
    }

    public long _getFileSizeInBytes() {
        return delegate().getFileSizeInBytes(ordinal);
    }

    public Long _getFileSizeInBytesBoxed() {
        return delegate().getFileSizeInBytesBoxed(ordinal);
    }

    public long _getSha1_1() {
        return delegate().getSha1_1(ordinal);
    }

    public Long _getSha1_1Boxed() {
        return delegate().getSha1_1Boxed(ordinal);
    }

    public long _getSha1_2() {
        return delegate().getSha1_2(ordinal);
    }

    public Long _getSha1_2Boxed() {
        return delegate().getSha1_2Boxed(ordinal);
    }

    public long _getSha1_3() {
        return delegate().getSha1_3(ordinal);
    }

    public Long _getSha1_3Boxed() {
        return delegate().getSha1_3Boxed(ordinal);
    }

    public long _getCrc32() {
        return delegate().getCrc32(ordinal);
    }

    public Long _getCrc32Boxed() {
        return delegate().getCrc32Boxed(ordinal);
    }

    public long _getCreatedTimeSeconds() {
        return delegate().getCreatedTimeSeconds(ordinal);
    }

    public Long _getCreatedTimeSecondsBoxed() {
        return delegate().getCreatedTimeSecondsBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamFileIdentificationTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamFileIdentificationDelegate delegate() {
        return (StreamFileIdentificationDelegate)delegate;
    }

}