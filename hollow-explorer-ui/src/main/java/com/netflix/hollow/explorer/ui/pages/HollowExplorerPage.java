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

import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public abstract class HollowExplorerPage {
    
    protected final HollowExplorerUI ui;
    
    protected final Template template;
    protected final Template headerTemplate;
    protected final Template footerTemplate;

    
    public HollowExplorerPage(HollowExplorerUI ui, String templateName) {
        this.ui = ui;
        this.template = ui.getVelocityEngine().getTemplate(templateName);
        this.headerTemplate = ui.getVelocityEngine().getTemplate("explorer-header.vm");
        this.footerTemplate = ui.getVelocityEngine().getTemplate("explorer-footer.vm");
    }

    public void render(HttpServletRequest req, Writer writer) {
        VelocityContext ctx = new VelocityContext();

        if(ui.getHeaderDisplayString() != null)
            ctx.put("headerDisplayString", ui.getHeaderDisplayString());
        
        if(ui.getCurrentStateVersion() != Long.MIN_VALUE)
            ctx.put("stateVersion", ui.getCurrentStateVersion());
        
        ctx.put("basePath", ui.getBaseURLPath());

        setUpContext(req, ctx);

        headerTemplate.merge(ctx, writer);
        template.merge(ctx, writer);
        footerTemplate.merge(ctx, writer);
    }

    protected abstract void setUpContext(HttpServletRequest req, VelocityContext ctx);

}
