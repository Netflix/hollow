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
package com.netflix.hollow.history.ui.pages;

import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.VersionTimestampConverter;
import com.netflix.hollow.history.ui.model.HistoryStateQueryMatches;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryTypeKeyIndex;
import com.netflix.hollow.ui.HollowUISession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class HistoryQueryPage extends HistoryPage {

    public HistoryQueryPage(HollowHistoryUI ui) {
        super(ui, "history-query.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        HollowHistory history = ui.getHistory();

        String query = req.getParameter("query");

        Map<String, IntList> typeQueryKeyMatches = typeQueryKeyMatches(history, query);

        List<HistoryStateQueryMatches> list = new ArrayList<HistoryStateQueryMatches>();

        for(HollowHistoricalState state : history.getHistoricalStates()) {
            HistoryStateQueryMatches matches = new HistoryStateQueryMatches(state, ui, VersionTimestampConverter.getTimestamp(state.getVersion(), ui.getTimeZone()), typeQueryKeyMatches);
            if(matches.hasMatches())
                list.add(matches);
        }

        ctx.put("stateQueryMatchesList", list);
        ctx.put("query", query);
    }

    private Map<String, IntList> typeQueryKeyMatches(HollowHistory history, String query) {
        Map<String, IntList> typeQueryKeyMatches = new HashMap<String, IntList>();

        for(Map.Entry<String, HollowHistoryTypeKeyIndex> entry : history.getKeyIndex().getTypeKeyIndexes().entrySet()) {
            IntList typeQueryResult = entry.getValue().queryIndexedFields(query);
            if(typeQueryResult.size() != 0)
                typeQueryKeyMatches.put(entry.getKey(), typeQueryResult);
        }
        return typeQueryKeyMatches;
    }

}
