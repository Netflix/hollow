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

import static com.netflix.hollow.core.util.Threads.daemonThread;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HollowUISession {

    private static final long SESSION_ABANDONMENT_MILLIS = 60 * 60 * 1000;
    private static final String HTTP_ONLY_COMMENT = "__HTTP_ONLY__";

    private final Map<String, Object> sessionParams;
    private long lastAccessed;

    public HollowUISession() {
        this.sessionParams = new ConcurrentHashMap<String, Object>();
    }

    public void clearAttribute(String param) {
        sessionParams.remove(param);
    }
    
    public void setAttribute(String param, Object value) {
        sessionParams.put(param, value);
    }

    public Object getAttribute(String param) {
        return sessionParams.get(param);
    }

    public void updateLastAccessed() {
        lastAccessed = System.currentTimeMillis();
    }

    private static final ConcurrentHashMap<Long, HollowUISession> sessions = new ConcurrentHashMap<Long, HollowUISession>();

    public static HollowUISession getSession(HttpServletRequest req, HttpServletResponse resp) {
        Long sessionId = null;

        if(req.getCookies() != null) {
            for(Cookie cookie : req.getCookies()) {
                if("hollowUISessionId".equals(cookie.getName())) {
                    sessionId = Long.valueOf(cookie.getValue());
                }
            }
        }

        if(sessionId == null) {
            sessionId = new Random().nextLong() & Long.MAX_VALUE;
            Cookie cookie = new Cookie("hollowUISessionId", sessionId.toString());
            cookie.setComment(HTTP_ONLY_COMMENT);
            resp.addCookie(cookie);
        }

        HollowUISession session = sessions.get(sessionId);
        if(session == null) {
            session = new HollowUISession();
            HollowUISession existingSession = sessions.putIfAbsent(sessionId, session);
            if(existingSession != null)
                session = existingSession;
        }
        session.updateLastAccessed();

        return session;
    }

    static {
        daemonThread(HollowUISession::cleanupSessions, HollowUISession.class, "session-cleanup")
                .start();
    }

    private static void cleanupSessions() {
        while (!Thread.currentThread().isInterrupted()) {
            sessions.values()
                    .removeIf(s-> s.lastAccessed + SESSION_ABANDONMENT_MILLIS < System.currentTimeMillis());
            try {
                MINUTES.sleep(1);
            } catch (InterruptedException e) { }
        }
    }
}
