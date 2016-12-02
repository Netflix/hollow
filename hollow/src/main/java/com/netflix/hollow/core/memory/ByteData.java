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
package com.netflix.hollow.core.memory;

/**
 * This interface is used to hide the underlying implementation of a range of bytes.
 *  
 * This is useful because Hollow often uses pooled arrays to back range of bytes.  
 *
 * @see SegmentedByteArray
 *
 * @author dkoszewnik
 *
 */
public interface ByteData {

    byte get(long position);

}
