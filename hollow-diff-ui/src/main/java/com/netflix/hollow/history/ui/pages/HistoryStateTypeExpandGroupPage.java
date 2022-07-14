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

import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.model.HistoryStateTypeChanges;
import com.netflix.hollow.ui.HollowUISession;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class HistoryStateTypeExpandGroupPage extends HistoryPage {

    public HistoryStateTypeExpandGroupPage(HollowHistoryUI ui) {
        super(ui, "history-state-type-expand-group.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        HistoryStateTypeChanges typeChange = HistoryStateTypePage.getStateTypeChanges(req, session, ui);
        String expandGroupId = req.getParameter("expandGroupId");

        ctx.put("expandedNode", typeChange.findTreeNode(expandGroupId));
        ctx.put("version", req.getParameter("version"));
        ctx.put("type", req.getParameter("type"));
    }

    @Override
    protected boolean includeHeaderAndFooter() {
        return false;
    }
}
