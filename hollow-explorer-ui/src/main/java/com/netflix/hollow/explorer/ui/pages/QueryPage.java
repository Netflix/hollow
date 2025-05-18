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
package com.netflix.hollow.explorer.ui.pages;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.explorer.ui.model.QueryResult;
import com.netflix.hollow.explorer.ui.model.QueryResult.QueryClause;
import com.netflix.hollow.ui.HollowUISession;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class QueryPage extends HollowExplorerPage {

    public QueryPage(HollowExplorerUI ui) {
        super(ui);
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
        String removeField = req.getParameter("removeField");
        
        List<String> allTypes = new ArrayList<String>();
        for(HollowSchema schema : ui.getStateEngine().getSchemas())
            allTypes.add(schema.getName());
        Collections.sort(allTypes);
        
        QueryResult result = (QueryResult) session.getAttribute("query-result");
        if(result != null) {
            result.recalculateIfNotCurrent(ui.getStateEngine());

            if(removeField != null) {
                result.removeQueryClause(removeField);
            }
        }

        if(field != null && queryValue != null) {
            HollowReadStateEngine stateEngine = ui.getStateEngine();

            QueryClause queryClause = new QueryClause(type, field, queryValue);
            
            if(result == null) {
                result = new QueryResult(stateEngine.getCurrentRandomizedTag());
                session.setAttribute("query-result", result);
            }
            
            result.augmentQuery(queryClause, ui.getStateEngine());
            
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

    @Override
    protected void renderPage(HttpServletRequest req, VelocityContext ctx, Writer writer) {
        ui.getVelocityEngine().getTemplate("query.vm").merge(ctx, writer);
    }
}
