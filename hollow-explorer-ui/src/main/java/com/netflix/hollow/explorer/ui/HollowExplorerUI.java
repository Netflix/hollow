/*
 *
 *  Copyright 2017 Netflix, Inc.
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

import com.netflix.hollow.explorer.ui.pages.BrowseSelectedTypePage;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.explorer.ui.pages.ShowAllTypesPage;
import com.netflix.hollow.ui.HollowUIRouter;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HollowExplorerUI extends HollowUIRouter {
    
    private final HollowClient client;
    private final HollowReadStateEngine stateEngine;
    
    private String headerDisplayString;
    
    private final ShowAllTypesPage showAllTypesPage;
    private final BrowseSelectedTypePage browseTypePage;
    
    public HollowExplorerUI(String baseUrlPath, HollowClient client) {
        this(baseUrlPath, client, null);
    }
    
    public HollowExplorerUI(String baseUrlPath, HollowReadStateEngine stateEngine) {
        this(baseUrlPath, null, stateEngine);
    }
    
    private HollowExplorerUI(String baseUrlPath, HollowClient client, HollowReadStateEngine stateEngine) {
        super(baseUrlPath);
        this.client = client;
        this.stateEngine = stateEngine;
        
        this.showAllTypesPage = new ShowAllTypesPage(this);
        this.browseTypePage = new BrowseSelectedTypePage(this);
    }

    public boolean handle(String target, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        String pageName = getTargetRootPath(target);
        
        if("".equals(pageName) || "home".equals(pageName)) {
            showAllTypesPage.render(req, resp.getWriter());
            return true;
        } else if("type".equals(pageName)) {
            browseTypePage.render(req, resp.getWriter());
            return true;
        }
        
        return false;
    }

    public long getCurrentStateVersion() {
        if(client == null)
            return Long.MIN_VALUE;
        return client.getCurrentVersionId();
    }
    
    public HollowReadStateEngine getStateEngine() {
        if(client == null)
            return stateEngine;
        return client.getStateEngine();
    }
    
    public void setHeaderDisplayString(String str) {
        this.headerDisplayString = str;
    }
    
    public String getHeaderDisplayString() {
        return headerDisplayString;
    }
    
}
