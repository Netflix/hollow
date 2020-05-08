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
package com.netflix.hollow.core.memory.pool;

import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.SegmentedLongArray;

/**
* An ArraySegmentRecycler is a memory pool.
* <p>
* Hollow pools and reuses memory to minimize GC effects while updating data.  
* This pool of memory is kept arrays on the heap.  Each array in the pool has a fixed length.  
* When a long array or a byte array is required in Hollow, it will stitch together pooled array 
* segments as a {@link SegmentedByteArray} or {@link SegmentedLongArray}.
* These classes encapsulate the details of treating segmented arrays as contiguous ranges of values.
*/
public interface ArraySegmentRecycler {

    public int getLog2OfByteSegmentSize();

    public int getLog2OfLongSegmentSize();

    public long[] getLongArray();

    public void recycleLongArray(long[] arr);

    public byte[] getByteArray();

    public void recycleByteArray(byte[] arr);

    public void swap();

}
