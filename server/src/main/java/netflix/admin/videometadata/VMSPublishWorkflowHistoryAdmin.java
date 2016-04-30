package netflix.admin.videometadata;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.vms.transformer.common.PublicationHistory;
import com.netflix.vms.transformer.common.PublicationJob;

@Path("/vms/workflowhistory")
@Singleton
public class VMSPublishWorkflowHistoryAdmin {

    public static PublicationHistory history;

    @Inject
    public VMSPublishWorkflowHistoryAdmin() {
    }

    @GET
    @Produces({"text/html"})
    public Response showStatus(@QueryParam("format") String format,
                               @QueryParam("sort") String sort,
                               @QueryParam("limit") String limit,
                               @Context HttpServletRequest req) {

        if(StringUtils.isEmpty(sort)) {
            sort = "cycleversion";
        }

        int maxResults = 1000;
        if(!StringUtils.isEmpty(limit))
            maxResults = Integer.parseInt(limit);



        if("json".equals(format))
            return jsonResponse(sort, maxResults);

        return htmlResponse(sort, maxResults);
    }

    public Response htmlResponse(String sort, int limit) {
        final StringBuilder html = new StringBuilder("<html><body><table border=\"1\">");

        html.append("<tr>");
        html.append("<th><b>JOB NAME</b></th>");
        html.append("<th><b><a href=\"workflowhistory?sort=cycleversion&limit=" + limit + "\">CYCLE VERSION</a></b></th>");
        html.append("<th><b>STATUS</b></th>");
        html.append("<th><b><a href=\"workflowhistory?sort=starttime&limit=" + limit + "\">START TIME</a></b></th>");
        html.append("<th><b><a href=\"workflowhistory?sort=endtime&limit=" + limit + "\">END TIME</a></b></th>");
        html.append("</tr>");

        List<PublicationJob> jobs;

        if("starttime".equals(sort))
            jobs = history.getJobsSortedByStartTimestamp();
        else if("endtime".equals(sort))
            jobs = history.getJobsSortedByCompletedOrFailedTimestamp();
        else
            jobs = history.getJobsSortedByCycleVersion();

        Collections.reverse(jobs);

        if(jobs.size() > limit)
            jobs = jobs.subList(0, limit);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));


        for(PublicationJob job : jobs) {
            html.append("<tr>");
            html.append("<td>").append(job.getJobName()).append("</td>");
            html.append("<td>").append(job.getCycleVersion()).append("</td>");
            html.append("<td>").append(job.getDisplayableStatus()).append("</td>");
            html.append("<td>").append(formatDate(dateFormat, job.getActualStartTimestamp())).append("</td>");
            html.append("<td>").append(formatDate(dateFormat, job.getFinishedTimestamp())).append("</td>");
            html.append("</tr>");
        }

        html.append("</table></body></html>");

        return Response.ok(html.toString(), MediaType.TEXT_HTML_TYPE).build();
    }

    private String formatDate(SimpleDateFormat format, long timestamp) {
        if(timestamp == PublicationJob.NOT_YET)
            return "N/A";
        return format.format(new Date(timestamp));
    }

    public Response jsonResponse(String sort, int maxResults) {
        throw new UnsupportedOperationException("JSON not yet implemented");
    }
}
