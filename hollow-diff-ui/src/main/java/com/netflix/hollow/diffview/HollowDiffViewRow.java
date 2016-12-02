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
package com.netflix.hollow.diffview;

import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer.EffigyFieldPair;

public class HollowDiffViewRow {

    private final EffigyFieldPair fieldPair;
    private final int indentation;
    private final int numDescendentRows;
    private final int rowId;
    private boolean unrolled;
    private boolean partiallyUnrolled;
    private boolean visibleForPartialUnroll;
    private final long moreFromRowsBits;
    private final long moreToRowsBits;

    public HollowDiffViewRow(EffigyFieldPair fieldPair, int rowId, int indentation, int numDescendentRows, boolean[] moreFromRows, boolean[] moreToRows) {
        this.fieldPair = fieldPair;
        this.indentation = indentation;
        this.numDescendentRows = numDescendentRows;
        this.rowId = rowId;

        long moreFromRowsBits = 0;
        long moreToRowsBits = 0;

        for(int i=0;i<=indentation;i++) {
            if(moreFromRows[i])
                moreFromRowsBits |= 1 << i;
            if(moreToRows[i])
                moreToRowsBits |= 1 << i;
        }

        this.moreFromRowsBits = moreFromRowsBits;
        this.moreToRowsBits = moreToRowsBits;
    }

    public EffigyFieldPair getFieldPair() {
        return fieldPair;
    }

    public int getRowId() {
        return rowId;
    }

    public int getIndentation() {
        return indentation;
    }

    public void setUnrolled(boolean unrolled) {
        this.unrolled = unrolled;
    }

    public boolean isUnrolled() {
        return unrolled;
    }

    public void setPartiallyUnrolled(boolean partiallyUnrolled) {
        this.partiallyUnrolled = partiallyUnrolled;
    }

    public boolean isPartiallyUnrolled() {
        return partiallyUnrolled;
    }

    public void setVisibleForPartialUnroll(boolean visible) {
        this.visibleForPartialUnroll = visible;
    }

    public boolean isVisibleForPartialUnroll() {
        return visibleForPartialUnroll;
    }

    public int getNumDescendentRows() {
        return numDescendentRows;
    }

    public boolean hasMoreFromRows(int indentation) {
        return (moreFromRowsBits & (1 << indentation)) != 0;
    }

    public boolean hasMoreToRows(int indentation) {
        return (moreToRowsBits & (1 << indentation)) != 0;
    }
}
