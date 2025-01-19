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
import com.netflix.hollow.history.ui.model.HistoryStateTypeChanges;
import com.netflix.hollow.history.ui.model.RecordDiff;
import com.netflix.hollow.history.ui.model.RecordDiffTreeNode;
import com.netflix.hollow.history.ui.naming.HollowHistoryRecordNamer;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.ui.HollowUISession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.velocity.VelocityContext;

public class HistoryStateTypePage extends HistoryPage {

    private static final String STATE_TYPE_CHANGES_SESSION_ATTRIBUTE_NAME = "HISTORY_STATE_TYPE_CHANGES";

    public HistoryStateTypePage(HollowHistoryUI ui) {
        super(ui, "history-state-type.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        long version = Long.parseLong(req.getParameter("version"));
        HistoryStateTypeChanges typeChange = getStateTypeChanges(req, session, ui);
        HollowHistoricalState historicalState = ui.getHistory().getHistoricalState(version);
        
        List<String> groupByOptions = new ArrayList<String>(Arrays.asList(historicalState.getKeyOrdinalMapping().getTypeMapping(req.getParameter("type")).getKeyIndex().getKeyFields()));
        groupByOptions.removeAll(Arrays.asList(typeChange.getGroupedFieldNames()));
        
        ctx.put("typeChange", typeChange);
        ctx.put("headerEntries", getHeaderEntries(historicalState));
        ctx.put("groupBy", req.getParameter("groupBy") == null ? "" : req.getParameter("groupBy"));
        ctx.put("groupByOptions", groupByOptions);
    }
    
    public void sendJson(HttpServletRequest request, HollowUISession session, HttpServletResponse response) {
    	long version = Long.parseLong(request.getParameter("version"));
    	HistoryStateTypeChanges typeChange = getStateTypeChanges(request, session, ui);
    	HollowHistoricalState historicalState = ui.getHistory().getHistoricalState(version);
    	
    	List<String> groupByOptions = new ArrayList<String>(Arrays.asList(historicalState.getKeyOrdinalMapping().getTypeMapping(request.getParameter("type")).getKeyIndex().getKeyFields()));
    	groupByOptions.removeAll(Arrays.asList(typeChange.getGroupedFieldNames()));
    	
    	Map<String, List<List<String>>> changes = new LinkedHashMap<String, List<List<String>>>();
    	
    	List<List<String>> groups = new ArrayList<List<String>>();
    	groups.add(groupByOptions);
    	changes.put("groups", groups);
    	
    	// handle additions
    	if(typeChange.getAddedRecords().isEmpty()) {
    		changes.put("additions", new ArrayList<List<String>>());
    	} else if(!typeChange.getAddedRecords().hasSubGroups()){
    		List<RecordDiff> addedDiffs = typeChange.getAddedRecords().getRecordDiffs();
    		List<List<String>> idRecords = new ArrayList<List<String>>();
    		for(RecordDiff diff : addedDiffs) {
    			List<String> data = new ArrayList<String>();
    			data.add(diff.getIdentifierString());
    			data.add(new Integer(diff.getKeyOrdinal()).toString());
    			idRecords.add(data);
    		}
    		changes.put("additions", idRecords);
    	} else {
    		// This has sub groups
    		List<List<String>> idRecords = new ArrayList<List<String>>();
    		for(RecordDiffTreeNode changeGroup : typeChange.getAddedRecords().getSubGroups()) {
    			List<String> data = new ArrayList<String>();
    			data.add(changeGroup.getGroupName() + "(" + changeGroup.getDiffCount() +  ")");
    			data.add(changeGroup.getHierarchicalFieldName());
    			idRecords.add(data);
    		}
    		changes.put("additions", idRecords);
    	}

    	// handle modifications
    	if(typeChange.getModifiedRecords().isEmpty()) {
    		changes.put("modifications", new ArrayList<List<String>>());
    	} else if(!typeChange.getModifiedRecords().hasSubGroups()){
    		List<RecordDiff> modifiedDiffs = typeChange.getModifiedRecords().getRecordDiffs();
    		List<List<String>> idRecords = new ArrayList<List<String>>();
    		for(RecordDiff diff : modifiedDiffs) {
    			List<String> data = new ArrayList<String>();
    			data.add(diff.getIdentifierString());
    			data.add(new Integer(diff.getKeyOrdinal()).toString());
    			idRecords.add(data);
    		}
    		changes.put("modifications", idRecords);
    	} else {
    		// This has sub groups
    		List<List<String>> idRecords = new ArrayList<List<String>>();
    		for(RecordDiffTreeNode changeGroup : typeChange.getModifiedRecords().getSubGroups()) {
    			List<String> data = new ArrayList<String>();
    			data.add(changeGroup.getGroupName() + "(" + changeGroup.getDiffCount() +  ")");
    			data.add(changeGroup.getHierarchicalFieldName());
    			idRecords.add(data);
    		}
    		changes.put("modifications", idRecords);    		
    	}
    	
    	// handle removals
    	if(typeChange.getRemovedRecords().isEmpty()) {
    		changes.put("removals", new ArrayList<List<String>>());
    	} else if(!typeChange.getRemovedRecords().hasSubGroups()){
    		List<RecordDiff> removedDiffs = typeChange.getRemovedRecords().getRecordDiffs();
    		List<List<String>> idRecords = new ArrayList<List<String>>();
    		for(RecordDiff diff : removedDiffs) {
    			List<String> data = new ArrayList<String>();
    			data.add(diff.getIdentifierString());
    			data.add(new Integer(diff.getKeyOrdinal()).toString());
    			idRecords.add(data);
    		}
    		changes.put("removals", idRecords);
    	} else {
    		// This has sub groups
    		List<List<String>> idRecords = new ArrayList<List<String>>();
    		for(RecordDiffTreeNode changeGroup : typeChange.getRemovedRecords().getSubGroups()) {
    			List<String> data = new ArrayList<String>();
    			data.add(changeGroup.getGroupName() + "(" + changeGroup.getDiffCount() +  ")");
    			data.add(changeGroup.getHierarchicalFieldName());
    			idRecords.add(data);
    		}
    		changes.put("removals", idRecords);    		
    	}
    	
    	
    	try {
        	PrintWriter out = response.getWriter();
        	Gson gson = new Gson();
        	String json = gson.toJson(changes, changes.getClass());
        	out.println(json);
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	
    }
    
    public static HistoryStateTypeChanges getStateTypeChanges(HttpServletRequest req, HollowUISession session, HollowHistoryUI ui) {
        HistoryStateTypeChanges typeChanges = (HistoryStateTypeChanges) session.getAttribute(STATE_TYPE_CHANGES_SESSION_ATTRIBUTE_NAME);
        long version = Long.parseLong(req.getParameter("version"));
        String type = req.getParameter("type");
        String groupBy = req.getParameter("groupBy");
        String[] groupedFieldNames = getGroupedFieldNames(groupBy);
        
        if(typeChanges == null 
                || version != typeChanges.getStateVersion()
                || !type.equals(typeChanges.getTypeName())
                || !Arrays.equals(groupedFieldNames, typeChanges.getGroupedFieldNames())) {
            
            HollowHistoricalState historicalState = ui.getHistory().getHistoricalState(Long.parseLong(req.getParameter("version")));
            HollowHistoryRecordNamer recordNamer = ui.getHistoryRecordNamer(type);
            typeChanges = new HistoryStateTypeChanges(historicalState, type, recordNamer, groupedFieldNames);
            session.setAttribute(STATE_TYPE_CHANGES_SESSION_ATTRIBUTE_NAME, typeChanges);
        }
        
       return typeChanges;
    }
    
    private static String[] getGroupedFieldNames(String groupBy) {
        if(groupBy == null)
            return new String[0];
        
        return groupBy.split(",");
    }

}
