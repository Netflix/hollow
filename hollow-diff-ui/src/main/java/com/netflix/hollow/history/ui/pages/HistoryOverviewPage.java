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
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.VersionTimestampConverter;
import com.netflix.hollow.history.ui.model.HistoryOverviewRow;
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

public class HistoryOverviewPage extends HistoryPage {
    private final boolean reverse;

    public HistoryOverviewPage(HollowHistoryUI ui) {
        super(ui, "history-overview.vm");
        this.reverse = ui.getHistory().getReverse();
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        List<HistoryOverviewRow> rows = getHistoryOverview();

        ctx.put("overviewDisplayHeaders", ui.getOverviewDisplayHeaders());
        ctx.put("overviewRows", rows);
    }
    
    public void sendJson(HttpServletRequest request, HttpServletResponse response) {
    	List<HistoryOverviewRow> rows = getHistoryOverview();
    	
    	try {
			PrintWriter out = response.getWriter();
			Gson gson = new Gson();
			String json = gson.toJson(rows);
			out.println(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    private List<HistoryOverviewRow> getHistoryOverview() {
    	List<HistoryOverviewRow> rows = new ArrayList<HistoryOverviewRow>();
    	
        for(HollowHistoricalState state : ui.getHistory().getHistoricalStates()) {
            ChangeBreakdown totalBreakdown = new ChangeBreakdown(ui.getHistory().getReverse());

            Map<String, ChangeBreakdown> topLevelChangesByType = new HashMap<String, ChangeBreakdown>();

            for(Map.Entry<String, HollowHistoricalStateTypeKeyOrdinalMapping> entry : state.getKeyOrdinalMapping().getTypeMappings().entrySet()) {
                topLevelChangesByType.put(entry.getKey(), new ChangeBreakdown(entry.getValue(), ui.getHistory().getReverse()));
                totalBreakdown.addTypeBreakown(entry.getValue());
            }

            String[] overviewDisplayHeaderValues = getOverviewDisplayHeaderValues(state, ui.getOverviewDisplayHeaders());

            rows.add(new HistoryOverviewRow(VersionTimestampConverter.getTimestamp(state.getVersion(), ui.getTimeZone()), state.getVersion(), totalBreakdown, topLevelChangesByType, overviewDisplayHeaderValues));
        }
        
        return rows;
    	
    }
    

    private String[] getOverviewDisplayHeaderValues(HollowHistoricalState state, String[] overviewDisplayHeaders) {
        String values[] = new String[overviewDisplayHeaders.length];
        
        Map<String, String> nextStateHeaders = getNextStateHeaderTags(state);
        
        for(int i=0;i<overviewDisplayHeaders.length;i++) {
            values[i] = nextStateHeaders.get(overviewDisplayHeaders[i]);
        }
        
        return values;
    }
    
    private Map<String, String> getNextStateHeaderTags(HollowHistoricalState state) {
        Map<String, String> toTags = ui.getHistory().getLatestState().getHeaderTags();
        if(state.getNextState() != null) {
            toTags = state.getNextState().getHeaderEntries();
        }
        return toTags;
    }

    public static class ChangeBreakdown {
        private int modifiedRecords;
        private int addedRecords;
        private int removedRecords;
        private final boolean reverse;

        public ChangeBreakdown() {
            this(false);
        }

        public ChangeBreakdown(boolean reverse) {
            this.reverse = reverse;
        }

        public ChangeBreakdown(HollowHistoricalStateTypeKeyOrdinalMapping keyMapping) {
            this(keyMapping, false);
        }

        public ChangeBreakdown(HollowHistoricalStateTypeKeyOrdinalMapping keyMapping, boolean reverse) {
            this.reverse = reverse;
            addTypeBreakown(keyMapping);
        }

        private void addTypeBreakown(HollowHistoricalStateTypeKeyOrdinalMapping keyMapping) {
            modifiedRecords += keyMapping.getNumberOfModifiedRecords();
            addedRecords += keyMapping.getNumberOfNewRecords();
            removedRecords += keyMapping.getNumberOfRemovedRecords();
        }

        public int getModifiedRecords() {
            return modifiedRecords;
        }

        public int getAddedRecords() {
            return reverse ? removedRecords : addedRecords;
        }

        public int getRemovedRecords() {
            return reverse ? addedRecords : removedRecords;
        }

        public int getTotal() {
            return modifiedRecords + addedRecords + removedRecords;
        }
    }

}
