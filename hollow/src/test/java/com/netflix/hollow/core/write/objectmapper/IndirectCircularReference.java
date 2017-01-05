package com.netflix.hollow.core.write.objectmapper;

/**
 * Object model representing an indirect or nested circular reference:
 *
 * E depends on F, F depends on G, and G depends back on E
 */
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
