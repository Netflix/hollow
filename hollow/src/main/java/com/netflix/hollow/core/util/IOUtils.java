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
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.memory.encoding.VarInt;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    public static void copyBytes(InputStream is, DataOutputStream[] os, long numBytes) throws IOException {
        byte buf[] = new byte[4096];

        while(numBytes > 0) {
            int numBytesToRead = 4096;
            if(numBytes < 4096)
                numBytesToRead = (int)numBytes;
            int bytesRead = is.read(buf, 0, numBytesToRead);

            for(int i=0;i<os.length;i++) {
                os[i].write(buf, 0, bytesRead);
            }

            numBytes -= bytesRead;
        }
    }

    public static void copySegmentedLongArray(DataInputStream is, DataOutputStream[] os) throws IOException {
        long numLongsToWrite = VarInt.readVLong(is);
        for(int i=0;i<os.length;i++)
            VarInt.writeVLong(os[i], numLongsToWrite);

        copyBytes(is, os, numLongsToWrite * 8);
    }

    public static int copyVInt(DataInputStream is, DataOutputStream[] os) throws IOException {
        int value = VarInt.readVInt(is);
        for(int i=0;i<os.length;i++)
            VarInt.writeVInt(os[i], value);
        return value;
    }

    public static long copyVLong(DataInputStream is, DataOutputStream[] os) throws IOException {
        long value = VarInt.readVLong(is);
        for(int i=0;i<os.length;i++)
            VarInt.writeVLong(os[i], value);
        return value;
    }

}
