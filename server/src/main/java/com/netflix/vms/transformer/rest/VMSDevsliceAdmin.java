package com.netflix.vms.transformer.rest;

import static com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper.TransformerColumnFamily.DEV_SLICE_TOPNODE_IDS;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/vms/devslice")
public class VMSDevsliceAdmin {
    
    private final TransformerCassandraColumnFamilyHelper cassandraHelper;
    
    @Inject
    public VMSDevsliceAdmin(TransformerCassandraHelper cassandraHelper) {
        this.cassandraHelper = cassandraHelper.getColumnFamilyHelper(DEV_SLICE_TOPNODE_IDS);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response doGet(@Context HttpServletRequest req) throws ConnectionException {

        String removeVideoId = req.getParameter("removeVideoId");
        String addVideoId = req.getParameter("addVideoId");

        if(removeVideoId != null)
            removeVideo(Integer.parseInt(removeVideoId));
        if(addVideoId != null)
            addVideo(Integer.parseInt(addVideoId));


        Map<String, String> columns = cassandraHelper.getColumns("ids_0");

        StringBuilder html = new StringBuilder("<html>");
        
        html.append("<head><title>VMS DevSlice Admin</title></head>");
        html.append("<body>");
        
        html.append("<p>");
        html.append("<b>Instructions:</b><br/>");
        html.append("Add the Video ID of a show or standalone movie here, and it will be available (along with all associated supplementals, seasons, and episodes) in the devslice data within 15 minutes.");
        html.append("<br/>See the \"Dev Slice\" section under <a href=\"http://go/vmsconfig\">http://go/vmsconfig</a> for instructions on how to initialize VMS devslice mode.");
        html.append("</p><p>");
        html.append("<b>Devslice Etiquette:</b>");
        html.append("<ul>");
        html.append("<li>Please don't add a huge number of IDs.</li>");
        html.append("<li>Please only remove IDs which you have added <i>recently</i>.  If IDs have been here for a long time, it's possible other VMS partners may be expecting their presence in the devslice data.</li>");
        html.append("</ul>");
        html.append("</p>");
        
        html.append("<form>");
        html.append("<input type=\"text\" name=\"addVideoId\">");
        html.append("<input type=\"submit\" value=\"Add Video\">");
        html.append("</form>");

        html.append("<p/><table border=\"1\">");
        html.append("<tr><th colspan=\"4\">TOP NODE IDS to include in VMS DevSlice</th></tr>");
        html.append("<tr><th>Top Node ID</th><th>REMOVE</th>");

        for(Map.Entry<String, String> entry : columns.entrySet()) {
            html.append("<tr><td>").append(entry.getKey()).append("</td>");
            html.append("<td><a href=\"?removeVideoId=").append(entry.getKey()).append("\">REMOVE</a></td></tr>");
        }

        html.append("</table>");

        html.append("</body>");
        html.append("</html>");

        return Response.ok(html.toString(), MediaType.TEXT_HTML_TYPE).build();

    }
    
    private void removeVideo(long videoId) throws ConnectionException {
        cassandraHelper.deleteKeyColumnValue("ids_0", String.valueOf(videoId));
    }
    
    private void addVideo(long videoId) throws ConnectionException {
        cassandraHelper.addKeyColumnValue("ids_0", String.valueOf(videoId), String.valueOf(videoId));
    }

}
