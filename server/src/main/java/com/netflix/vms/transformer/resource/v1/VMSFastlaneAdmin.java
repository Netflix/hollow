package com.netflix.vms.transformer.resource.v1;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import java.util.Date;
import com.netflix.mutationstream.fastlane.FastlaneVideo;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import com.google.inject.Inject;
import com.netflix.mutationstream.fastlane.FastlaneCassandraHelper;
import com.google.inject.Singleton;

@Singleton
@Path("/vms/fastlane")
public class VMSFastlaneAdmin {

    private final FastlaneCassandraHelper fastlaneCassandraHelper;

    @Inject
    public VMSFastlaneAdmin(FastlaneCassandraHelper fastlaneCassandraHelper) {
        this.fastlaneCassandraHelper = fastlaneCassandraHelper;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response doGet(@Context HttpServletRequest req) throws ConnectionException {

        String removeVideoId = req.getParameter("removeVideoId");
        String addVideoId = req.getParameter("addVideoId");

        if(removeVideoId != null)
            fastlaneCassandraHelper.removeVideoFromFastlane(Integer.parseInt(removeVideoId));
        if(addVideoId != null)
            fastlaneCassandraHelper.addVideoToFastlane(Integer.parseInt(addVideoId));


        List<FastlaneVideo> fastlaneVideos = fastlaneCassandraHelper.getFastlaneVideos();
        Date now = new Date();

        StringBuilder html = new StringBuilder("<html>");

        html.append("<form>");
        html.append("<input type=\"text\" name=\"addVideoId\">");
        html.append("<input type=\"submit\" value=\"Add Video\">");
        html.append("</form>");

        html.append("<p/><table border=\"1\">");
        html.append("<tr><th colspan=\"4\">Current Videos</th></tr>");
        html.append("<tr><th>Video ID</th><th>Window Start</th><th>Window End</th><th>REMOVE</th>");

        for(FastlaneVideo video : fastlaneVideos) {
            if(video.getStartWindow().before(now) && video.getEndWindow().after(now)) {
                html.append("<tr><td>").append(video.getVideoId()).append("</td><td>").append(video.getStartWindow()).append("</td><td>").append(video.getEndWindow()).append("</td>");
                html.append("<td><a href=\"?removeVideoId=").append(video.getVideoId()).append("\">REMOVE</a></td></tr>");
            }
        }

        html.append("</table>");

        html.append("<p/><table border=\"1\">");
        html.append("<tr><th colspan=\"4\">Future Videos</th></tr>");
        html.append("<tr><th>Video ID</th><th>Window Start</th><th>Window End</th><th>REMOVE</th>");

        for(FastlaneVideo video : fastlaneVideos) {
            if(video.getStartWindow().after(now)) {
                html.append("<tr><td>").append(video.getVideoId()).append("</td><td>").append(video.getStartWindow()).append("</td><td>").append(video.getEndWindow()).append("</td>");
                html.append("<td><a href=\"?removeVideoId=").append(video.getVideoId()).append("\">REMOVE</a></td></tr>");
            }
        }

        html.append("</table>");

        html.append("<p/><table border=\"1\">");
        html.append("<tr><th colspan=\"4\">Past Videos</th></tr>");
        html.append("<tr><th>Video ID</th><th>Window Start</th><th>Window End</th><th>REMOVE</th>");

        for(FastlaneVideo video : fastlaneVideos) {
            if(video.getEndWindow().before(now)) {
                html.append("<tr><td>").append(video.getVideoId()).append("</td><td>").append(video.getStartWindow()).append("</td><td>").append(video.getEndWindow()).append("</td>");
                html.append("<td><a href=\"?removeVideoId=").append(video.getVideoId()).append("\">REMOVE</a></td></tr>");
            }
        }

        html.append("</table>");

        html.append("</html>");

        return Response.ok(html.toString(), MediaType.TEXT_HTML_TYPE).build();

    }

}
