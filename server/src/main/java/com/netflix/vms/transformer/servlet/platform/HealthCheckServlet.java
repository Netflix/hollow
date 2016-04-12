package com.netflix.vms.transformer.servlet.platform;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.karyon.server.eureka.HealthCheckInvocationStrategy;
import com.netflix.logging.ILog;
import com.netflix.logging.LogManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * A sample of a custom servlet. This servlet uses the shared HealthCheckCallback
 * to display good or bad depending on the status
 */
@SuppressWarnings("serial")
@Singleton  // guice requires that servlets are marked as singletons
public class HealthCheckServlet extends HttpServlet {

    private final ILog log = LogManager.getLogger(getClass());

    private final HealthCheckInvocationStrategy healthCheckInvocationStrategy;

    @Inject
    public HealthCheckServlet(HealthCheckInvocationStrategy healthCheckInvocationStrategy) {
        this.healthCheckInvocationStrategy = healthCheckInvocationStrategy;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int status;
        try {
            status = healthCheckInvocationStrategy.invokeCheck();
        } catch (TimeoutException e) {
            log.error("Healthcheck invocation timedout, returning unhealthy.", e);
            status = 500;
        }
        String  content = status == 200 || status == 203 ? "Good" : "Bad";
        response.setStatus(status);
        response.setContentLength(content.length());
        response.setContentType("text/plain");
        response.getWriter().print(content);
    }
}
