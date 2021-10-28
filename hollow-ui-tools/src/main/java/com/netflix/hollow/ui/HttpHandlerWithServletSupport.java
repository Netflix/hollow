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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpUtils;


public class HttpHandlerWithServletSupport implements HttpHandler {

    private HttpServlet servlet;

    private final class RequestWrapper extends HttpServletRequestWrapper {
        private final HttpExchange ex;
        private final Map<String, String[]> postData;
        private final ServletInputStream is;
        private final Map<String, Object> attributes = new HashMap<>();

        private RequestWrapper(HttpServletRequest request, HttpExchange ex, Map<String, String[]> postData, ServletInputStream is) {
            super(request);
            this.ex = ex;
            this.postData = postData;
            this.is = is;
        }

        @Override
        public Cookie[] getCookies() {
            Headers headers = ex.getRequestHeaders();
            if (headers != null) {
                List<String> strCookies = headers.get("Cookie");
                if (strCookies != null) {
                    Cookie[] cookies = new Cookie[strCookies.size()];
                    int i=0;
                    for (String cookieString : strCookies) {
                         String[] tokens = cookieString.split("\\s*;\\s*");
                         for (String token : tokens) {
                              String[] keyVal = token.split("\\s*=\\s*");
                              if(keyVal.length==2){
                                 cookies[i] = new Cookie(keyVal[0], keyVal[1]);
                                 i++;
                              }
                         }
                   }
                   return cookies;
               }
            }
            return null;
        }

        @Override
        public String getHeader(String name) {
            return ex.getRequestHeaders().getFirst(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return new Vector<String>(ex.getRequestHeaders().get(name)).elements();
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return new Vector<String>(ex.getRequestHeaders().keySet()).elements();
        }

        @Override
        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public void setAttribute(String name, Object o) {
            this.attributes.put(name, o);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return new Vector<String>(attributes.keySet()).elements();
        }

        @Override
        public String getMethod() {
            return ex.getRequestMethod();
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return is;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public String getPathInfo() {
            return ex.getRequestURI().getPath();
        }

        @Override
        public String getParameter(String name) {
            String[] arr = postData.get(name);
            return arr != null ? (arr.length > 1 ? Arrays.toString(arr) : arr[0]) : null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return postData;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return new Vector<String>(postData.keySet()).elements();
        }
    }

    private final class ResponseWrapper extends HttpServletResponseWrapper {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final ServletOutputStream servletOutputStream = new ServletOutputStream() {

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

        };

        private final HttpExchange ex;
        private final PrintWriter printWriter;
        private int status = HttpServletResponse.SC_OK;

        private ResponseWrapper(HttpServletResponse response, HttpExchange ex) {
            super(response);
            this.ex = ex;
            printWriter = new PrintWriter(servletOutputStream);
        }

        @Override
        public void setContentType(String type) {
            ex.getResponseHeaders().add("Content-Type", type);
        }

        @Override
        public void setHeader(String name, String value) {
            ex.getResponseHeaders().add(name, value);
        }

        @Override
        public javax.servlet.ServletOutputStream getOutputStream() throws IOException {
            return servletOutputStream;
        }

        @Override
        public void setContentLength(int len) {
            ex.getResponseHeaders().add("Content-Length", len + "");
        }

        @Override
        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            this.status = sc;
            if (msg != null) {
                printWriter.write(msg);
            }
        }

        @Override
        public void sendError(int sc) throws IOException {
            sendError(sc, null);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return printWriter;
        }

        @Override
        public void addCookie(Cookie c) {
        }

        public void complete() throws IOException {
            try {
                printWriter.flush();
                ex.sendResponseHeaders(status, outputStream.size());
                if (outputStream.size() > 0) {
                    ex.getResponseBody().write(outputStream.toByteArray());
                }
                ex.getResponseBody().flush();
            } catch (Exception e) {
                throw new IOException(e);
            } finally {
                ex.close();
            }
        }
    }

    public HttpHandlerWithServletSupport(HttpServlet servlet) {
        this.servlet = servlet;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void handle(final HttpExchange ex) throws IOException {
        byte[] inBytes = getBytes(ex.getRequestBody());
        ex.getRequestBody().close();
        final ByteArrayInputStream newInput = new ByteArrayInputStream(inBytes);
        final ServletInputStream is = new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return newInput.read();
            }

            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

        };

        Map<String, String[]> parsePostData = new HashMap<>();

        try {
            parsePostData.putAll(HttpUtils.parseQueryString(ex.getRequestURI().getQuery()));

            // check if any postdata to parse
            parsePostData.putAll(HttpUtils.parsePostData(inBytes.length, is));
        } catch (IllegalArgumentException e) {
            // no postData - just reset inputstream
            newInput.reset();
        }
        final Map<String, String[]> postData = parsePostData;

        RequestWrapper req = new RequestWrapper(createUnimplementAdapter(HttpServletRequest.class), ex, postData, is);

        ResponseWrapper resp = new ResponseWrapper(createUnimplementAdapter(HttpServletResponse.class), ex);

        try {
            servlet.service(req, resp);
            resp.complete();
        } catch (ServletException e) {
            throw new IOException(e);
        }
    }

    private static byte[] getBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int r = in.read(buffer);
            if (r == -1)
                break;
            out.write(buffer, 0, r);
        }
        return out.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private static <T> T createUnimplementAdapter(Class<T> httpServletApi) {
        class UnimplementedHandler implements InvocationHandler {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                throw new UnsupportedOperationException("Not implemented: " + method + ", args=" + Arrays.toString(args));
            }
        }

        return (T) Proxy.newProxyInstance(UnimplementedHandler.class.getClassLoader(),
                new Class<?>[] { httpServletApi },
                new UnimplementedHandler());
    }
}
