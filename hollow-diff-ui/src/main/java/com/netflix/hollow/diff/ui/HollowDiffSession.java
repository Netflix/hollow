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
package com.netflix.hollow.diff.ui;

import com.netflix.hollow.diffview.HollowObjectView;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Response;

public class HollowDiffSession {

    private static final long SESSION_ABANDONMENT_MILLIS = 60 * 60 * 1000;

    private final Map<String, Object> sessionParams;
    private HollowObjectView currentObjectView;
    private long lastAccessed;

    public HollowDiffSession() {
        this.sessionParams = new ConcurrentHashMap<String, Object>();
    }

    public void setAttribute(String param, Object value) {
        sessionParams.put(param, value);
    }

    public Object getAttribute(String param) {
        return sessionParams.get(param);
    }

    public void setObjectView(HollowObjectView view) {
        this.currentObjectView = view;
    }

    public HollowObjectView getObjectView() {
        return currentObjectView;
    }

    public void updateLastAccessed() {
        lastAccessed = System.currentTimeMillis();
    }

    private static final ConcurrentHashMap<Long, HollowDiffSession> sessions = new ConcurrentHashMap<Long, HollowDiffSession>();

    public static HollowDiffSession getSession(HttpServletRequest req, HttpServletResponse resp) {
        Long sessionId = null;

        if(req.getCookies() != null) {
            for(Cookie cookie : req.getCookies()) {
                if("hollowDiffSessionId".equals(cookie.getName())) {
                    sessionId = Long.valueOf(cookie.getValue());
                }
            }
        }

        if(sessionId == null) {
            sessionId = new Random().nextLong() & Long.MAX_VALUE;
            Cookie cookie = new Cookie("hollowDiffSessionId", sessionId.toString());
            cookie.setComment(Response.HTTP_ONLY_COMMENT);
            resp.addCookie(cookie);
        }

        HollowDiffSession session = sessions.get(sessionId);
        if(session == null) {
            session = new HollowDiffSession();
            HollowDiffSession existingSession = sessions.putIfAbsent(sessionId, session);
            if(existingSession != null)
                session = existingSession;
        }

        return session;
    }

    static {
        Thread sessionCleanupThread = new Thread(new Runnable() {
            public void run() {
                Iterator<Map.Entry<Long, HollowDiffSession>> iter = sessions.entrySet().iterator();
                while(iter.hasNext()) {
                    Map.Entry<Long, HollowDiffSession> entry = iter.next();
                    if(entry.getValue().lastAccessed + SESSION_ABANDONMENT_MILLIS < System.currentTimeMillis())
                        iter.remove();
                }

                try {
                    Thread.sleep(60000L);
                } catch (InterruptedException e) { }
            }
        });

        sessionCleanupThread.setDaemon(true);
        sessionCleanupThread.start();
    }


}
