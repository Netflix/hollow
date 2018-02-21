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
import com.netflix.hollow.ui.HollowUISession;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import javax.servlet.http.HttpServletRequest;
import java.io.Writer;

public abstract class HollowExplorerPage {
    
    protected final HollowExplorerUI ui;
    
    private final Template headerTemplate;
    private final Template footerTemplate;

    
    public HollowExplorerPage(HollowExplorerUI ui) {
        this.ui = ui;
        this.headerTemplate = ui.getVelocityEngine().getTemplate("explorer-header.vm");
        this.footerTemplate = ui.getVelocityEngine().getTemplate("explorer-footer.vm");
    }

    public void render(HttpServletRequest req, HollowUISession session, Writer writer) {
        VelocityContext ctx = new VelocityContext();

        if(ui.getHeaderDisplayString() != null)
            ctx.put("headerDisplayString", ui.getHeaderDisplayString());
        
        if(ui.getCurrentStateVersion() != Long.MIN_VALUE)
            ctx.put("stateVersion", ui.getCurrentStateVersion());
        
        ctx.put("basePath", ui.getBaseURLPath());

        setUpContext(req, session, ctx);

        headerTemplate.merge(ctx, writer);
        renderPage(req, ctx, writer);
        footerTemplate.merge(ctx, writer);
    }

    /**
     * Renders the page to the provided Writer, using the provided VelocityContext.
     */
    protected abstract void renderPage(HttpServletRequest req, VelocityContext ctx, Writer writer);

    /**
     * Populates the provided VelocityContext.
     */
    protected abstract void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx);

}
