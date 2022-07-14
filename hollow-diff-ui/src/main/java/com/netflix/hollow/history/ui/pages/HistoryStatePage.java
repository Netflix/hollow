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

import com.google.gson.Gson;
import com.netflix.hollow.diff.ui.model.HollowHeaderEntry;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.model.HistoryStateTypeChangeSummary;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import com.netflix.hollow.ui.HollowUISession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.VelocityContext;

public class HistoryStatePage extends HistoryPage {

    public HistoryStatePage(HollowHistoryUI ui) {
        // Use the following line for the NEW UI
        //super(ui, "history-state-enhanced-ui.vm");
        
        // Use the following line for the CLASSIC UI
        super(ui, "history-state.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        HollowHistoricalState historicalState = ui.getHistory().getHistoricalState(Long.parseLong(req.getParameter("version")));

        long nextStateVersion = getNextStateVersion(historicalState);
        long prevStateVersion = getPreviousStateVersion(historicalState);

        List<HistoryStateTypeChangeSummary> typeChanges = new ArrayList<HistoryStateTypeChangeSummary>();

        for(Map.Entry<String, HollowHistoricalStateTypeKeyOrdinalMapping>entry : historicalState.getKeyOrdinalMapping().getTypeMappings().entrySet()) {
            HistoryStateTypeChangeSummary typeChange = new HistoryStateTypeChangeSummary(historicalState.getVersion(), entry.getKey(), entry.getValue());
            if(!typeChange.isEmpty())
                typeChanges.add(typeChange);
        }

        ctx.put("typeChanges", typeChanges);
        ctx.put("headerEntries", getHeaderEntries(historicalState));
        ctx.put("currentStateVersion", historicalState.getVersion());
        ctx.put("nextStateVersion", nextStateVersion);
        ctx.put("prevStateVersion", prevStateVersion);
    }

    public void sendJson(HttpServletRequest req, HttpServletResponse resp) {
        HollowHistoricalState historicalState = ui.getHistory().getHistoricalState(Long.parseLong(req.getParameter("version")));

        List<HistoryStateTypeChangeSummary> typeChanges = new ArrayList<HistoryStateTypeChangeSummary>();

        for(Map.Entry<String, HollowHistoricalStateTypeKeyOrdinalMapping> entry : historicalState.getKeyOrdinalMapping().getTypeMappings().entrySet()) {
            HistoryStateTypeChangeSummary typeChange = new HistoryStateTypeChangeSummary(historicalState.getVersion(), entry.getKey(), entry.getValue());
            if(!typeChange.isEmpty())
                typeChanges.add(typeChange);
        }

        List<HollowHeaderEntry> headerEntries = getHeaderEntries(historicalState);

        Map<String, String> params = new HashMap<String, String>();
        for(HollowHeaderEntry headerEntry : headerEntries) {
            String key = headerEntry.getKey();
            if(key.equals("VIP")) {
                params.put("fromVip", headerEntry.getFromValue());
                params.put("toVip", headerEntry.getToValue());
            }
            if(key.equals("dataVersion")) {
                params.put("fromVersion", headerEntry.getFromValue());
                params.put("toVersion", headerEntry.getToValue());
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("params", params);
        data.put("objectTypes", typeChanges);

        //resp.setContentType("application/json");
        try {
            PrintWriter out = resp.getWriter();
            Gson gson = new Gson();
            String json = gson.toJson(data);
            out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private long getNextStateVersion(HollowHistoricalState currentHistoricalState) {
        if(currentHistoricalState.getNextState() != null)
            return currentHistoricalState.getNextState().getVersion();
        return -1;
    }

    private long getPreviousStateVersion(HollowHistoricalState currentHistoricalState) {
        for(HollowHistoricalState state : ui.getHistory().getHistoricalStates()) {
            if(state.getNextState() == currentHistoricalState) {
                return state.getVersion();
            }
        }
        return -1;
    }

    @Override
    protected boolean includeHeaderAndFooter() {
        return false;
    }


}
