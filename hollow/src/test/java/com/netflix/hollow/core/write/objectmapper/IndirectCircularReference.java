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

/**
 * Object model representing an indirect or nested circular reference:
 *
 * E depends on F, F depends on G, and G depends back on E
 */
@SuppressWarnings("unused")
public class IndirectCircularReference {

    class TypeE {
        private final TypeF f;

        public TypeE(TypeF f) {
            this.f = f;
        }
    }

    class TypeF {
        private final TypeG g;

        public TypeF(TypeG g) {
            this.g = g;
        }
    }

    class TypeG {
        private final TypeE e;

        public TypeG(TypeE e) {
            this.e = e;
        }
    }
}
