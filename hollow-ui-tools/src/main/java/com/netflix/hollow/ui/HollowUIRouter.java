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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public abstract class HollowUIRouter extends HttpServlet {

    protected final String baseUrlPath;
    protected final VelocityEngine velocityEngine;

    @Override
    public abstract void doGet(HttpServletRequest request,
        HttpServletResponse response) throws IOException;

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

        int secondSlashIndex = target.indexOf('/', baseLength);

        if(secondSlashIndex == -1)
            return target.substring(baseLength);

        return target.substring(baseLength, secondSlashIndex);
    }

    protected String getResourceName(String target) {
        int baseLength = baseUrlPath.length() + 1;

        int secondSlashIndex = target.indexOf('/', baseLength);
        if(secondSlashIndex == -1)
            return "";
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
