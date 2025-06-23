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

import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.explorer.ui.model.SchemaDisplay;
import com.netflix.hollow.explorer.ui.model.SchemaDisplayField;
import com.netflix.hollow.ui.HollowUISession;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class BrowseSchemaPage extends HollowExplorerPage {

    public BrowseSchemaPage(HollowExplorerUI ui) {
        super(ui);
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        String[] types = req.getParameterValues("type");
        String expand = req.getParameter("expand");
        String collapse = req.getParameter("collapse");

        List<SchemaDisplay> schemaDisplays = new ArrayList<SchemaDisplay>();
        for (String type : types) {
            SchemaDisplay schemaDisplay = (SchemaDisplay) session.getAttribute("schema-display-" + type);

            if(schemaDisplay == null) {
                schemaDisplay = new SchemaDisplay(ui.getStateEngine().getSchema(type));
                schemaDisplay.setExpanded(true);
            }

            if(expand != null) expandOrCollapse(schemaDisplay, expand.split("\\."), 1, true);
            if(collapse != null) expandOrCollapse(schemaDisplay, collapse.split("\\."), 1, false);

            session.setAttribute("schema-display-" + type, schemaDisplay);

            schemaDisplays.add(schemaDisplay);
        }

        ctx.put("schemaDisplays", schemaDisplays);
        ctx.put("type", types);
    }

    @Override
    protected void renderPage(HttpServletRequest req, VelocityContext ctx, Writer writer) {
        ui.getVelocityEngine().getTemplate("browse-schema.vm").merge(ctx, writer);
    }
    
    private void expandOrCollapse(SchemaDisplay display, String fieldPaths[], int cursor, boolean isExpand) {
        if(display == null)
            return;
        
        if(cursor >= fieldPaths.length) {
            display.setExpanded(isExpand);
            return;
        } 
        
        for(SchemaDisplayField field : display.getFields()) {
            if(field.getFieldName().equals(fieldPaths[cursor]))
                expandOrCollapse(field.getReferencedType(), fieldPaths, cursor+1, isExpand);
        }
    }
}
