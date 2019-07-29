package com.netflix.vms.transformer.rest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.vms.transformer.common.TransformCycleInterrupter;
import java.util.Date;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.time.FastDateFormat;

@Singleton
@Path("/vms/interruptcycle")
public class VMSCycleInterrupter {
    private final static TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
    private final static FastDateFormat formatter = FastDateFormat.getInstance("dd-MMM-yyyy HH:mm:ss z", tz);

    private final TransformCycleInterrupter cycleInterrupter;

    @Inject
    public VMSCycleInterrupter(TransformCycleInterrupter cycleInterrupter) {
        this.cycleInterrupter = cycleInterrupter;
    }

    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    public Response doGet(@Context HttpServletRequest req,
            @QueryParam("interrupt") Boolean interrupt,
            @QueryParam("pause") Boolean pause,
            @QueryParam("message") String msg) throws Exception, Throwable {

        String response = "";
        if (pause != null) {
            cycleInterrupter.pauseCycle(pause);
            response = "Next cycle will be paused: " + pause + "; ";
        }

        if (interrupt != null && interrupt == true) {
            if (msg == null || msg.trim().isEmpty()) throw new IllegalArgumentException("message is required");

            if (pause != null) msg += "; Pause Next Cycle:" + pause;
            cycleInterrupter.interruptCycle(msg);

            String reqTime = formatter.format(new Date());
            response += "Cycle Interrupt request received at " + reqTime + " with message=" + msg;
        } else if (pause == null) {
            response = "USAGE: interrupt=true&pause=true&message=[REASON FOR CYCLE INTERRUPT]";
        }

        if (!cycleInterrupter.getHistory().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(response);
            sb.append("\n\n");
            sb.append(cycleInterrupter.getHistoryAsString());
            response = sb.toString();
        }

        return Response.ok(response,  MediaType.TEXT_PLAIN_TYPE).build();
    }
}