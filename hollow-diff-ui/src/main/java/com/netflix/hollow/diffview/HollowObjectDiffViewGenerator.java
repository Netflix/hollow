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

import java.util.Arrays;

import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.diffview.effigy.CustomHollowEffigyFactory;
import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigyFactory;
import com.netflix.hollow.diffview.effigy.HollowRecordDiffUI;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer.EffigyFieldPair;
import java.util.List;

public class HollowObjectDiffViewGenerator {

    private final long MAX_TIME_BEFORE_PAIRING_TIMEOUT = 150000L;

    private final HollowDataAccess fromDataAccess;
    private final HollowDataAccess toDataAccess;
    private final HollowRecordDiffUI diffUI;
    private final String typeName;
    private final int fromOrdinal;
    private final int toOrdinal;
    private final long deadlineBeforePairingTimeout;

    public HollowObjectDiffViewGenerator(HollowDataAccess fromDataAccess, HollowDataAccess toDataAccess, HollowRecordDiffUI diffUI, String typeName, int fromOrdinal, int toOrdinal) {
        this.fromDataAccess = fromDataAccess;
        this.toDataAccess = toDataAccess;
        this.diffUI = diffUI;
        this.typeName = typeName;
        this.fromOrdinal = fromOrdinal;
        this.toOrdinal = toOrdinal;
        this.deadlineBeforePairingTimeout = System.currentTimeMillis() + MAX_TIME_BEFORE_PAIRING_TIMEOUT;
    }

    public HollowDiffViewRow getHollowDiffViewRows() {
        HollowEffigy fromEffigy, toEffigy;

        if(diffUI != null && diffUI.getCustomHollowEffigyFactory(typeName) != null) {
            CustomHollowEffigyFactory effigyFactory = diffUI.getCustomHollowEffigyFactory(typeName);
            synchronized(effigyFactory) {
                effigyFactory.setFromHollowRecord(fromDataAccess.getTypeDataAccess(typeName), fromOrdinal);
                effigyFactory.setToHollowRecord(toDataAccess.getTypeDataAccess(typeName), toOrdinal);
                effigyFactory.generateEffigies();
                fromEffigy = effigyFactory.getFromEffigy();
                toEffigy = effigyFactory.getToEffigy();
            }
        } else {
            HollowEffigyFactory effigyFactory = new HollowEffigyFactory();
            fromEffigy = fromOrdinal == -1 ? null : effigyFactory.effigy(fromDataAccess, typeName, fromOrdinal);
            toEffigy = toOrdinal == -1 ? null : effigyFactory.effigy(toDataAccess, typeName, toOrdinal);
        }

        HollowDiffViewRow rootRow = createRootRow(fromEffigy, toEffigy);
        traverseEffigyToCreateViewRows(rootRow, fromEffigy, toEffigy, new boolean[64], new boolean[64]);

        return rootRow;
    }

    private void traverseEffigyToCreateViewRows(HollowDiffViewRow parent, HollowEffigy from, HollowEffigy to, boolean moreFromRows[], boolean moreToRows[]) {
        List<EffigyFieldPair> pairs = HollowEffigyFieldPairer.pair(from, to, diffUI.getMatchHints(), deadlineBeforePairingTimeout);

        for(int i=0;i<pairs.size();i++) {
            EffigyFieldPair pair = pairs.get(i);

            int indentation = parent.getRowPath().length + 1;
            
            moreFromRows[indentation] = moreRows(pairs, true, i);
            moreToRows[indentation] = moreRows(pairs, false, i);

            HollowDiffViewRow newRow;

            int rowPath[] = Arrays.copyOf(parent.getRowPath(), indentation); 
            rowPath[rowPath.length - 1] = i; 
            
            if(pair.isLeafNode()) {
                newRow = new HollowDiffViewRow(pair, rowPath, moreFromRows, moreToRows);
            } else {
                newRow = new HollowDiffViewRow(pair, rowPath, moreFromRows, moreToRows);
                traverseEffigyToCreateViewRows(newRow, pair, moreFromRows, moreToRows);
            }

            parent.addChild(newRow);
        }
    }

    private boolean moreRows(List<EffigyFieldPair> pairs, boolean from, int startIdx) {
        for(int i=startIdx+1;i<pairs.size();i++) {
            if(from) {
                if(pairs.get(i).getFrom() != null)
                    return true;
            } else {
                if(pairs.get(i).getTo() != null)
                    return true;
            }
        }
        return false;
    }

    private void traverseEffigyToCreateViewRows(HollowDiffViewRow parent, EffigyFieldPair fieldPair, boolean moreFromRows[], boolean moreToRows[]) {
        HollowEffigy from = fieldPair.getFrom() != null ? (HollowEffigy) fieldPair.getFrom().getValue() : null;
        HollowEffigy to   = fieldPair.getTo()   != null ? (HollowEffigy) fieldPair.getTo().getValue()   : null;

        traverseEffigyToCreateViewRows(parent, from, to, moreFromRows, moreToRows);
    }

    private HollowDiffViewRow createRootRow(HollowEffigy fromEffigy, HollowEffigy toEffigy) {
        HollowEffigy.Field fromField = fromEffigy == null ? null : new HollowEffigy.Field(null, fromEffigy);
        HollowEffigy.Field toField = toEffigy == null ? null : new HollowEffigy.Field(null, toEffigy);

        EffigyFieldPair fieldPair = new EffigyFieldPair(fromField, toField, -1, -1);
        boolean moreRows[] = new boolean[] { false };
        return new HollowDiffViewRow(fieldPair, new int[0], moreRows, moreRows);
    }

}
