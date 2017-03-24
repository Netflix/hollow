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

import java.util.ArrayList;

import java.util.List;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer.EffigyFieldPair;

public class HollowDiffViewRow {

    private final int[] rowPath;
    private final EffigyFieldPair fieldPair;

    private boolean isVisible;
    
    private final List<HollowDiffViewRow> children;
    
    private final long moreFromRowsBits;
    private final long moreToRowsBits;

    public HollowDiffViewRow(EffigyFieldPair fieldPair, int[] rowPath, boolean[] moreFromRows, boolean[] moreToRows) {
        this.fieldPair = fieldPair;
        this.rowPath = rowPath;

        long moreFromRowsBits = 0;
        long moreToRowsBits = 0;

        for(int i=0;i<=rowPath.length;i++) {
            if(moreFromRows[i])
                moreFromRowsBits |= 1 << i;
            if(moreToRows[i])
                moreToRowsBits |= 1 << i;
        }

        this.children = new ArrayList<HollowDiffViewRow>();
        this.isVisible = false;
        
        this.moreFromRowsBits = moreFromRowsBits;
        this.moreToRowsBits = moreToRowsBits;
    }
    
    public EffigyFieldPair getFieldPair() {
        return fieldPair;
    }

    public int[] getRowPath() {
        return rowPath;
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
        if(children.isEmpty())
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

    void addChild(HollowDiffViewRow child) {
        children.add(child);
    }

    public List<HollowDiffViewRow> getChildren() {
        return children;
    }

    public boolean hasMoreFromRows(int indentation) {
        return (moreFromRowsBits & (1 << indentation)) != 0;
    }

    public boolean hasMoreToRows(int indentation) {
        return (moreToRowsBits & (1 << indentation)) != 0;
    }
    
    public static enum Action {
        COLLAPSE,
        UNCOLLAPSE,
        PARTIAL_UNCOLLAPSE,
        NONE
    }
}
