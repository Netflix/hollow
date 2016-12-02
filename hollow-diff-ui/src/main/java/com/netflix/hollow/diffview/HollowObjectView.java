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

import java.util.List;

public abstract class HollowObjectView {

    private static final int MAX_INITIAL_VISIBLE_ROWS_BEFORE_COLLAPSING_DIFFS = 300;

    private final List<HollowDiffViewRow> rows;

    public HollowObjectView(List<HollowDiffViewRow> rows) {
        this.rows = rows;
    }

    public List<HollowDiffViewRow> getRows() {
        return rows;
    }

    public void resetView() {
        boolean hasAnyDiff = false;

        for(HollowDiffViewRow row : rows) {
            if(row.getFieldPair().isDiff()) {
                row.setVisibleForPartialUnroll(true);
                row.setUnrolled(true);
                hasAnyDiff = true;
            } else if(hasAnyDiffChildren(rows, row.getRowId(), false)) {
                row.setVisibleForPartialUnroll(true);
                if(hasAnyNonDiffChildren(rows, row.getRowId(), false)) {
                    row.setPartiallyUnrolled(true);
                } else {
                    row.setUnrolled(true);
                }
            }
        }

        int visibleRows = 1 + countVisibleDescendents(rows, 0);

        if(visibleRows > MAX_INITIAL_VISIBLE_ROWS_BEFORE_COLLAPSING_DIFFS) {
            for(HollowDiffViewRow row : rows) {
                if(row.getFieldPair().isDiff()) {
                    collapseSelfAndAllDescendents(rows, row.getRowId());
                }
            }
        }

        if(!hasAnyDiff) {
            for(HollowDiffViewRow row : rows) {
                if(row.getFieldPair().isDiff()) {
                    row.setVisibleForPartialUnroll(true);
                    row.setUnrolled(true);
                    hasAnyDiff = true;
                } else if(row.getFieldPair().getFromIdx() != row.getFieldPair().getToIdx()) {
                    row.setVisibleForPartialUnroll(true);
                } else if(hasAnyDiffChildren(rows, row.getRowId(), true)) {
                    row.setVisibleForPartialUnroll(true);
                    if(hasAnyNonDiffChildren(rows, row.getRowId(), true)) {
                        row.setPartiallyUnrolled(true);
                    } else {
                        row.setUnrolled(true);
                    }
                }
            }
        }
    }

    private boolean hasAnyDiffChildren(List<HollowDiffViewRow> rows, int rowId, boolean showOrderingDiffs) {
        HollowDiffViewRow row = rows.get(rowId);
        for(int i=rowId + 1;i<=rowId + row.getNumDescendentRows();i++) {
            if(rows.get(i).getFieldPair().isDiff())
                return true;
            if(showOrderingDiffs && rows.get(i).getFieldPair().getFromIdx() != rows.get(i).getFieldPair().getToIdx())
                return true;
        }
        return false;
    }

    private boolean hasAnyNonDiffChildren(List<HollowDiffViewRow> rows, int rowId, boolean showOrderingDiffs) {
        HollowDiffViewRow parentRow = rows.get(rowId);

        if(parentRow.getFieldPair().isLeafNode())
            return false;

        for(int i=rowId + 1;i<=rowId + parentRow.getNumDescendentRows();i++) {
            HollowDiffViewRow currentRow = rows.get(i);
            if(!currentRow.getFieldPair().isDiff() && !hasAnyDiffChildren(rows, i, showOrderingDiffs)) {
                if(!showOrderingDiffs || currentRow.getFieldPair().getFromIdx() == currentRow.getFieldPair().getToIdx())
                    return true;
            }

            if(!currentRow.getFieldPair().isLeafNode())
                i += currentRow.getNumDescendentRows();
        }
        return false;
    }

    private int countVisibleDescendents(List<HollowDiffViewRow> rows, int beginRowId) {
        HollowDiffViewRow parentRow = rows.get(beginRowId);
        int endRowId = beginRowId + rows.get(beginRowId).getNumDescendentRows();

        int visibleRows = 0;

        for(int i=beginRowId+1;i < endRowId;i++) {
            HollowDiffViewRow currentRow = rows.get(i);
            boolean rowIsVisible = parentRow.isUnrolled() || currentRow.isVisibleForPartialUnroll();

            if(rowIsVisible) {
                if(!currentRow.getFieldPair().isLeafNode()) {
                    if(currentRow.isUnrolled() || currentRow.isPartiallyUnrolled())
                        visibleRows += countVisibleDescendents(rows, i);
                }

                visibleRows++;
            }

            if(!currentRow.getFieldPair().isLeafNode())
                i += currentRow.getNumDescendentRows();
        }
        return visibleRows;
    }

    private boolean collapseSelfAndAllDescendents(List<HollowDiffViewRow> rows, int rowId) {
        HollowDiffViewRow row = rows.get(rowId);
        for(int i=rowId;i<=rowId + row.getNumDescendentRows();i++) {
            rows.get(i).setUnrolled(false);
        }
        return false;
    }

}
