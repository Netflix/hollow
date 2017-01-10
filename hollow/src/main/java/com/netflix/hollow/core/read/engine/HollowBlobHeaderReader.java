/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.memory.encoding.VarInt;

import com.netflix.hollow.core.HollowBlobHeader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains serialization logic for the hollow blob headers.
 * 
 * @see HollowBlobHeader
 * 
 */
public class HollowBlobHeaderReader {

    public HollowBlobHeader readHeader(InputStream is) throws IOException {
        HollowBlobHeader header = new HollowBlobHeader();
        DataInputStream dis = new DataInputStream(is);

        int headerVersion = dis.readInt();
        if(headerVersion != HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER) {
            throw new IOException("The HollowBlob you are trying to read is incompatible.  "
                    + "The expected Hollow blob version was " + HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER + " but the actual version was " + headerVersion);
        }

        header.setBlobFormatVersion(headerVersion);

        if(headerVersion == HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER) {
            header.setOriginRandomizedTag(dis.readLong());
            header.setDestinationRandomizedTag(dis.readLong());

            int bytesToSkip = VarInt.readVInt(is); /// forwards-compatibility, new data can be added here.
            while(bytesToSkip > 0) {
                int skippedBytes = (int)is.skip(bytesToSkip);
                if(skippedBytes < 0)
                    throw new EOFException();
                bytesToSkip -= skippedBytes;
            }

        } else {
            //old format header
            //Discard backward compatibility data
            dis.readUTF();
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

    public void copyHeader(DataInputStream dis, DataOutputStream... dos) throws IOException {
        int headerVersion = dis.readInt();
        for(int i=0;i<dos.length;i++)
            dos[i].writeInt(headerVersion);

        if(headerVersion == HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER) {
            long originRandomizedTag = dis.readLong();
            long destRandomizedTag = dis.readLong();

            int bytesToSkip = VarInt.readVInt(dis); /// forwards-compatibility, new data can be added here.
            while(bytesToSkip > 0) {
                int skippedBytes = (int)dis.skip(bytesToSkip);
                if(skippedBytes < 0)
                    throw new EOFException();
                bytesToSkip -= skippedBytes;
            }

            for(int i=0;i<dos.length;i++){
                dos[i].writeLong(originRandomizedTag);
                dos[i].writeLong(destRandomizedTag);
                VarInt.writeVInt(dos[i], 0);
            }
        } else {
            String backwardCompatbilityString = dis.readUTF();
            for(int i=0;i<dos.length;i++)
                dos[i].writeUTF(backwardCompatbilityString);
        }

        int numHeaderTags = dis.readShort();
        for(int i=0;i<dos.length;i++)
            dos[i].writeShort(numHeaderTags);

        for(int i=0;i<numHeaderTags;i++) {
            String headerTagKey = dis.readUTF();
            String headerTagValue = dis.readUTF();

            for(int j=0;j<dos.length;j++) {
                dos[j].writeUTF(headerTagKey);
                dos[j].writeUTF(headerTagValue);
            }
        }
    }
}
