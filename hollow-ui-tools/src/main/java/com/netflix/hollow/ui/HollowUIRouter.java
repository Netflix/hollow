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
package com.netflix.hollow.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public abstract class HollowUIRouter extends HttpServlet {

    protected final String baseUrlPath;
    protected final VelocityEngine velocityEngine;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            handle(request.getPathInfo(), request, response);
        } catch (Exception ex) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            ex.printStackTrace(printWriter);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, stringWriter.toString());
        }
    }

    public abstract boolean handle(String target, HttpServletRequest req, HttpServletResponse resp) throws IOException;

    public HollowUIRouter(String baseUrlPath) {
        if(!baseUrlPath.startsWith("/"))
            baseUrlPath = "/" + baseUrlPath;
        if(baseUrlPath.endsWith("/"))
            baseUrlPath = baseUrlPath.substring(0, baseUrlPath.length() - 1);

        this.baseUrlPath = baseUrlPath;
        this.velocityEngine = initVelocity();
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public String getBaseURLPath() {
        return baseUrlPath;
    }

    protected String getTargetRootPath(String target) {
        int baseLength = baseUrlPath.length() + 1;

        if(target.length() < baseLength)
            return "";

        if (target == null) {
            throw new IllegalStateException("target is null. It defaults to HttpServletRequest::getPathInfo() but can be " +
                    "customized by invoking handle method on HollowExplorerUI HollowDiffUI et al classes.");
        }
        int secondSlashIndex = target.indexOf('/', baseLength);

        if(secondSlashIndex == -1)
            return target.substring(baseLength);

        return target.substring(baseLength, secondSlashIndex);
    }

    protected String getResourceName(String target, String diffUIKey) {
        if (diffUIKey == null || diffUIKey.length() == 0) {   // for diff at path ""
            int baseLength = baseUrlPath.length() + 1;

            int secondSlashIndex = target.indexOf('/', baseLength);
            if(secondSlashIndex == -1) {
                // a diff hosted at path ""
                secondSlashIndex = target.indexOf('/');
                if (secondSlashIndex == -1) {
                    return "";
                }
            }
            return target.substring(secondSlashIndex + 1);
        } else {
            return getResourceName(target);
        }
    }

    protected String getResourceName(String target) {
        int baseLength = baseUrlPath.length() + 1;

        int secondSlashIndex = target.indexOf('/', baseLength);
        if(secondSlashIndex == -1) {
            return "";
        }
        return target.substring(secondSlashIndex + 1);
    }

    protected boolean serveResource(HttpServletRequest req, HttpServletResponse resp, String resourceName) {
        try {
            if(resourceName.endsWith(".css")) {
                resp.setContentType("text/css");
            } else if(resourceName.endsWith(".js")) {
                resp.setContentType("text/javascript");
            } else if(resourceName.endsWith(".png")) {
                resp.setContentType("image/png");
            }

            InputStream is = this.getClass().getResourceAsStream("/" + resourceName);

            IOUtils.copy(is, resp.getOutputStream());
            return true;
        } catch(Exception e){
            return false;
        }
    }

    protected VelocityEngine initVelocity() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        return ve;
    }

}
