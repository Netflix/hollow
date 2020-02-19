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
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowSchema;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
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

    public HollowBlobHeader readHeader(RandomAccessFile raf) throws IOException {
        HollowBlobHeader header = new HollowBlobHeader();
        raf.seek(0);

        int headerVersion = raf.readInt();
        if(headerVersion != HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER) {
            throw new IOException("The HollowBlob you are trying to read is incompatible.  "
                    + "The expected Hollow blob version was " + HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER + " but the actual version was " + headerVersion);
        }

        header.setBlobFormatVersion(headerVersion);

        header.setOriginRandomizedTag(raf.readLong());
        header.setDestinationRandomizedTag(raf.readLong());

        int oldBytesToSkip = VarInt.readVInt(raf); /// pre v2.2.0 envelope

        if(oldBytesToSkip != 0) {
            int numSchemas = VarInt.readVInt(raf);

            List<HollowSchema> schemas = new ArrayList<HollowSchema>();
            for(int i=0;i<numSchemas;i++)
                schemas.add(HollowSchema.readFrom(raf));
            header.setSchemas(schemas);

            int bytesToSkip = VarInt.readVInt(raf); /// forwards-compatibility, new data can be added here.
            while(bytesToSkip > 0) {
                int skippedBytes = (int)raf.skipBytes(bytesToSkip);
                if(skippedBytes < 0)
                    throw new EOFException();
                bytesToSkip -= skippedBytes;
            }
        }

        Map<String, String> headerTags = readHeaderTags(raf);
        header.setHeaderTags(headerTags);

        return header;
    }

    public HollowBlobHeader readHeader(InputStream is) throws IOException {
        HollowBlobHeader header = new HollowBlobHeader();
        DataInputStream dis = new DataInputStream(is);

        int headerVersion = dis.readInt();
        if(headerVersion != HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER) {
            throw new IOException("The HollowBlob you are trying to read is incompatible.  "
                    + "The expected Hollow blob version was " + HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER + " but the actual version was " + headerVersion);
        }

        header.setBlobFormatVersion(headerVersion);

        header.setOriginRandomizedTag(dis.readLong());
        header.setDestinationRandomizedTag(dis.readLong());
        
        int oldBytesToSkip = VarInt.readVInt(is); /// pre v2.2.0 envelope
        
        if(oldBytesToSkip != 0) {
            int numSchemas = VarInt.readVInt(is);
            
            List<HollowSchema> schemas = new ArrayList<HollowSchema>();
            for(int i=0;i<numSchemas;i++)
                schemas.add(HollowSchema.readFrom(is));
            header.setSchemas(schemas);

            int bytesToSkip = VarInt.readVInt(is); /// forwards-compatibility, new data can be added here.
            while(bytesToSkip > 0) {
                int skippedBytes = (int)is.skip(bytesToSkip);
                if(skippedBytes < 0)
                    throw new EOFException();
                bytesToSkip -= skippedBytes;
            }
        }

        Map<String, String> headerTags = readHeaderTags(dis);
        header.setHeaderTags(headerTags);

        return header;
    }

    /**
     * Map of string header tags reading.
     *
     * @param dis
     * @throws IOException
     */
    private Map<String, String> readHeaderTags(DataInputStream dis) throws IOException {
        int numHeaderTags = dis.readShort();
        Map<String, String> headerTags = new HashMap<String, String>();
        for (int i = 0; i < numHeaderTags; i++) {
            headerTags.put(dis.readUTF(), dis.readUTF());
        }
        return headerTags;
    }
    private Map<String, String> readHeaderTags(RandomAccessFile raf) throws IOException {
        int numHeaderTags = raf.readShort();
        Map<String, String> headerTags = new HashMap<String, String>();
        for (int i = 0; i < numHeaderTags; i++) {
            headerTags.put(raf.readUTF(), raf.readUTF());
        }
        return headerTags;
    }
}
