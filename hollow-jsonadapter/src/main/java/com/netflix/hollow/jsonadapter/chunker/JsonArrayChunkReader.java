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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class JsonArrayChunkReader extends Reader {

    private final List<JsonArrayChunkerInputSegment> segments;
    private int lastSegmentEndPos;

    private int currentSegment;
    private int currentSegmentOffset;

    public JsonArrayChunkReader(JsonArrayChunkerInputSegment firstSegment, int startOffset) {
        this.segments = new ArrayList<JsonArrayChunkerInputSegment>(2);
        segments.add(firstSegment);

        this.currentSegment = 0;
        this.currentSegmentOffset = startOffset;
    }

    public void addSegment(JsonArrayChunkerInputSegment segment) {
        segments.add(segment);
    }

    public void setEndOffset(int endOffset) {
        this.lastSegmentEndPos = endOffset;
    }

    @Override
    public int read() {
        while(currentSegment < segments.size()) {
            int maxSrcPos = currentSegment == (segments.size() - 1) ? lastSegmentEndPos : segments.get(currentSegment).length();
            if(currentSegmentOffset < maxSrcPos) {
                return segments.get(currentSegment).charAt(currentSegmentOffset++);
            }
            currentSegment++;
            currentSegmentOffset = 0;
        }

        return -1;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if(currentSegment == segments.size())
            return -1;

        int totalCopiedBytes = 0;
        while(currentSegment < segments.size()) {
            int maxSrcPos = currentSegment == (segments.size() - 1) ? lastSegmentEndPos : segments.get(currentSegment).length();

            int copiedBytes = segments.get(currentSegment).copyTo(cbuf, currentSegmentOffset, off, len, maxSrcPos);
            len -= copiedBytes;
            totalCopiedBytes += copiedBytes;
            currentSegmentOffset += copiedBytes;

            if(len == 0)
                return totalCopiedBytes;

            off += copiedBytes;
            currentSegment++;
            currentSegmentOffset = 0;
        }

        return totalCopiedBytes;
    }

    @Override
    public void close() {
    }


}
