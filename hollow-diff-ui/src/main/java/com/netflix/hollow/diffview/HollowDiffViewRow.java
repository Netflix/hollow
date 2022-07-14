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
package com.netflix.hollow.diffview;

import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer.EffigyFieldPair;
import java.util.List;

public class HollowDiffViewRow {

    private final int[] rowPath;
    private final EffigyFieldPair fieldPair;
    private final HollowDiffViewRow parent;
    private final HollowObjectDiffViewGenerator viewGenerator;

    private boolean isVisible;

    private List<HollowDiffViewRow> children;

    private long moreFromRowsBits = -1;
    private long moreToRowsBits = -1;

    public HollowDiffViewRow(EffigyFieldPair fieldPair, int[] rowPath, HollowDiffViewRow parent, HollowObjectDiffViewGenerator viewGenerator) {
        this.fieldPair = fieldPair;
        this.rowPath = rowPath;
        this.parent = parent;
        this.viewGenerator = viewGenerator;
        this.isVisible = false;
    }

    public boolean areChildrenPopulated() {
        return children != null;
    }

    public EffigyFieldPair getFieldPair() {
        return fieldPair;
    }

    public int[] getRowPath() {
        return rowPath;
    }

    public HollowDiffViewRow getParent() {
        return parent;
    }

    public int getIndentation() {
        return rowPath.length;
    }

    public void setVisibility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Action getAvailableAction() {
        if(getChildren().isEmpty())
            return Action.NONE;

        boolean foundVisibleChild = false;
        boolean foundInvisibleChild = false;

        for(HollowDiffViewRow child : children) {
            if(child.isVisible()) {
                if(foundInvisibleChild)
                    return Action.PARTIAL_UNCOLLAPSE;
                foundVisibleChild = true;
            } else {
                if(foundVisibleChild)
                    return Action.PARTIAL_UNCOLLAPSE;
                foundInvisibleChild = true;
            }
        }

        return foundVisibleChild ? Action.COLLAPSE : Action.UNCOLLAPSE;
    }

    public List<HollowDiffViewRow> getChildren() {
        if(children == null) {
            children = viewGenerator.traverseEffigyToCreateViewRows(this);
        }
        return children;
    }

    public boolean hasMoreFromRows(int indentation) {
        if(moreFromRowsBits == -1)
            buildMoreRowsBits();
        return (moreFromRowsBits & (1 << indentation)) != 0;
    }

    public boolean hasMoreToRows(int indentation) {
        if(moreToRowsBits == -1)
            buildMoreRowsBits();
        return (moreToRowsBits & (1 << indentation)) != 0;
    }

    private void buildMoreRowsBits() {
        HollowDiffViewRow ancestor = this.parent;
        moreFromRowsBits = 0;
        moreToRowsBits = 0;

        for(int i = rowPath.length; i >= 1; i--) {
            if(moreRows(ancestor, rowPath[i - 1], true))
                moreFromRowsBits |= 1 << i;
            if(moreRows(ancestor, rowPath[i - 1], false))
                moreToRowsBits |= 1 << i;
            ancestor = ancestor.getParent();
        }
    }

    private boolean moreRows(HollowDiffViewRow parent, int childIdx, boolean from) {
        for(int i = childIdx + 1; i < parent.getChildren().size(); i++) {
            EffigyFieldPair fieldPair = parent.getChildren().get(i).getFieldPair();
            if((from && fieldPair.getFrom() != null) || (!from && fieldPair.getTo() != null))
                return true;
        }
        return false;
    }

    public static enum Action {
        COLLAPSE,
        UNCOLLAPSE,
        PARTIAL_UNCOLLAPSE,
        NONE
    }
}
