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
package com.netflix.hollow.diffview.effigy.pairer;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigy.Field;
import java.util.List;
import java.util.Map;

public abstract class HollowEffigyFieldPairer {

    protected final HollowEffigy from;
    protected final HollowEffigy to;

    public HollowEffigyFieldPairer(HollowEffigy from, HollowEffigy to) {
        this.from = from;
        this.to = to;
    }

    public abstract List<EffigyFieldPair> pair();

    public static class EffigyFieldPair {
        private final HollowEffigy.Field from;
        private final HollowEffigy.Field to;
        private final int fromIdx;
        private final int toIdx;
        private final boolean isDiff;

        public EffigyFieldPair(Field from, Field to, int fromIdx, int toIdx) {
            this.from = from;
            this.to = to;
            this.fromIdx = fromIdx;
            this.toIdx = toIdx;

            this.isDiff = calculateIsDiff();

        }

        private boolean calculateIsDiff() {
            if((from == null && to != null) || (from != null && to == null))
                return true;
            if(from.getValue() == null)
                return to.getValue() != null;
            if(isLeafNode())
                return !from.getValue().equals(to.getValue());
            return false;
        }

        public HollowEffigy.Field getFrom() {
            return from;
        }

        public HollowEffigy.Field getTo() {
            return to;
        }

        public int getFromIdx() {
            return fromIdx;
        }

        public int getToIdx() {
            return toIdx;
        }

        public boolean isLeafNode() {
            return (from != null && from.getValue() != null) ? from.isLeafNode() : to == null ? true : to.isLeafNode();
        }

        public boolean isDiff() {
            return isDiff;
        }

        public boolean isOrderingDiff() {
            return fromIdx != toIdx;
        }
    }

    public static List<EffigyFieldPair> pair(HollowEffigy from, HollowEffigy to, Map<String, PrimaryKey> matchHints) {
        if(from == null || to == null)
            return new HollowEffigyNullPartnerPairer(from, to).pair();

        if(from.getDataAccess() == null)
            return new HollowEffigyObjectPairer(from, to).pair();

        HollowSchema schema = from.getDataAccess().getSchema();

        switch(schema.getSchemaType()) {
            case OBJECT:
                return new HollowEffigyObjectPairer(from, to).pair();
            case MAP:
                String keyType = ((HollowMapSchema) schema).getKeyType();
                return new HollowEffigyMapPairer(from, to, matchHints.get(keyType)).pair();
            case LIST:
            case SET:
                String elementType = ((HollowCollectionSchema) schema).getElementType();
                return new HollowEffigyCollectionPairer(from, to, matchHints.get(elementType)).pair();
        }

        throw new IllegalArgumentException("I don't know how to pair fields for type " + schema.getName() + "(" + schema.getSchemaType() + ")");
    }
}
