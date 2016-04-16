package com.netflix.vms.transformer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// NOTE: Frozen code - do not change.  There is a cloned version in videometadata-snaphost library.  On next vip: need to use standard lz4 lib
public class LZ4VMSStreamHelper {
    static void writeLength(int length, OutputStream os) throws IOException {
        int b1 = ((length & 0xff000000) >> 24);
        int b2 = ((length & 0x00ff0000) >> 16);
        int b3 = ((length & 0x0000ff00) >>  8);
        int b4 = (length & 0xff0000ff);
        os.write(b1);
        os.write(b2);
        os.write(b3);
        os.write(b4);
    }

    // network order, big endian, most significant byte first
    // package scope
    static int readLength(InputStream is) throws IOException {
        int b1 = is.read();
        int b2 = is.read();
        int b3 = is.read();
        int b4 = is.read();

        int length;
        if((-1 == b1) || (-1 == b2) || (-1 == b3) || (-1 == b4)) {
            length = -1;
        }
        else {
            length = ((b1 << 24) | (b2 << 16) | (b3 << 8) | b4);
        }
        return length;
    }
}