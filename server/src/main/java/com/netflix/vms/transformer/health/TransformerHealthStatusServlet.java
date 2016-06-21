package com.netflix.vms.transformer.health;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.runtime.health.api.Health;
import com.netflix.runtime.health.api.HealthCheckAggregator;
import com.netflix.runtime.health.api.HealthCheckStatus;
import com.netflix.vms.transformer.health.TransformerServerHealthIndicator.Status;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class TransformerHealthStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 2887810521418639614L;
    private final HealthCheckAggregator healthCheckAggregator;

    @Inject
    public TransformerHealthStatusServlet(HealthCheckAggregator healthCheckAggregator) {
        this.healthCheckAggregator = healthCheckAggregator;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
        HealthCheckStatus health;
        try {
            health = this.healthCheckAggregator.check().get();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        if (health.isHealthy()) {
            List<Health> indicators = health.getHealthResults();
            for (Health indicator : indicators) {
                Map<String, Object> details = indicator.getDetails();
                if (details != null) {
                    if (Status.STARTING.name().equals(details.get(TransformerServerHealthIndicator.TRANSFORMER_STATUS_STRING))) {
                        resp.setStatus(204);
                        break;
                    }
                }
            }

            if (resp.getStatus() != 204) {
                resp.setStatus(200);
            }
        } else {
            resp.setStatus(500);
        }
        String content = health.toString();
        resp.setContentLength(content.length());
        resp.setContentType("text/plain");
        resp.getWriter().print(content);
    }

}
