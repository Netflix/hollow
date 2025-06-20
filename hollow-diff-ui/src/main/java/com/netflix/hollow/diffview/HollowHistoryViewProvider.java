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

import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import com.netflix.hollow.ui.HollowUISession;
import jakarta.servlet.http.HttpServletRequest;

public class HollowHistoryViewProvider implements HollowObjectViewProvider {

    private final HollowHistoryUI historyUI;

    public HollowHistoryViewProvider(HollowHistoryUI historyUI) {
        this.historyUI = historyUI;
    }

    @Override
    public HollowHistoryView getObjectView(HttpServletRequest req, HollowUISession session) {
        long version = Long.parseLong(req.getParameter("version"));
        String type = req.getParameter("type");
        int keyOrdinal = Integer.parseInt(req.getParameter("keyOrdinal"));

        HollowHistoryView objectView = getObjectView(session, version, type, keyOrdinal);
        return objectView;
    }

    private HollowHistoryView getObjectView(HollowUISession session, long version, String type, int keyOrdinal) {
        HollowHistoryView objectView = (HollowHistoryView) session.getAttribute("hollow-history-view");

        long currentRandomizedTag = historyUI.getHistory().getLatestState().getCurrentRandomizedTag();
        
        if(objectView != null
                && objectView.getHistoricalVersion() == version
                && objectView.getType().equals(type)
                && objectView.getKeyOrdinal() == keyOrdinal
                && objectView.getLatestStateEngineRandomizedTag() == currentRandomizedTag) {
            return objectView;
        }

        HollowHistoricalState historicalState = historyUI.getHistory().getHistoricalState(version);
        HollowHistoricalStateTypeKeyOrdinalMapping typeMapping = historicalState.getKeyOrdinalMapping().getTypeMapping(type);
        int fromOrdinal = typeMapping.findRemovedOrdinal(keyOrdinal);
        int toOrdinal = typeMapping.findAddedOrdinal(keyOrdinal);

        HollowDiffViewRow rootRow = new HollowObjectDiffViewGenerator(historicalState.getDataAccess(), historicalState.getDataAccess(), historyUI, type, fromOrdinal, toOrdinal).getHollowDiffViewRows();
        objectView = new HollowHistoryView(version, type, keyOrdinal, currentRandomizedTag, rootRow, historyUI.getExactRecordMatcher());
        objectView.resetView();
        session.setAttribute("hollow-history-view", objectView);

        return objectView;
    }


}
