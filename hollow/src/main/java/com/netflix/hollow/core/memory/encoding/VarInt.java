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
package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ByteDataBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Variable-byte integer encoding and decoding logic
 *
 * @author dkoszewnik
 */
public class VarInt {


    /**
     * Write a 'null' variable length integer into the supplied {@link ByteDataBuffer}
     */
    public static void writeVNull(ByteDataBuffer buf) {
        buf.write((byte)0x80);
        return;
    }

    /**
     * Encode the specifed long as a variable length integer into the supplied {@link ByteDataBuffer}
     */
    public static void writeVLong(ByteDataBuffer buf, long value) {
        if(value < 0)                                buf.write((byte)0x81);
        if(value > 0xFFFFFFFFFFFFFFL || value < 0)   buf.write((byte)(0x80 | ((value >>> 56) & 0x7FL)));
        if(value > 0x1FFFFFFFFFFFFL || value < 0)    buf.write((byte)(0x80 | ((value >>> 49) & 0x7FL)));
        if(value > 0x3FFFFFFFFFFL || value < 0)      buf.write((byte)(0x80 | ((value >>> 42) & 0x7FL)));
        if(value > 0x7FFFFFFFFL || value < 0)        buf.write((byte)(0x80 | ((value >>> 35) & 0x7FL)));
        if(value > 0xFFFFFFFL || value < 0)          buf.write((byte)(0x80 | ((value >>> 28) & 0x7FL)));
        if(value > 0x1FFFFFL || value < 0)           buf.write((byte)(0x80 | ((value >>> 21) & 0x7FL)));
        if(value > 0x3FFFL || value < 0)             buf.write((byte)(0x80 | ((value >>> 14) & 0x7FL)));
        if(value > 0x7FL || value < 0)               buf.write((byte)(0x80 | ((value >>>  7) & 0x7FL)));

        buf.write((byte)(value & 0x7FL));
    }

    /**
     * Encode the specified long as a variable length integer into the supplied OuputStream 
     */
    public static void writeVLong(OutputStream out, long value) throws IOException {
        if(value < 0)                                out.write((byte)0x81);
        if(value > 0xFFFFFFFFFFFFFFL || value < 0)   out.write((byte)(0x80 | ((value >>> 56) & 0x7FL)));
        if(value > 0x1FFFFFFFFFFFFL || value < 0)    out.write((byte)(0x80 | ((value >>> 49) & 0x7FL)));
        if(value > 0x3FFFFFFFFFFL || value < 0)      out.write((byte)(0x80 | ((value >>> 42) & 0x7FL)));
        if(value > 0x7FFFFFFFFL || value < 0)        out.write((byte)(0x80 | ((value >>> 35) & 0x7FL)));
        if(value > 0xFFFFFFFL || value < 0)          out.write((byte)(0x80 | ((value >>> 28) & 0x7FL)));
        if(value > 0x1FFFFFL || value < 0)           out.write((byte)(0x80 | ((value >>> 21) & 0x7FL)));
        if(value > 0x3FFFL || value < 0)             out.write((byte)(0x80 | ((value >>> 14) & 0x7FL)));
        if(value > 0x7FL || value < 0)               out.write((byte)(0x80 | ((value >>>  7) & 0x7FL)));

        out.write((byte)(value & 0x7FL));
    }

    /**
     * Encode the specified int as a variable length integer into the supplied {@link ByteDataBuffer} 
     */
    public static void writeVInt(ByteDataBuffer buf, int value) {
        if(value > 0x0FFFFFFF || value < 0) buf.write((byte)(0x80 | ((value >>> 28))));
        if(value > 0x1FFFFF || value < 0)   buf.write((byte)(0x80 | ((value >>> 21) & 0x7F)));
        if(value > 0x3FFF || value < 0)     buf.write((byte)(0x80 | ((value >>> 14) & 0x7F)));
        if(value > 0x7F || value < 0)       buf.write((byte)(0x80 | ((value >>>  7) & 0x7F)));

        buf.write((byte)(value & 0x7F));
    }

    /**
     * Encode the specified int as a variable length integer into the supplied OutputStream 
     */
    public static void writeVInt(OutputStream out, int value) throws IOException {
        if(value > 0x0FFFFFFF || value < 0) out.write((byte)(0x80 | ((value >>> 28))));
        if(value > 0x1FFFFF || value < 0)   out.write((byte)(0x80 | ((value >>> 21) & 0x7F)));
        if(value > 0x3FFF || value < 0)     out.write((byte)(0x80 | ((value >>> 14) & 0x7F)));
        if(value > 0x7F || value < 0)       out.write((byte)(0x80 | ((value >>>  7) & 0x7F)));

        out.write((byte)(value & 0x7F));
    }
    
    /**
     * Write the value as a VarInt into the array, starting at the specified position.
     * 
     * Returns the next position after the VarInt has been written.
     */
    public static int writeVInt(byte data[], int pos, int value) {
        if(value > 0x0FFFFFFF || value < 0) data[pos++] = ((byte)(0x80 | ((value >>> 28))));
        if(value > 0x1FFFFF || value < 0)   data[pos++] = ((byte)(0x80 | ((value >>> 21) & 0x7F)));
        if(value > 0x3FFF || value < 0)     data[pos++] = ((byte)(0x80 | ((value >>> 14) & 0x7F)));
        if(value > 0x7F || value < 0)       data[pos++] = ((byte)(0x80 | ((value >>>  7) & 0x7F)));
        
        data[pos++] = (byte)(value & 0x7F);
        
        return pos;
    }

    /**
     * Determine whether or not the value at the specified position in the supplied {@link ByteData} is 
     * a 'null' variable length integer.
     */
    public static boolean readVNull(ByteData arr, long position) {
        return arr.get(position) == (byte)0x80;
    }

    /**
     * Read a variable length integer from the supplied {@link ByteData} starting at the specified position.
     */
    public static int readVInt(ByteData arr, long position) {
        byte b = arr.get(position++);

        if(b == (byte) 0x80)
            throw new RuntimeException("Attempting to read null value as int");

        int value = b & 0x7F;
        while ((b & 0x80) != 0) {
          b = arr.get(position++);
          value <<= 7;
          value |= (b & 0x7F);
        }

        return value;
    }

    /**
     * Read a variable length integer from the supplied InputStream
     */
    public static int readVInt(InputStream in) throws IOException {
        byte b = (byte)in.read();

        if(b == (byte) 0x80)
            throw new RuntimeException("Attempting to read null value as int");

        int value = b & 0x7F;
        while ((b & 0x80) != 0) {
          b = (byte)in.read();
          value <<= 7;
          value |= (b & 0x7F);
        }

        return value;
    }

    /**
     * Read a variable length long from the supplied {@link ByteData} starting at the specified position. 
     */
    public static long readVLong(ByteData arr, long position) {
        byte b = arr.get(position++);

        if(b == (byte) 0x80)
            throw new RuntimeException("Attempting to read null value as long");

        long value = b & 0x7F;
        while ((b & 0x80) != 0) {
          b = arr.get(position++);
          value <<= 7;
          value |= (b & 0x7F);
        }

        return value;
    }

    /**
     * Determine the size (in bytes) of the variable length long in the supplied {@link ByteData}, starting at the specified position.  
     */
    public static int nextVLongSize(ByteData arr, long position) {
        byte b = arr.get(position++);

        if(b == (byte) 0x80)
            return 1;

        int length = 1;

        while((b & 0x80) != 0) {
            b = arr.get(position++);
            length++;
        }

        return length;
    }

    /**
     * Read a variable length long from the supplied InputStream.
     */
    public static long readVLong(InputStream in) throws IOException {
        byte b = (byte)in.read();

        if(b == (byte) 0x80)
            throw new RuntimeException("Attempting to read null value as long");

        long value = b & 0x7F;
        while ((b & 0x80) != 0) {
          b = (byte)in.read();
          value <<= 7;
          value |= (b & 0x7F);
        }

        return value;
    }


    /**
     * Determine the size (in bytes) of the specified value when encoded as a variable length integer.
     */
    public static int sizeOfVInt(int value) {
        if(value < 0)
            return 5;
        if(value < 0x80)
            return 1;
        if(value < 0x4000)
            return 2;
        if(value < 0x200000)
            return 3;
        if(value < 0x10000000)
            return 4;
        return 5;
    }

    /**
     * Determine the size (in bytes) of the specified value when encoded as a variable length integer.
     */
    public static int sizeOfVLong(long value) {
        if(value < 0L)
            return 10;
        if(value < 0x80L)
            return 1;
        if(value < 0x4000L)
            return 2;
        if(value < 0x200000L)
            return 3;
        if(value < 0x10000000L)
            return 4;
        if(value < 0x800000000L)
            return 5;
        if(value < 0x40000000000L)
            return 6;
        if(value < 0x2000000000000L)
            return 7;
        if(value < 0x100000000000000L)
            return 8;
        return 9;
    }

    /**
     * Count the number of variable length integers encoded in the supplied {@link ByteData} in the specified range.
     */
    public static int countVarIntsInRange(ByteData byteData, long fieldPosition, int length) {
        int numInts = 0;

        boolean insideInt = false;

        for(int i=0;i<length;i++) {
            byte b = byteData.get(fieldPosition + i);

            if((b & 0x80) == 0) {
                numInts++;
                insideInt = false;
            } else if(!insideInt && b == (byte)0x80) {
                numInts++;
            } else {
                insideInt = true;
            }
        }

        return numInts;
    }

}
