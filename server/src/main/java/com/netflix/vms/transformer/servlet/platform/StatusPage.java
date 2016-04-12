package com.netflix.vms.transformer.servlet.platform;

import com.google.inject.Singleton;
import com.netflix.server.base.BaseStatusPage;

import java.io.PrintWriter;

@Singleton  // guice requires servlets to be singletons
public class StatusPage extends BaseStatusPage {

    private static final long serialVersionUID = 1L;

    protected void getDetails(PrintWriter out, boolean htmlize) {
        super.getDetails(out, htmlize);
        // Add any extra status info here
    }

}
