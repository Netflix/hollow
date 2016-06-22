package com.netflix.vms.transformer.health;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.runtime.health.api.Health;
import com.netflix.util.Pair;
import com.netflix.vms.transformer.health.TransformerServerHealthIndicator.Status;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class TransformerHealthStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 2887810521418639614L;
    private final TransformerServerHealthIndicator healthIndicator;

    @Inject
    public TransformerHealthStatusServlet(TransformerServerHealthIndicator healthIndicator) {
        this.healthIndicator = healthIndicator;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
        Pair<Health, Status> healthStatus = healthIndicator.getHealthStatus();
        Health health = healthStatus.first();
        if (health.isHealthy()) {
            resp.setStatus(200);
        } else {
            Status status = healthStatus.second();
            if (status == Status.STARTING) {
                resp.setStatus(204);
            } else {
                resp.setStatus(500);
            }
        }

        String content = health.toString();
        resp.setContentLength(content.length());
        resp.setContentType("text/plain");
        resp.getWriter().print(content);
    }

}
