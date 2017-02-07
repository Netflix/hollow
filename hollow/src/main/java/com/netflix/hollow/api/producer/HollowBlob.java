package com.netflix.hollow.api.producer;

import java.io.Closeable;
import java.io.OutputStream;

public interface HollowBlob extends Closeable {

    OutputStream getOutputStream();

    void finish();

    @Override
    void close();

}
