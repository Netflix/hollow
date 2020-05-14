/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.test.consumer;

import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestBlob extends Blob {
    private final InputStream inputStream;

    public TestBlob(long toVersion) {
        super(toVersion);
        this.inputStream = null;
    }

    public TestBlob(long toVersion, InputStream inputStream) {
        super(toVersion);
        this.inputStream = inputStream;
    }

    public TestBlob(long fromVersion, long toVersion) {
        super(fromVersion, toVersion);
        this.inputStream = null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public File getFile() throws IOException {
        throw new UnsupportedOperationException();
    }
}
