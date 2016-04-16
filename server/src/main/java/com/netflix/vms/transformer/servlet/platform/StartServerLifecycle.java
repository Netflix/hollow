package com.netflix.vms.transformer.servlet.platform;

import com.netflix.cassandra.NFAstyanaxManager;

import com.google.inject.servlet.ServletModule;
import com.netflix.aws.file.FileStore;
import com.netflix.governator.annotations.AutoBindSingleton;
import com.netflix.karyon.spi.HealthCheckHandler;
import com.netflix.server.base.NFFilter;
import com.netflix.server.base.lifecycle.BaseServerLifecycleListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContextEvent;


@AutoBindSingleton
public class StartServerLifecycle extends BaseServerLifecycleListener {

    public StartServerLifecycle() {
        super("vmstransformer", "vmstransformer", "1.0");
    }

    @Override
    protected void initialize(ServletContextEvent event) throws Exception {
        PlatformLibraries.FILE_STORE = getInjector().getInstance(FileStore.class);
        PlatformLibraries.ASTYANAX = getInjector().getInstance(NFAstyanaxManager.class);
    }

    @Override
    protected ServletModule getServletModule() {
        return new JerseyServletModule() {
            @Override
            protected void configureServlets() {
                // initialize NFFilter
                Map<String, String> initParams = new HashMap<String, String>();
                initParams.put("requestId.accept", "true");
                initParams.put("requestId.require", "true");
                filter("/*").through(NFFilter.class, initParams);

                // This sets up Jersey to scan the "com.netflix" and "com.sun.jersey" packages
                // and serve any found resources from the base path of "/REST/"
                serve("/REST/*").with(GuiceContainer.class, createStandardServeParams());

                // fix bug in Jersey-Guice integration exposed by child injectors
                binder().bind(GuiceContainer.class).asEagerSingleton();

                serve("/Status").with(StatusPage.class);
                serve("/healthcheck").with(HealthCheckServlet.class);

                // fix bug in Jersey-Guice integration exposed by child injectors
                binder().bind(GuiceContainer.class).asEagerSingleton();
            }
        };
    }


    @Override
    protected Class<? extends HealthCheckHandler> getHealthCheckHandler() {
        return HealthCheckHandlerImpl.class;
    }

}

