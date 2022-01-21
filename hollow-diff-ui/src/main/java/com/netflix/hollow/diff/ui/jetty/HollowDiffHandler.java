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
package com.netflix.hollow.diff.ui.jetty;

import com.netflix.hollow.diff.ui.HollowDiffUIRouter;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class HollowDiffHandler extends AbstractHandler {

    private final HollowDiffUIRouter router;

    public HollowDiffHandler() {
        this.router = new HollowDiffUIRouter();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(router.handle(target, req, resp))
            baseRequest.setHandled(true);
    }

    public HollowDiffUIRouter getRouter() {
        return router;
    }

}
