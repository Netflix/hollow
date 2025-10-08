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
import com.netflix.hollow.ui.EscapingTool;
import com.netflix.hollow.ui.HollowUISession;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public abstract class HollowExplorerPage {
    
    protected final HollowExplorerUI ui;
    
    private final Template headerTemplate;
    private final Template footerTemplate;

    
    public HollowExplorerPage(HollowExplorerUI ui) {
        this.ui = ui;
        this.headerTemplate = ui.getVelocityEngine().getTemplate("explorer-header.vm");
        this.footerTemplate = ui.getVelocityEngine().getTemplate("explorer-footer.vm");
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, HollowUISession session) throws IOException {
        VelocityContext ctx = new VelocityContext();
        
        if (ui.getCurrentStateVersion() != Long.MIN_VALUE)
            ctx.put("stateVersion", ui.getCurrentStateVersion());

        String headerDisplayString = ui.getHeaderDisplayString();
        if (headerDisplayString != null) {
            ctx.put("headerDisplayString", headerDisplayString);
            String headerDisplayURL = (ui.getFromHeaderDisplayMap(headerDisplayString) != null)
                    ? ui.getFromHeaderDisplayMap(headerDisplayString) : "";
            ctx.put("headerStringURL", headerDisplayURL);
        }

        String headerDisplayEnv = ui.getHeaderDisplayEnv();
        if (headerDisplayEnv != null) {
            ctx.put("headerDisplayEnv", headerDisplayEnv);
            String headerDisplayEnvColor = ui.getHeaderDisplayEnvColor();
            if (headerDisplayEnvColor != null) {
                ctx.put("headerDisplayEnvColor", headerDisplayEnvColor);
            }
        }

        String headerDisplayPinnedVersion = ui.getHeaderDisplayPinnedVersion();
        if (headerDisplayPinnedVersion != null) {
            ctx.put("headerDisplayPinnedVersion", headerDisplayPinnedVersion);
        }

        ctx.put("basePath", ui.getBaseURLPath());

        ctx.put("esc", new EscapingTool());

        setUpContext(req, session, ctx);

        resp.setContentType("text/html;charset=UTF-8");
        Writer writer = resp.getWriter();
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
