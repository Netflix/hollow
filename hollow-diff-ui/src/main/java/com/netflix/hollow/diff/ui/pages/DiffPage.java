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
package com.netflix.hollow.diff.ui.pages;

import static com.netflix.hollow.ui.HollowDiffUtil.formatBytes;

import com.netflix.hollow.diff.ui.HollowDiffUI;
import com.netflix.hollow.diff.ui.model.HollowHeaderEntry;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.ui.HollowUISession;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public abstract class DiffPage {

    protected final HollowDiffUI diffUI;
    protected final Template template;
    protected final Template headerTemplate;
    protected final Template footerTemplate;

    protected String env = "";
    protected boolean isHeaderEnabled = false;

    public DiffPage(HollowDiffUI diffUI, String templateName) {
        this.diffUI = diffUI;
        this.template = diffUI.getVelocity().getTemplate(templateName);
        this.headerTemplate = diffUI.getVelocity().getTemplate("diff-header.vm");
        this.footerTemplate = diffUI.getVelocity().getTemplate("diff-footer.vm");
    }

    public void render(HttpServletRequest req, HollowUISession session, Writer writer) {
        processCookies(req);

        VelocityContext ctx = new VelocityContext();

        ctx.put("request", req);
        ctx.put("env", env);
        ctx.put("isHeaderEnabled", isHeaderEnabled);

        ctx.put("basePath", diffUI.getBaseURLPath());
        ctx.put("path", diffUI.getDiffUIPath());
        ctx.put("fromBlobName", diffUI.getFromBlobName());
        ctx.put("toBlobName", diffUI.getToBlobName());

        HollowDiff diff = diffUI.getDiff();
        long heapFrom = diff.getFromStateEngine().calcApproxDataSize();
        long heapTo = diff.getToStateEngine().calcApproxDataSize();
        long heapDiff = heapTo-heapFrom;
        ctx.put("fromHeap", formatBytes(heapFrom));
        ctx.put("toHeap", formatBytes(heapTo));
        ctx.put("diffHeap", (heapDiff > 0 ? "+" : "") + formatBytes(heapDiff));
        ctx.put("diffHeap_cssClass", heapDiff<=0 ? "heap_dec" : "heap_inc");

        setUpContext(req, session, ctx);

        ctx.put("headerEntries", getHeaderEntries());

        headerTemplate.merge(ctx, writer);
        template.merge(ctx, writer);
        footerTemplate.merge(ctx, writer);

    }

    private void processCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                if ("env".equals(name)) {
                    env = value;
                } else if ("isHeaderEnabled".equals(name)) {
                    isHeaderEnabled = Boolean.valueOf(value);
                }
            }
        }
    }

    protected abstract void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx);

    protected HollowDiff getDiff() {
        return diffUI.getDiff();
    }

    protected int intParam(HttpServletRequest req, HollowUISession session, String ctx, String paramName, int defaultValue) {
        return Integer.parseInt(param(req, session, ctx, paramName, String.valueOf(defaultValue)));
    }

    protected boolean boolParam(HttpServletRequest req, HollowUISession session, String ctx, String paramName, boolean defaultValue) {
        return Boolean.parseBoolean(param(req, session, ctx, paramName, String.valueOf(defaultValue)));
    }

    protected String param(HttpServletRequest req, HollowUISession session, String ctx, String paramName, String defaultValue) {
        String sessionParamName = ctx + "_" + paramName;
        String reqParam = req.getParameter(paramName);

        if(reqParam != null) {
            session.setAttribute(sessionParamName, reqParam);
            return reqParam;
        }

        String sessionParam = (String) session.getAttribute(sessionParamName);
        if(sessionParam != null)
            return sessionParam;

        return defaultValue;
    }

    protected HollowTypeDiff getTypeDiff(String typeName) {
        for(HollowTypeDiff typeDiff : getDiff().getTypeDiffs()) {
            if(typeDiff.getTypeName().equals(typeName))
                return typeDiff;
        }
        return null;
    }

    private List<HollowHeaderEntry> getHeaderEntries() {
        Map<String, String> fromTags = diffUI.getDiff().getFromStateEngine().getHeaderTags();
        Map<String, String> toTags = diffUI.getDiff().getToStateEngine().getHeaderTags();

        Set<String> allKeys = new HashSet<String>();
        allKeys.addAll(fromTags.keySet());
        allKeys.addAll(toTags.keySet());

        List<HollowHeaderEntry> entries = new ArrayList<HollowHeaderEntry>();

        int i=0;

        for(String key : allKeys) {
            entries.add(new HollowHeaderEntry(i++, key, fromTags.get(key), toTags.get(key)));
        }

        return entries;
    }

}
