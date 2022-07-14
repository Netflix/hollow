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
package com.netflix.hollow.jsonadapter.chunker;

import com.netflix.hollow.core.util.IntList;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.io.IOUtils;

class JsonArrayChunkerInputSegment {

    // special characters: 0:{ 1:} 2:" 3:\
    
    private final char[] data;
    private final IntList specialCharacterOffsets = new IntList();

    private int dataLength;
    private int specialCharacterIteratorPos = -1;

    JsonArrayChunkerInputSegment(int len) {
        this.data = new char[len];
    }

    boolean fill(Reader reader) throws IOException {
        dataLength = IOUtils.read(reader, data);
        return dataLength < data.length;
    }

    JsonArrayChunkerInputSegment findSpecialCharacterOffsets() {
        for(int i = 0; i < data.length; i++) {
            switch(data[i]) {
                case '{':
                    specialCharacterOffsets.add(i);
                    break;
                case '}':
                    specialCharacterOffsets.add(i | (1 << 30));
                    break;
                case '\"':
                    specialCharacterOffsets.add(i | (2 << 30));
                    break;
                case '\\':
                    specialCharacterOffsets.add(i | (3 << 30));
                    break;
                default:
            }
        }
        return this;
    }

    boolean nextSpecialCharacter() {
        return ++specialCharacterIteratorPos < specialCharacterOffsets.size();
    }

    int specialCharacterIteratorPosition() {
        return specialCharacterOffsets.get(specialCharacterIteratorPos) & 0x3FFFFFFF;
    }

    char specialCharacter() {
        switch(specialCharacterOffsets.get(specialCharacterIteratorPos) >>> 30) {
            case 0: return '{';
            case 1: return '}';
            case 2: return '\"';
            case 3: return '\\';
        }
        throw new IllegalStateException();
    }

    int length() {
        return dataLength;
    }

    char charAt(int offset) {
        return data[offset];
    }

    int copyTo(char[] dest, int srcPos, int destPos, int len, int maxSrcPos) {
        int bytesAvailable = maxSrcPos - srcPos;
        if(bytesAvailable >= len) {
            System.arraycopy(data, srcPos, dest, destPos, len);
            return len;
        } else {
            System.arraycopy(data, srcPos, dest, destPos, bytesAvailable);
            return bytesAvailable;
        }
    }

}
