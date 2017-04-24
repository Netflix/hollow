package com.netflix.hollow.explorer.ui.pages;

import com.netflix.hollow.explorer.ui.model.SchemaDisplayField;

import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.explorer.ui.model.SchemaDisplay;
import com.netflix.hollow.ui.HollowUISession;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class BrowseSchemaPage extends HollowExplorerPage {

    public BrowseSchemaPage(HollowExplorerUI ui) {
        super(ui, "browse-schema.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        String type = req.getParameter("type");
        String expand = req.getParameter("expand");
        String collapse = req.getParameter("collapse");
        
        SchemaDisplay schemaDisplay = (SchemaDisplay) session.getAttribute("schema-display-" + type);

        if(schemaDisplay == null) {
            schemaDisplay = new SchemaDisplay(ui.getStateEngine().getSchema(type));
            schemaDisplay.setExpanded(true);
        }
        
        if(expand != null) expandOrCollapse(schemaDisplay, expand.split("\\."), 1, true);
        if(collapse != null) expandOrCollapse(schemaDisplay, collapse.split("\\."), 1, false);
        
        session.setAttribute("schema-display-" + type, schemaDisplay);
        
        ctx.put("schemaDisplay", schemaDisplay);
        ctx.put("type", type);
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
