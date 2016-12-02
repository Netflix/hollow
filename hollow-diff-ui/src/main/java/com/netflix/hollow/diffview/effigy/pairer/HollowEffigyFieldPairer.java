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
package com.netflix.hollow.diffview.effigy.pairer;

import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigy.CollectionType;
import com.netflix.hollow.diffview.effigy.HollowEffigy.Field;
import java.util.List;

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
    }

    public static List<EffigyFieldPair> pair(HollowEffigy from, HollowEffigy to, long deadlineBeforePairingTimeout) {
        if(from == null || to == null)
            return new HollowEffigyNullPartnerPairer(from, to).pair();

        if(from.getCollectionType() == CollectionType.NONE)
            return new HollowEffigyObjectPairer(from, to).pair();
        if(from.getCollectionType() == CollectionType.MAP)
            return new HollowEffigyMapPairer(from, to, deadlineBeforePairingTimeout).pair();

        return new HollowEffigyCollectionPairer(from, to, deadlineBeforePairingTimeout).pair();
    }
}
