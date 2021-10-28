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
package com.netflix.hollow.history.ui;

import static com.netflix.hollow.ui.HollowUISession.getSession;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.diffview.DiffViewOutputGenerator;
import com.netflix.hollow.diffview.HollowHistoryViewProvider;
import com.netflix.hollow.diffview.HollowObjectViewProvider;
import com.netflix.hollow.diffview.effigy.CustomHollowEffigyFactory;
import com.netflix.hollow.diffview.effigy.HollowRecordDiffUI;
import com.netflix.hollow.diffview.effigy.pairer.exact.ExactRecordMatcher;
import com.netflix.hollow.diffview.effigy.pairer.exact.HistoryExactRecordMatcher;
import com.netflix.hollow.history.ui.naming.HollowHistoryRecordNamer;
import com.netflix.hollow.history.ui.pages.HistoricalObjectDiffPage;
import com.netflix.hollow.history.ui.pages.HistoryOverviewPage;
import com.netflix.hollow.history.ui.pages.HistoryQueryPage;
import com.netflix.hollow.history.ui.pages.HistoryStatePage;
import com.netflix.hollow.history.ui.pages.HistoryStateTypeExpandGroupPage;
import com.netflix.hollow.history.ui.pages.HistoryStateTypePage;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.ui.HollowUIRouter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HollowHistoryUI extends HollowUIRouter implements HollowRecordDiffUI {

    private final HollowHistory history;

    private final HistoryOverviewPage overviewPage;
    private final HistoryStatePage statePage;
    private final HistoryStateTypePage stateTypePage;
    private final HistoryStateTypeExpandGroupPage stateTypeExpandPage;
    private final HistoryQueryPage queryPage;
    private final HistoricalObjectDiffPage objectDiffPage;

    private final HollowObjectViewProvider viewProvider;
    private final DiffViewOutputGenerator diffViewOutputGenerator;

    private final Map<String, CustomHollowEffigyFactory> customHollowEffigyFactories;
    private final Map<String, HollowHistoryRecordNamer> customHollowRecordNamers;
    private final Map<String, PrimaryKey> matchHints;
    private final TimeZone timeZone;

    private String[] overviewDisplayHeaders;

    public HollowHistoryUI(String baseUrlPath, HollowConsumer consumer) {
        this(baseUrlPath, consumer, 1024, VersionTimestampConverter.PACIFIC_TIMEZONE);
    }

    public HollowHistoryUI(String baseUrlPath, HollowConsumer consumer, TimeZone timeZone) {
        this(baseUrlPath, consumer, 1024, timeZone);
    }

    public HollowHistoryUI(String baseUrlPath, HollowConsumer consumer, int numStatesToTrack, TimeZone timeZone) {
        this(baseUrlPath, createHistory(consumer, numStatesToTrack), timeZone);
    }

    private static HollowHistory createHistory(HollowConsumer consumer, int numStatesToTrack) {
        consumer.getRefreshLock().lock();
        try {
            HollowHistory history = new HollowHistory(consumer.getStateEngine(), consumer.getCurrentVersionId(), numStatesToTrack);
            consumer.addRefreshListener(new HollowHistoryRefreshListener(history));
            return history;
        } finally {
            consumer.getRefreshLock().unlock();
        }
    }

    public HollowHistoryUI(String baseUrlPath, HollowHistory history) {
        this(baseUrlPath, history, VersionTimestampConverter.PACIFIC_TIMEZONE);
    }

    public HollowHistoryUI(String baseUrlPath, HollowHistory history, TimeZone timeZone) {
        super(baseUrlPath);
        this.history = history;

        this.overviewPage = new HistoryOverviewPage(this);
        this.statePage = new HistoryStatePage(this);
        this.queryPage = new HistoryQueryPage(this);
        this.objectDiffPage = new HistoricalObjectDiffPage(this);
        this.stateTypePage = new HistoryStateTypePage(this);
        this.stateTypeExpandPage = new HistoryStateTypeExpandGroupPage(this);

        this.viewProvider = new HollowHistoryViewProvider(this);
        this.diffViewOutputGenerator = new DiffViewOutputGenerator(viewProvider);

        this.customHollowEffigyFactories = new HashMap<String, CustomHollowEffigyFactory>();
        this.customHollowRecordNamers = new HashMap<String, HollowHistoryRecordNamer>();
        this.matchHints = new HashMap<String, PrimaryKey>();
        this.overviewDisplayHeaders = new String[0];
        this.timeZone = timeZone;
    }
    
    public HollowHistory getHistory() {
        return history;
    }
 
    public void handle(String target, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pageName = getTargetRootPath(req.getPathInfo());

        if("diffrowdata".equals(pageName)) {
            diffViewOutputGenerator.uncollapseRow(req, resp);
            return;
        } else if("collapsediffrow".equals(pageName)) {
            diffViewOutputGenerator.collapseRow(req, resp);
            return;
        }

        resp.setContentType("text/html");


        if("resource".equals(pageName)) {
            if(serveResource(req, resp, getResourceName(req.getPathInfo())))
                return;
        } else if("".equals(pageName) || "overview".equals(pageName)) {
        	if(req.getParameter("format") != null && req.getParameter("format").equals("json")) {
        		overviewPage.sendJson(req, resp);
        		return;
        	}
            overviewPage.render(req, getSession(req, resp), resp.getWriter());
        } else if("state".equals(pageName)) {
        	if(req.getParameter("format") != null && req.getParameter("format").equals("json")) {
        		statePage.sendJson(req, resp);
        		return;
        	}
            statePage.render(req, getSession(req, resp), resp.getWriter());
        } else if("statetype".equals(pageName)) {
        	if(req.getParameter("format") != null && req.getParameter("format").equals("json")) {
        		stateTypePage.sendJson(req, getSession(req, resp),  resp);
        		return;
        	}
            stateTypePage.render(req, getSession(req, resp), resp.getWriter());
        } else if("statetypeexpand".equals(pageName)) {
            stateTypeExpandPage.render(req, getSession(req, resp), resp.getWriter());
        } else if("query".equals(pageName)) {
            queryPage.render(req, getSession(req, resp), resp.getWriter());
        } else if("historicalObject".equals(pageName)) {
            objectDiffPage.render(req, getSession(req, resp), resp.getWriter());
        }
    }
    
    public void addCustomHollowRecordNamer(String typeName, HollowHistoryRecordNamer recordNamer) {
        customHollowRecordNamers.put(typeName, recordNamer);
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
    
    public ExactRecordMatcher getExactRecordMatcher() {
        return HistoryExactRecordMatcher.INSTANCE;
    }

    public void setOverviewDisplayHeaders(String... displayHeaders) {
        this.overviewDisplayHeaders = displayHeaders;
    }
    
    public HollowHistoryRecordNamer getHistoryRecordNamer(String typeName) {
         HollowHistoryRecordNamer recordNamer = customHollowRecordNamers.get(typeName);
         if(recordNamer == null)
             return HollowHistoryRecordNamer.DEFAULT_RECORD_NAMER;
         return recordNamer;
    }
    
    public String[] getOverviewDisplayHeaders() {
        return overviewDisplayHeaders;
    }

    public HollowObjectViewProvider getViewProvider() {
        return viewProvider;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }
}
