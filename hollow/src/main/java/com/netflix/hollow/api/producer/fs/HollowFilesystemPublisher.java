/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.producer.fs;

import com.netflix.hollow.api.producer.HollowProducer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HollowFilesystemPublisher implements HollowProducer.Publisher {
    
    private final Path blobStorePath;

    // TODO: deprecate in Hollow 3.0.0
    // @Deprecated
    public HollowFilesystemPublisher(File blobStoreDir) {
        this(blobStoreDir.toPath());
    }

    /**
     * @since 2.12.0
     */
    public HollowFilesystemPublisher(Path blobStorePath) {
        this.blobStorePath = blobStorePath;
        try {
            if(!Files.exists(this.blobStorePath)){
                Files.createDirectories(this.blobStorePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create folder for publisher; path=" + this.blobStorePath, e);
        }
    }

    @Override
    public void publish(HollowProducer.Blob blob) {
        Path destination = null;
        
        switch(blob.getType()) {
        case SNAPSHOT:
            destination = blobStorePath.resolve(String.format("%s-%d", blob.getType().prefix, blob.getToVersion()));
            break;
        case DELTA:
        case REVERSE_DELTA:
            destination = blobStorePath.resolve(String.format("%s-%d-%d", blob.getType().prefix, blob.getFromVersion(), blob.getToVersion()));
            break;
        }
            
        try(
                InputStream is = blob.newInputStream();
                OutputStream os = Files.newOutputStream(destination);
        ) {
            byte buf[] = new byte[4096];
            int n = 0;
            while (-1 != (n = is.read(buf)))
                os.write(buf, 0, n);
        } catch(IOException e) {
            throw new RuntimeException("Unable to publish file!", e);
        }
    }
}
