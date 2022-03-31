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

import com.netflix.hollow.diffview.HollowDiffHtmlKickstarter;
import com.netflix.hollow.diffview.HollowObjectView;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.VersionTimestampConverter;
import com.netflix.hollow.history.ui.model.HistoricalObjectChangeVersion;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import com.netflix.hollow.ui.HollowUISession;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class HistoricalObjectDiffPage extends HistoryPage {

    public HistoricalObjectDiffPage(HollowHistoryUI ui) {
        super(ui, "history-object.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        long version = Long.parseLong(req.getParameter("version"));
        String type = req.getParameter("type");
        int keyOrdinal = Integer.parseInt(req.getParameter("keyOrdinal"));

        ctx.put("version", version);
        ctx.put("typeName", type);
        ctx.put("keyOrdinal", keyOrdinal);

        HollowObjectView objectView = ui.getViewProvider().getObjectView(req, session);

        HollowDiffHtmlKickstarter htmlKickstarter = new HollowDiffHtmlKickstarter(ui.getBaseURLPath());

        HollowHistory history = ui.getHistory();

        ctx.put("initialHtml", htmlKickstarter.initialHtmlRows(objectView));
        ctx.put("changeVersions", getChangeVersions(type, keyOrdinal, history));
        ctx.put("headerEntries", getHeaderEntries(history.getHistoricalState(version)));
    }

    private List<HistoricalObjectChangeVersion> getChangeVersions(String type, int keyOrdinal, HollowHistory history) {
        List<HistoricalObjectChangeVersion> changeVersions = new ArrayList<HistoricalObjectChangeVersion>();
        for(HollowHistoricalState historicalState : history.getHistoricalStates()) {
            HollowHistoricalStateTypeKeyOrdinalMapping typeMapping = historicalState.getKeyOrdinalMapping().getTypeMapping(type);
            if (typeMapping == null) continue;

            if(typeMapping.findAddedOrdinal(keyOrdinal) != -1 || typeMapping.findRemovedOrdinal(keyOrdinal) != -1) {
                changeVersions.add(new HistoricalObjectChangeVersion(historicalState.getVersion(), VersionTimestampConverter.getTimestamp(historicalState.getVersion(),  ui.getTimeZone())));
            }
        }
        return changeVersions;
    }

}
