/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.diffview;

import com.netflix.hollow.diff.ui.HollowDiffSession;
import com.netflix.hollow.diff.ui.HollowDiffUI;
import javax.servlet.http.HttpServletRequest;

public class HollowDiffViewProvider implements HollowObjectViewProvider {

    private final HollowDiffUI diffUI;

    public HollowDiffViewProvider(HollowDiffUI diffUI) {
        this.diffUI = diffUI;
    }

    @Override
    public HollowDiffView getObjectView(HttpServletRequest req, HollowDiffSession session) {
        String type = req.getParameter("type");
        int fromOrdinal = Integer.parseInt(req.getParameter("fromOrdinal"));
        int toOrdinal = Integer.parseInt(req.getParameter("toOrdinal"));

        HollowDiffView objectView = getObjectView(session, type, fromOrdinal, toOrdinal);
        return objectView;
    }

    private HollowDiffView getObjectView(HollowDiffSession session, String type, int fromOrdinal, int toOrdinal) {
        HollowDiffView objectView = (HollowDiffView) session.getObjectView();

        if(objectView != null
                && objectView.getType().equals(type)
                && objectView.getToOrdinal() == toOrdinal
                && objectView.getFromOrdinal() == fromOrdinal) {
            return objectView;
        }

        HollowDiffViewRow rootRow = new HollowObjectDiffViewGenerator(diffUI.getDiff().getFromStateEngine(), diffUI.getDiff().getToStateEngine(), diffUI, type, fromOrdinal, toOrdinal).getHollowDiffViewRows();
        objectView = new HollowDiffView(type, fromOrdinal, toOrdinal, rootRow, diffUI.getExactRecordMatcher());
        objectView.resetView();
        session.setObjectView(objectView);

        return objectView;
    }
}
