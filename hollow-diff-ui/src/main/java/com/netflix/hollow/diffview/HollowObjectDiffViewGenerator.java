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

import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.diffview.effigy.CustomHollowEffigyFactory;
import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigy.Field;
import com.netflix.hollow.diffview.effigy.HollowEffigyFactory;
import com.netflix.hollow.diffview.effigy.HollowRecordDiffUI;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer;
import com.netflix.hollow.diffview.effigy.pairer.HollowEffigyFieldPairer.EffigyFieldPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HollowObjectDiffViewGenerator {

    private final HollowDataAccess fromDataAccess;
    private final HollowDataAccess toDataAccess;
    private final HollowRecordDiffUI diffUI;
    private final String typeName;
    private final int fromOrdinal;
    private final int toOrdinal;

    public HollowObjectDiffViewGenerator(HollowDataAccess fromDataAccess, HollowDataAccess toDataAccess, HollowRecordDiffUI diffUI, String typeName, int fromOrdinal, int toOrdinal) {
        this.fromDataAccess = fromDataAccess;
        this.toDataAccess = toDataAccess;
        this.diffUI = diffUI;
        this.typeName = typeName;
        this.fromOrdinal = fromOrdinal;
        this.toOrdinal = toOrdinal;
    }

    public HollowDiffViewRow getHollowDiffViewRows() {
        HollowEffigy fromEffigy, toEffigy;

        if(diffUI != null && diffUI.getCustomHollowEffigyFactory(typeName) != null) {
            CustomHollowEffigyFactory effigyFactory = diffUI.getCustomHollowEffigyFactory(typeName);
            synchronized (effigyFactory) {
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
        traverseEffigyToCreateViewRows(rootRow);

        return rootRow;
    }

    List<HollowDiffViewRow> traverseEffigyToCreateViewRows(HollowDiffViewRow parent) {
        if(parent.getFieldPair().isLeafNode())
            return Collections.emptyList();

        Field fromField = parent.getFieldPair().getFrom();
        Field toField = parent.getFieldPair().getTo();

        HollowEffigy from = fromField == null ? null : (HollowEffigy) fromField.getValue();
        HollowEffigy to = toField == null ? null : (HollowEffigy) toField.getValue();

        List<EffigyFieldPair> pairs = HollowEffigyFieldPairer.pair(from, to, diffUI.getMatchHints());

        List<HollowDiffViewRow> childRows = new ArrayList<HollowDiffViewRow>();

        for(int i = 0; i < pairs.size(); i++) {
            EffigyFieldPair pair = pairs.get(i);

            int indentation = parent.getRowPath().length + 1;

            int rowPath[] = Arrays.copyOf(parent.getRowPath(), indentation);
            rowPath[rowPath.length - 1] = i;

            childRows.add(new HollowDiffViewRow(pair, rowPath, parent, this));
        }

        return childRows;
    }

    private HollowDiffViewRow createRootRow(HollowEffigy fromEffigy, HollowEffigy toEffigy) {
        HollowEffigy.Field fromField = fromEffigy == null ? null : new HollowEffigy.Field(null, fromEffigy);
        HollowEffigy.Field toField = toEffigy == null ? null : new HollowEffigy.Field(null, toEffigy);

        EffigyFieldPair fieldPair = new EffigyFieldPair(fromField, toField, -1, -1);
        return new HollowDiffViewRow(fieldPair, new int[0], null, this);
    }

}
