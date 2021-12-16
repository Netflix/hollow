package com.netflix.hollow.test.consumer;

import com.netflix.hollow.api.consumer.HollowConsumer.HeaderBlob;
import java.io.IOException;
import java.io.InputStream;

public class TestHeaderBlob extends HeaderBlob {
    private final InputStream inputStream;

    public TestHeaderBlob(long version) {
        this(version, null);
    }

    public TestHeaderBlob(long version, InputStream inputStream) {
        super(version);
        this.inputStream = inputStream;
    }


    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }
}
