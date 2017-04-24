/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.explorer.ui.pages;

import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.explorer.ui.model.QueryResult;
import com.netflix.hollow.explorer.ui.model.QueryResult.QueryClause;
import com.netflix.hollow.tools.query.HollowFieldMatchQuery;
import com.netflix.hollow.tools.traverse.TransitiveSetTraverser;
import com.netflix.hollow.ui.HollowUISession;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class QueryPage extends HollowExplorerPage {

    public QueryPage(HollowExplorerUI ui) {
        super(ui, "query.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        if("true".equals(req.getParameter("clear")))
            session.clearAttribute("query-result");
        
        String type = req.getParameter("type");
        String field = req.getParameter("field");
        
        if("ANY TYPE".equals(type))
            type = null;
        
        String queryValue = req.getParameter("queryValue");
        
        List<String> allTypes = new ArrayList<String>();
        for(HollowSchema schema : ui.getStateEngine().getSchemas())
            allTypes.add(schema.getName());
        Collections.sort(allTypes);
        
        if(field != null && queryValue != null) {
            HollowFieldMatchQuery query = new HollowFieldMatchQuery(ui.getStateEngine());
            Map<String, BitSet> queryMatches = type != null ? query.findMatchingRecords(type, field, queryValue) : query.findMatchingRecords(field, queryValue);
            TransitiveSetTraverser.addReferencingOutsideClosure(ui.getStateEngine(), queryMatches);
            
            QueryClause queryClause = new QueryClause(type, field, queryValue);
            
            QueryResult result = (QueryResult) session.getAttribute("query-result");
            if(result == null) {
                result = new QueryResult(queryClause, queryMatches);
                session.setAttribute("query-result", result);
            } else {
                result.getQueryClauses().add(queryClause);
                booleanAndQueryMatches(result.getQueryMatches(), queryMatches);
            }
            
            type = null;
            field = null;
            queryValue = null;
        }
        
        ctx.put("allTypes", allTypes);
        ctx.put("selectedType", type);
        ctx.put("selectedField", field);
        ctx.put("queryValue", queryValue);
        ctx.put("queryResult", session.getAttribute("query-result"));
    }
    
    private void booleanAndQueryMatches(Map<String, BitSet> existingQueryMatches, Map<String, BitSet> newQueryMatches) {
        Iterator<Map.Entry<String, BitSet>> iter = existingQueryMatches.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, BitSet> existingEntry = iter.next();
            BitSet newTypeMatches = newQueryMatches.get(existingEntry.getKey());
            if(newTypeMatches != null) {
                existingEntry.getValue().and(newTypeMatches);
            } else {
                iter.remove();
            }
        }
    }

}
