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

import com.netflix.hollow.diff.ui.HollowDiffUI;
import com.netflix.hollow.diff.ui.model.HollowDiffUIBreadcrumbs;
import com.netflix.hollow.diffview.HollowDiffHtmlKickstarter;
import com.netflix.hollow.diffview.HollowObjectView;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.ui.HollowUISession;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class DiffObjectPage extends DiffPage {

    public DiffObjectPage(HollowDiffUI diffUI) {
        super(diffUI, "diff-object.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        String type = req.getParameter("type");
        int fromOrdinal = Integer.parseInt(req.getParameter("fromOrdinal"));
        int toOrdinal = Integer.parseInt(req.getParameter("toOrdinal"));

        int fieldIdx = -1;
        if(req.getParameter("fieldIdx") != null)
            fieldIdx = Integer.parseInt(req.getParameter("fieldIdx"));

        ctx.put("typeName", type);
        ctx.put("fromOrdinal", fromOrdinal);
        ctx.put("toOrdinal", toOrdinal);

        HollowObjectView diffView = diffUI.getHollowObjectViewProvider().getObjectView(req, session);

        HollowDiffHtmlKickstarter htmlKickstarter = new HollowDiffHtmlKickstarter(diffUI.getBaseURLPath());

        ctx.put("initialHtml", htmlKickstarter.initialHtmlRows(diffView));

        ctx.put("breadcrumbs", getBreadcrumbs(type, fieldIdx, fromOrdinal, toOrdinal));
    }


    private List<HollowDiffUIBreadcrumbs> getBreadcrumbs(String type, int fieldIdx, int fromOrdinal, int toOrdinal) {
        HollowTypeDiff typeDiff = getTypeDiff(type);

        List<HollowDiffUIBreadcrumbs> breadcrumbs = new ArrayList<HollowDiffUIBreadcrumbs>();

        breadcrumbs.add(new HollowDiffUIBreadcrumbs((diffUI.getDiffUIPath() == null || diffUI.getDiffUIPath().length() == 0) ?
                "/" : diffUI.getDiffUIPath(), "Overview"));
        breadcrumbs.add(new HollowDiffUIBreadcrumbs((diffUI.getDiffUIPath() == null || diffUI.getDiffUIPath().length() == 0) ?
                "typediff?type=" + type : diffUI.getDiffUIPath() + "/typediff?type=" + type, type));
        if(fieldIdx != -1) {
            breadcrumbs.add(new HollowDiffUIBreadcrumbs((diffUI.getDiffUIPath() == null || diffUI.getDiffUIPath().length() == 0) ?
                    "fielddiff?type=" + type + "&fieldIdx=" + fieldIdx : diffUI.getDiffUIPath() + "/fielddiff?type=" + type + "&fieldIdx=" + fieldIdx, typeDiff.getFieldDiffs().get(fieldIdx).getFieldIdentifier().toString()));
        }

        String displayKey =
                fromOrdinal != -1 ?
                    typeDiff.getMatcher().getKeyDisplayString(typeDiff.getFromTypeState(), fromOrdinal)
                    : typeDiff.getMatcher().getKeyDisplayString(typeDiff.getToTypeState(), toOrdinal);
        breadcrumbs.add(new HollowDiffUIBreadcrumbs(null, displayKey));

        return breadcrumbs;
    }
}
