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

import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.ui.HollowUIRouter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HollowDiffUIRouter extends HollowUIRouter {

    private final Map<String, HollowDiffUI> diffUIs;

    public HollowDiffUIRouter() {
        this("");
    }

    public HollowDiffUIRouter(String baseUrlPath) {
        super(baseUrlPath);
        this.diffUIs = new LinkedHashMap<>();
    }

    public boolean handle(String target, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String diffUIKey = getTargetRootPath(target);

        if("resource".equals(diffUIKey)) {
            if(serveResource(req, resp, getResourceName(target)))
                return true;
        } else {
            HollowDiffUI ui = diffUIs.get(diffUIKey);

            if(ui != null) {
                if(ui.serveRequest(getResourceName(target), req, resp))
                    return true;
            }
        }

        return false;
    }

    public Map<String, HollowDiffUI> getDiffUIs() {
        return diffUIs;
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff, String fromBlobName, String toBlobName) {
        HollowDiffUI diffUI = new HollowDiffUI(baseUrlPath, diffPath, diff, fromBlobName, toBlobName, velocityEngine);
        diffUIs.put(diffPath, diffUI);
        return diffUI;
    }

    public void removeDiff(String diffPath) {
        diffUIs.remove(diffPath);
    }

}
