/*
 *  Copyright 2016-2021 Netflix, Inc.
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
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.HollowBlobOptionalPartHeader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.schema.HollowSchema;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains serialization logic for the hollow blob headers.
 * 
 * @see HollowBlobHeader
 * 
 */
public class HollowBlobHeaderReader {

    public HollowBlobHeader readHeader(InputStream is) throws IOException {
        return readHeader(HollowBlobInput.serial(is));
    }

    public HollowBlobHeader readHeader(HollowBlobInput in) throws IOException {
        HollowBlobHeader header = new HollowBlobHeader();
        int headerVersion = in.readInt();
        if(headerVersion != HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER) {
            throw new IOException("The HollowBlob you are trying to read is incompatible. "
                    + "The expected Hollow blob version was " + HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER
                    + " but the actual version was " + headerVersion);
        }

        header.setBlobFormatVersion(headerVersion);

        header.setOriginRandomizedTag(in.readLong());
        header.setDestinationRandomizedTag(in.readLong());

        int oldBytesToSkip = VarInt.readVInt(in); /// pre v2.2.0 envelope

        if(oldBytesToSkip != 0) {
            header.setSchemas(readSchemas(in));

            /// forwards-compatibility, new data can be added here.
            readForwardCompatibilityBytes(in, header);
        }

        Map<String, String> headerTags = readHeaderTags(in);
        header.setHeaderTags(headerTags);

        return header;
    }

    public HollowBlobOptionalPartHeader readPartHeader(InputStream is) throws IOException {
        return readPartHeader(HollowBlobInput.serial(is));
    }

    public HollowBlobOptionalPartHeader readPartHeader(HollowBlobInput in) throws IOException {
        int headerVersion = in.readInt();
        if(headerVersion != HollowBlobOptionalPartHeader.HOLLOW_BLOB_PART_VERSION_HEADER) {
            throw new IOException("The HollowBlob optional part you are trying to read is incompatible. "
                    + "The expected Hollow blob version was " + HollowBlobOptionalPartHeader.HOLLOW_BLOB_PART_VERSION_HEADER
                    + " but the actual version was " + headerVersion);
        }

        HollowBlobOptionalPartHeader header = new HollowBlobOptionalPartHeader(in.readUTF());
        
        header.setOriginRandomizedTag(in.readLong());
        header.setDestinationRandomizedTag(in.readLong());

        header.setSchemas(readSchemas(in));

        /// forwards-compatibility, new data can be added here.
        skipForwardCompatibilityBytes(in);

        return header;
    }
    
    private List<HollowSchema> readSchemas(HollowBlobInput in) throws IOException {
        int numSchemas = VarInt.readVInt(in);

        List<HollowSchema> schemas = new ArrayList<HollowSchema>(numSchemas);
        for(int i=0;i<numSchemas;i++)
            schemas.add(HollowSchema.readFrom(in));

        return schemas;
    }
    
    private void skipForwardCompatibilityBytes(HollowBlobInput in) throws IOException, EOFException {
        int bytesToSkip = VarInt.readVInt(in);
        while(bytesToSkip > 0) {
            int skippedBytes = (int)in.skipBytes(bytesToSkip);
            if(skippedBytes < 0)
                throw new EOFException();
            bytesToSkip -= skippedBytes;
        }
    }

    private void readForwardCompatibilityBytes(HollowBlobInput in, HollowBlobHeader header) throws IOException, EOFException {
        int bytesToSkip = VarInt.readVInt(in);

        // Check if there's the hasAppendedSchemaData flag
        if (bytesToSkip > 0) {
            // Read the hasAppendedSchemaData flag
            int flagValue = in.read();
            header.setHasAppendedSchemaData(flagValue == 1);
            bytesToSkip--; // We consumed 1 byte

            // Skip any remaining bytes for forward compatibility
            while(bytesToSkip > 0) {
                int skippedBytes = (int)in.skipBytes(bytesToSkip);
                if(skippedBytes < 0)
                    throw new EOFException();
                bytesToSkip -= skippedBytes;
            }
        }
    }

    /**
     * Map of string header tags reading.
     *
     * @param in the Hollow blob input
     * @throws IOException
     */
    private Map<String, String> readHeaderTags(HollowBlobInput in) throws IOException {
        int numHeaderTags = in.readShort();
        Map<String, String> headerTags = new HashMap<String, String>();
        for (int i = 0; i < numHeaderTags; i++) {
            headerTags.put(in.readUTF(), in.readUTF());
        }
        return headerTags;
    }
}
