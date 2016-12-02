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
package com.netflix.hollow.jsonadapter.chunker;

import com.netflix.hollow.core.util.IntList;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.io.IOUtils;

public class JsonArrayChunkerInputSegment {
    
    // special characters: 0:{ 1:} 2:" 3:\
    
    private final char[] data;
    private final IntList specialCharacterOffsets;
    private int dataLength;
    
    private int specialCharacterIteratorPos = -1;
    
    private volatile boolean offsetsDefined = false;
    
    public JsonArrayChunkerInputSegment(int len) {
        this.data = new char[len];
        this.specialCharacterOffsets = new IntList();
    }
    
    public boolean fill(Reader reader) throws IOException {
        dataLength = IOUtils.read(reader, data);
        return dataLength < data.length;
    }
    
    public synchronized void findSpecialCharacterOffsets() {
        for(int i=0;i<data.length;i++) {
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
        
        offsetsDefined = true;
        this.notify();
    }
    
    public synchronized void waitForDefinedOffsets() throws InterruptedException {
        while(!offsetsDefined) {
            wait();
        }
    }
    
    public boolean nextSpecialCharacter() {
        return ++specialCharacterIteratorPos < specialCharacterOffsets.size();
    }
    
    public int specialCharacterIteratorPosition() {
        return specialCharacterOffsets.get(specialCharacterIteratorPos) & 0x3FFFFFFF;
    }
    
    public char specialCharacter() {
        switch(specialCharacterOffsets.get(specialCharacterIteratorPos) >>> 30) {
        case 0: return '{';
        case 1: return '}';
        case 2: return '\"';
        case 3: return '\\';
        }
        throw new IllegalStateException();
    }
    
    public int length() {
        return dataLength;
    }
    
    public char charAt(int offset) {
        return data[offset];
    }
    
    public int copyTo(char[] dest, int srcPos, int destPos, int len, int maxSrcPos) {
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
