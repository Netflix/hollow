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

import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.diffview.effigy.CustomHollowEffigyFactory;
import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigyFactory;
import com.netflix.hollow.diffview.effigy.HollowRecordDiffUI;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer.EffigyFieldPair;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import java.util.ArrayList;
import java.util.List;

public class HollowObjectDiffViewGenerator {

    private final long MAX_TIME_BEFORE_PAIRING_TIMEOUT = 15000L;

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

    public List<HollowDiffViewRow> getHollowDiffViewRows() {
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

        List<HollowDiffViewRow> rows = new ArrayList<HollowDiffViewRow>();
        traverseEffigyToCreateViewRows(rows, fromEffigy, toEffigy, 0, 1, new boolean[64], new boolean[64]);
        insertRootRow(rows, fromEffigy, toEffigy);

        return rows;
    }

    private void traverseEffigyToCreateViewRows(List<HollowDiffViewRow> viewRows, HollowEffigy from, HollowEffigy to, int rowId, int indentation, boolean moreFromRows[], boolean moreToRows[]) {
        List<EffigyFieldPair> pairs = HollowEffigyFieldPairer.pair(from, to, diffUI.getMatchHints(), deadlineBeforePairingTimeout);

        List<HollowDiffViewRow> directChildren = new ArrayList<HollowDiffViewRow>();

        for(int i=0;i<pairs.size();i++) {
            EffigyFieldPair pair = pairs.get(i);

            moreFromRows[indentation] = moreRows(pairs, true, i);
            moreToRows[indentation] = moreRows(pairs, false, i);

            HollowDiffViewRow newRow;

            if(pair.isLeafNode()) {
                newRow = new HollowDiffViewRow(pair, ++rowId, indentation, 0, moreFromRows, moreToRows);
                viewRows.add(newRow);
            } else {
                List<HollowDiffViewRow> descendents = new ArrayList<HollowDiffViewRow>();
                traverseEffigyToCreateViewRows(descendents, pair, ++rowId, indentation + 1, moreFromRows, moreToRows);
                newRow = new HollowDiffViewRow(pair, rowId, indentation, descendents.size(), moreFromRows, moreToRows);
                rowId += descendents.size();
                viewRows.add(newRow);
                viewRows.addAll(descendents);
            }

            directChildren.add(newRow);
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

    private void traverseEffigyToCreateViewRows(List<HollowDiffViewRow> rows, EffigyFieldPair fieldPair, int rowId, int indentation, boolean moreFromRows[], boolean moreToRows[]) {
        HollowEffigy from = fieldPair.getFrom() != null ? (HollowEffigy) fieldPair.getFrom().getValue() : null;
        HollowEffigy to   = fieldPair.getTo()   != null ? (HollowEffigy) fieldPair.getTo().getValue()   : null;

        traverseEffigyToCreateViewRows(rows, from, to, rowId, indentation, moreFromRows, moreToRows);
    }

    private void insertRootRow(List<HollowDiffViewRow> rows, HollowEffigy fromEffigy, HollowEffigy toEffigy) {
        HollowDiffNodeIdentifier nodeId = new HollowDiffNodeIdentifier(fromEffigy == null ? toEffigy.getObjectType() : fromEffigy.getObjectType());
        HollowEffigy.Field fromField = fromEffigy == null ? null : new HollowEffigy.Field(nodeId, fromEffigy);
        HollowEffigy.Field toField = toEffigy == null ? null : new HollowEffigy.Field(nodeId, toEffigy);

        EffigyFieldPair fieldPair = new EffigyFieldPair(fromField, toField, -1, -1);
        boolean moreRows[] = new boolean[] { false };
        rows.add(0, new HollowDiffViewRow(fieldPair, 0, 0, rows.size(), moreRows, moreRows));
    }

}
