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
package com.netflix.hollow.diff.ui;

import static com.netflix.hollow.ui.HollowUISession.getSession;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.diff.ui.pages.DiffFieldPage;
import com.netflix.hollow.diff.ui.pages.DiffObjectPage;
import com.netflix.hollow.diff.ui.pages.DiffOverviewPage;
import com.netflix.hollow.diff.ui.pages.DiffPage;
import com.netflix.hollow.diff.ui.pages.DiffTypePage;
import com.netflix.hollow.diffview.DiffViewOutputGenerator;
import com.netflix.hollow.diffview.HollowDiffViewProvider;
import com.netflix.hollow.diffview.HollowObjectViewProvider;
import com.netflix.hollow.diffview.effigy.CustomHollowEffigyFactory;
import com.netflix.hollow.diffview.effigy.HollowRecordDiffUI;
import com.netflix.hollow.diffview.effigy.pairer.exact.DiffExactRecordMatcher;
import com.netflix.hollow.diffview.effigy.pairer.exact.ExactRecordMatcher;
import com.netflix.hollow.tools.diff.HollowDiff;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.app.VelocityEngine;

public class HollowDiffUI implements HollowRecordDiffUI {

    private final String baseURLPath;
    private final String diffUIPath;
    private final HollowDiff diff;
    private final VelocityEngine velocity;
    private final String fromBlobName;
    private final String toBlobName;

    private final DiffOverviewPage overviewPage;
    private final DiffTypePage typePage;
    private final DiffFieldPage fieldPage;
    private final DiffObjectPage objectPage;

    private final HollowObjectViewProvider viewProvider;
    private final DiffViewOutputGenerator diffViewOutputGenerator;

    private final Map<String, PrimaryKey> matchHints;
    private final Map<String, CustomHollowEffigyFactory> customHollowEffigyFactories;
    private final ExactRecordMatcher exactRecordMatcher;

    HollowDiffUI(String baseURLPath, String diffUIPath, HollowDiff diff, String fromBlobName, String toBlobName, VelocityEngine ve) {
        this.baseURLPath = baseURLPath;
        this.diffUIPath = baseURLPath + "/" + diffUIPath;
        this.diff = diff;
        this.velocity = ve;
        this.fromBlobName = fromBlobName;
        this.toBlobName = toBlobName;
        this.overviewPage = new DiffOverviewPage(this);
        this.typePage = new DiffTypePage(this);
        this.fieldPage = new DiffFieldPage(this);
        this.objectPage = new DiffObjectPage(this);
        this.viewProvider = new HollowDiffViewProvider(this);
        this.diffViewOutputGenerator = new DiffViewOutputGenerator(viewProvider);
        this.customHollowEffigyFactories = new HashMap<String, CustomHollowEffigyFactory>();
        this.matchHints = new HashMap<String, PrimaryKey>();
        this.exactRecordMatcher = new DiffExactRecordMatcher(diff.getEqualityMapping());
    }
    
    public boolean serveRequest(String pageName, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if("diffrowdata".equals(pageName)) {
            diffViewOutputGenerator.uncollapseRow(req, resp);
            return true;
        } else if("collapsediffrow".equals(pageName)) {
            diffViewOutputGenerator.collapseRow(req, resp);
            return true;
        }

        resp.setContentType("text/html;charset=UTF-8");

        if("".equals(pageName) || "overview".equals(pageName)) {
            render(overviewPage, req, resp);
        } else if("typediff".equals(pageName)) {
            render(typePage, req, resp);
        } else if("fielddiff".equals(pageName)) {
            render(fieldPage, req, resp);
        } else if("objectdiff".equals(pageName)) {
            render(objectPage, req, resp);
        } else {
            return false;
        }

        return true;
    }

    public HollowDiff getDiff() {
        return diff;
    }

    public String getFromBlobName() {
        return fromBlobName;
    }

    public String getToBlobName() {
        return toBlobName;
    }

    public VelocityEngine getVelocity() {
        return velocity;
    }

    public String getBaseURLPath() {
        return baseURLPath;
    }

    public String getDiffUIPath() {
        return diffUIPath;
    }

    public void addCustomHollowEffigyFactory(String typeName, CustomHollowEffigyFactory factory) {
        customHollowEffigyFactories.put(typeName, factory);
    }

    @Override
    public CustomHollowEffigyFactory getCustomHollowEffigyFactory(String typeName) {
        return customHollowEffigyFactories.get(typeName);
    }
    
    public void addMatchHint(PrimaryKey matchHint) {
        this.matchHints.put(matchHint.getType(), matchHint);
    }

    @Override
    public Map<String, PrimaryKey> getMatchHints() {
        return matchHints;
    }
    
    @Override
    public ExactRecordMatcher getExactRecordMatcher() {
        return exactRecordMatcher;
    }

    public HollowObjectViewProvider getHollowObjectViewProvider() {
        return viewProvider;
    }

    public DiffViewOutputGenerator getDiffViewOutputGenerator() {
        return diffViewOutputGenerator;
    }

    private void render(DiffPage page, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        page.render(req, getSession(req, resp), resp.getWriter());
    }

}
