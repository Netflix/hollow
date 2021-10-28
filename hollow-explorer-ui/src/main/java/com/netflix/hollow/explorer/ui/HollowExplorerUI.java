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
package com.netflix.hollow.explorer.ui;

import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.explorer.ui.pages.BrowseSchemaPage;
import com.netflix.hollow.explorer.ui.pages.BrowseSelectedTypePage;
import com.netflix.hollow.explorer.ui.pages.QueryPage;
import com.netflix.hollow.explorer.ui.pages.ShowAllTypesPage;
import com.netflix.hollow.ui.HollowUIRouter;
import com.netflix.hollow.ui.HollowUISession;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("deprecation")
public class HollowExplorerUI extends HollowUIRouter {
    
    private final HollowConsumer consumer;
    private final HollowClient client;
    private final HollowReadStateEngine stateEngine;
    /** General purpose map to store header string and corresponding values used in HollowExplorer. It also stores headerDisplayString
     *  for backwards compatibility.
     **/
    private final HashMap<String, String> headerDisplayMap = new HashMap<>();
    private final static String HEADER_DISPLAY_STRING = "headerDisplayString";

    private final ShowAllTypesPage showAllTypesPage;
    private final BrowseSelectedTypePage browseTypePage;
    private final BrowseSchemaPage browseSchemaPage;
    private final QueryPage queryPage;
    
    public HollowExplorerUI(String baseUrlPath, HollowConsumer consumer) {
        this(baseUrlPath, consumer, null, null);
    }
    
    public HollowExplorerUI(String baseUrlPath, HollowClient client) {
        this(baseUrlPath, null, client, null);
    }
    
    public HollowExplorerUI(String baseUrlPath, HollowReadStateEngine stateEngine) {
        this(baseUrlPath, null, null, stateEngine);
    }
    
    private HollowExplorerUI(String baseUrlPath, HollowConsumer consumer, HollowClient client, HollowReadStateEngine stateEngine) {
        super(baseUrlPath);
        this.consumer = consumer;
        this.client = client;
        this.stateEngine = stateEngine;
        
        this.showAllTypesPage = new ShowAllTypesPage(this);
        this.browseTypePage = new BrowseSelectedTypePage(this);
        this.browseSchemaPage = new BrowseSchemaPage(this);
        this.queryPage = new QueryPage(this);
    }

    public void handle( String target, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pageName = getTargetRootPath(req.getPathInfo());
        
        HollowUISession session = HollowUISession.getSession(req, resp);
        
        if("".equals(pageName) || "home".equals(pageName)) {
            showAllTypesPage.render(req, resp, session);
            return;
        } else if("type".equals(pageName)) {
            browseTypePage.render(req, resp, session);
            return;
        } else if("schema".equals(pageName)) {
            browseSchemaPage.render(req, resp, session);
            return;
        } else if("query".equals(pageName)) {
            queryPage.render(req, resp, session);
            return;
        }
    }

    public long getCurrentStateVersion() {
        if(consumer != null)
            return consumer.getCurrentVersionId();
        if(client != null)
            return client.getCurrentVersionId();
        return Long.MIN_VALUE;
    }
    
    public HollowReadStateEngine getStateEngine() {
        if(consumer != null)
            return consumer.getStateEngine();
        if(client != null)
            return client.getStateEngine();
        return stateEngine;
    }

    public String getHeaderDisplayString() {
        return headerDisplayMap.get(HEADER_DISPLAY_STRING);
    }

    public void setHeaderDisplayString(String str) {
        this.headerDisplayMap.put(HEADER_DISPLAY_STRING, str);
    }

    public void addToHeaderDisplayMap(String key, String value) {
        headerDisplayMap.put(key, value);
    }

    public String getFromHeaderDisplayMap(String key) {
        return headerDisplayMap.get(key);
    }
    
}
