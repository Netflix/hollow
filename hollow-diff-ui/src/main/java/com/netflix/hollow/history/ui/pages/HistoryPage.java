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

import com.netflix.hollow.diff.ui.model.HollowHeaderEntry;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.ui.HollowUISession;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public abstract class HistoryPage {

    protected final HollowHistoryUI ui;
    protected final Template template;
    protected final Template headerTemplate;
    protected final Template footerTemplate;

    public HistoryPage(HollowHistoryUI ui, String templateName) {
        this.ui = ui;
        this.template = ui.getVelocityEngine().getTemplate(templateName);
        this.headerTemplate = ui.getVelocityEngine().getTemplate("history-header.vm");
        this.footerTemplate = ui.getVelocityEngine().getTemplate("history-footer.vm");
    }

    public void render(HttpServletRequest req, HollowUISession session, Writer writer) {
        VelocityContext ctx = new VelocityContext();

        ctx.put("showHomeLink", !(this instanceof HistoryOverviewPage));
        ctx.put("basePath", ui.getBaseURLPath());

        try {
            setUpContext(req, session, ctx);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        if(includeHeaderAndFooter())
            headerTemplate.merge(ctx, writer);
        template.merge(ctx, writer);
        if(includeHeaderAndFooter())
            footerTemplate.merge(ctx, writer);
    }

    protected abstract void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx);

    protected List<HollowHeaderEntry> getHeaderEntries(HollowHistoricalState state) {
        Map<String, String> fromTags = state.getHeaderEntries();
        Map<String, String> toTags = ui.getHistory().getLatestState().getHeaderTags();
        if(state.getNextState() != null) {
            toTags = state.getNextState().getHeaderEntries();
        }

        Set<String> allKeys = new HashSet<String>();
        allKeys.addAll(fromTags.keySet());
        allKeys.addAll(toTags.keySet());

        List<HollowHeaderEntry> entries = new ArrayList<HollowHeaderEntry>();

        int i = 0;

        for(String key : allKeys) {
            entries.add(new HollowHeaderEntry(i++, key, fromTags.get(key), toTags.get(key)));
        }

        return entries;
    }

    protected boolean includeHeaderAndFooter() {
        return true;
    }
}
