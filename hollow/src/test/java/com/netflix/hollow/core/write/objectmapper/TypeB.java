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
package com.netflix.hollow.core.write.objectmapper;

public class TypeB {

    private final short b1;
    private final long b2;
    private final float b3;
    private final char[] b4;
    private final byte[] b5;

    public TypeB(short b1, long b2, float b3, char[] b4, byte[] b5) {
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.b4 = b4;
        this.b5 = b5;
    }

    public short getB1() {
        return b1;
    }

    public long getB2() {
        return b2;
    }

    public float getB3() {
        return b3;
    }

    public char[] getB4() {
        return b4;
    }

    public byte[] getB5() {
        return b5;
    }

}
