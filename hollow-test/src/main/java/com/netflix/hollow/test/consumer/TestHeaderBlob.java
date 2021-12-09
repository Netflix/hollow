package com.netflix.hollow.test.consumer;

import com.netflix.hollow.api.consumer.HollowConsumer.HeaderBlob;

import java.io.IOException;
import java.io.InputStream;

public class TestHeaderBlob extends HeaderBlob {
    private final InputStream inputStream;

    public TestHeaderBlob(long fromVersion, long toVersion) {
        this(fromVersion, toVersion, null);
    }

    public TestHeaderBlob(long fromVersion, long toVersion, InputStream inputStream) {
        super(fromVersion, toVersion);
        this.inputStream = inputStream;
    }


    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }
}
