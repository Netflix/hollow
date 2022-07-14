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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.HollowBlobOptionalPartHeader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowSchema;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class HollowBlobHeaderWriter {
    /**
     * Write the header to the data output stream
     * @param header the blob header
     * @param dos the data output stream to write the blov header
     * @throws IOException if the blob header could not be written
     */
    public void writeHeader(HollowBlobHeader header, DataOutputStream dos) throws IOException {
        /// save 4 bytes to indicate FastBlob version header.  This will be changed to indicate backwards incompatibility.
        dos.writeInt(HollowBlobHeader.HOLLOW_BLOB_VERSION_HEADER);

        /// Write randomized tag data -- every state gets a random 64-bit tag.
        /// When attempting to apply a delta, the originating state's random 64-bit tag is compared against the current 64-bit tag.
        /// This prevents deltas from being applied to incorrect states.
        dos.writeLong(header.getOriginRandomizedTag());
        dos.writeLong(header.getDestinationRandomizedTag());

        /// write the schemas contained in this blob to the stream in the pre v2.2.0 backwards compatibility envelope
        ByteArrayOutputStream schemasStream = new ByteArrayOutputStream();
        VarInt.writeVInt(schemasStream, header.getSchemas().size());
        for(HollowSchema schema : header.getSchemas())
            schema.writeTo(schemasStream);
        byte[] schemasData = schemasStream.toByteArray();

        VarInt.writeVInt(dos, schemasData.length + 1); // plus one byte for new backwards compatibility envelope.
        dos.write(schemasData);

        ///backwards compatibility -- new data can be added here by first indicating number of bytes used, will be skipped by existing readers.
        VarInt.writeVInt(dos, 0);

        /// write the header tags -- intended to include input source data versions
        dos.writeShort(header.getHeaderTags().size());

        for(Map.Entry<String, String> headerTag : header.getHeaderTags().entrySet()) {
            dos.writeUTF(headerTag.getKey());
            dos.writeUTF(headerTag.getValue());
        }
    }

    public void writePartHeader(HollowBlobOptionalPartHeader header, DataOutputStream dos) throws IOException {
        dos.writeInt(HollowBlobOptionalPartHeader.HOLLOW_BLOB_PART_VERSION_HEADER);

        dos.writeUTF(header.getPartName());

        dos.writeLong(header.getOriginRandomizedTag());
        dos.writeLong(header.getDestinationRandomizedTag());

        VarInt.writeVInt(dos, header.getSchemas().size());
        for(HollowSchema schema : header.getSchemas())
            schema.writeTo(dos);

        ///backwards compatibility -- new data can be added here by first indicating number of bytes used, will be skipped by existing readers.
        VarInt.writeVInt(dos, 0);
    }

}
