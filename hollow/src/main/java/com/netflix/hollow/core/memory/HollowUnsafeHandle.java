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
package com.netflix.hollow.core.memory;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class HollowUnsafeHandle {
    private static final Logger log = Logger.getLogger(HollowUnsafeHandle.class.getName());
    private static final Unsafe unsafe;

    static {
        Field theUnsafe;
        Unsafe u = null;
        try {
            theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            u = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unsafe access failed", e);
        }
        unsafe = u;
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }

}
