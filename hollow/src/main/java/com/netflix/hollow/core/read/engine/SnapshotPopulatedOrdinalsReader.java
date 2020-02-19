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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SnapshotPopulatedOrdinalsReader {

    /**
     * Read populated ordinals as a bit set from a stream, and notify a listener for each populated ordinal.
     *
     * @param raf the data input stream
     * @param listeners the type state listeners
     * @throws IOException if the ordinals cannot be read
     * @author dkoszewnik
     */
    public static void readOrdinals(RandomAccessFile raf, HollowTypeStateListener[] listeners) throws IOException {
        int numLongs = raf.readInt();

        int currentOrdinal = 0;

        for(int i=0;i<numLongs;i++) {
            long l = raf.readLong();
            notifyPopulatedOrdinals(l, currentOrdinal, listeners);
            currentOrdinal += 64;
        }
    }

    private static void notifyPopulatedOrdinals(long l, int ordinal, HollowTypeStateListener[] listeners) {
        if(l == 0)
            return;

        int stopOrdinal = ordinal + 64;

        while(ordinal < stopOrdinal) {
            long mask = 1L << ordinal;
            if((l & mask) != 0) {
                for(int i=0; i<listeners.length; i++) {
                    listeners[i].addedOrdinal(ordinal);
                }
            }
            ordinal++;
        }
    }

    public static void discardOrdinals(DataInputStream dis) throws IOException {
        long numLongs = dis.readInt();
        long bytesToSkip = numLongs * 8;

        while(bytesToSkip > 0)
            bytesToSkip -= dis.skip(bytesToSkip);
    }

}
