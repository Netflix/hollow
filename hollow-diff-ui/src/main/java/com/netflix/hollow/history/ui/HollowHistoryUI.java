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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    /**
     * HollowHistoryUI constructor that builds history for a consumer that transitions forwards i.e. in increasing
     * version order (v1, v2, v3...). This constructor defaults max states to 1024 and time zone to PST.
     *
     * @param baseUrlPath url path for history UI endpoint
     * @param consumer HollowConsumer (already initialized with data) that will be traversing forward deltas or double snapshots
     */
    public HollowHistoryUI(String baseUrlPath, HollowConsumer consumer) {
        this(baseUrlPath, consumer, 1024, VersionTimestampConverter.PACIFIC_TIMEZONE);
    }

    public HollowHistoryUI(String baseUrlPath, HollowConsumer consumer, TimeZone timeZone) {
        this(baseUrlPath, consumer, 1024, timeZone);
    }

    public HollowHistoryUI(String baseUrlPath, HollowConsumer consumer, int numStatesToTrack, TimeZone timeZone) {
        this(baseUrlPath, createHistory(consumer, numStatesToTrack), timeZone);
    }

    /**
     * HollowHistoryUI that supports building history in both directions simultaneously.
     * Fwd and rev consumers should be initialized to the same version before calling this constructor.
     * This constructor defaults max states to 1024 and time zone to PST.
     *
     * @param baseUrlPath url path for history UI endpoint
     * @param consumerFwd HollowConsumer (already initialized with data) that will be traversing forward deltas or double snapshots
     * @param consumerRev HollowConsumer (also initialized to the same version as consumerFwd) that will be traversing reverse deltas
     */
    public HollowHistoryUI(String baseUrlPath, HollowConsumer consumerFwd, HollowConsumer consumerRev) {
        this(baseUrlPath, consumerFwd, consumerRev, 1024, VersionTimestampConverter.PACIFIC_TIMEZONE);
    }

    public HollowHistoryUI(String baseUrlPath, HollowConsumer consumerFwd, HollowConsumer consumerRev, int numStatesToTrack, TimeZone timeZone) {
        this(baseUrlPath, createHistory(consumerFwd, consumerRev, numStatesToTrack), timeZone);
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

        this.customHollowEffigyFactories = new HashMap<>();
        this.customHollowRecordNamers = new HashMap<>();
        this.matchHints = new HashMap<>();
        this.overviewDisplayHeaders = new String[0];
        this.timeZone = timeZone;
    }

    private static HollowHistory createHistory(HollowConsumer consumer, int numStatesToTrack) {
        return createHistory(consumer, null, numStatesToTrack);
    }

    private static HollowHistory createHistory(HollowConsumer consumerFwd, HollowConsumer consumerRev, int numStatesToTrack) {
        if (consumerRev == null) {
            consumerFwd.getRefreshLock().lock();
            try {
                HollowHistory history = new HollowHistory(consumerFwd.getStateEngine(), consumerFwd.getCurrentVersionId(), numStatesToTrack);
                consumerFwd.addRefreshListener(new HollowHistoryRefreshListener(history));
                return history;
            } finally {
                consumerFwd.getRefreshLock().unlock();
            }
        } else {
            consumerFwd.getRefreshLock().lock();
            consumerRev.getRefreshLock().lock();
            try {
                HollowHistory history = new HollowHistory(consumerFwd.getStateEngine(), consumerRev.getStateEngine(),
                        consumerFwd.getCurrentVersionId(), consumerRev.getCurrentVersionId(), numStatesToTrack);
                HollowHistoryRefreshListener listener = new HollowHistoryRefreshListener(history);
                consumerFwd.addRefreshListener(listener);
                consumerRev.addRefreshListener(listener);
                return history;
            } finally {
                consumerFwd.getRefreshLock().unlock();
                consumerRev.getRefreshLock().unlock();
            }

        }
    }

    public HollowHistory getHistory() {
        return history;
    }

    @Override
    public boolean handle(String target, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String pageName = getTargetRootPath(target);

        if("diffrowdata".equals(pageName)) {
            diffViewOutputGenerator.uncollapseRow(req, resp);
            return true;
        } else if("collapsediffrow".equals(pageName)) {
            diffViewOutputGenerator.collapseRow(req, resp);
            return true;
        }

        resp.setContentType("text/html");

        if("resource".equals(pageName)) {
            if(serveResource(req, resp, getResourceName(target)))
                return true;
        } else if("".equals(pageName) || "overview".equals(pageName)) {
            if(req.getParameter("format") != null && req.getParameter("format").equals("json")) {
                overviewPage.sendJson(req, resp);
                return true;
            }
            overviewPage.render(req, getSession(req, resp), resp.getWriter());
        } else if("state".equals(pageName)) {
            if(req.getParameter("format") != null && req.getParameter("format").equals("json")) {
                statePage.sendJson(req, resp);
                return true;
            }
            statePage.render(req, getSession(req, resp), resp.getWriter());
            return true;
        } else if("statetype".equals(pageName)) {
            if(req.getParameter("format") != null && req.getParameter("format").equals("json")) {
                stateTypePage.sendJson(req, getSession(req, resp),  resp);
                return true;
            }
            stateTypePage.render(req, getSession(req, resp), resp.getWriter());
            return true;
        } else if("statetypeexpand".equals(pageName)) {
            stateTypeExpandPage.render(req, getSession(req, resp), resp.getWriter());
            return true;
        } else if("query".equals(pageName)) {
            queryPage.render(req, getSession(req, resp), resp.getWriter());
            return true;
        } else if("historicalObject".equals(pageName)) {
            objectDiffPage.render(req, getSession(req, resp), resp.getWriter());
            return true;
        }
        return false;
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
