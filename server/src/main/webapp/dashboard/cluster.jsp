<%@ page import="com.netflix.videometadata.config.DataIOManager"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.net.HttpURLConnection"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="org.codehaus.jackson.JsonNode"%>
<%@ page import="org.codehaus.jackson.map.ObjectMapper" %>
<%@ page import="org.codehaus.jackson.node.ArrayNode" %>


<%!
public static String readString(InputStream is) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    StringBuilder sb = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
            sb.append(line);
    }
    return sb.toString();
}

public static String getString(JsonNode node, String name) {
    final JsonNode dataNode = node.get(name);
    return dataNode==null ? null : dataNode.getTextValue();
}

public static String getCLUSTER(String env, String region, String vip) throws Exception {
    String urlStr = String.format("http://asgard%s.netflix.com/%s/cluster/list.json", env, region);
    URL url = new URL(urlStr); 
    HttpURLConnection huc =  (HttpURLConnection)  url.openConnection(); 
    huc.setRequestMethod("GET");
    try {
	    huc.connect(); 
	    int respCode = huc.getResponseCode();
	    if (200 <= respCode && respCode <= 299) {
	        String jsonStr = readString(huc.getInputStream());
	        final ArrayNode jsonNode = new ObjectMapper().readValue(jsonStr, ArrayNode.class);
	
	        final Iterator<JsonNode> mainIT = jsonNode.getElements();
	        while (mainIT.hasNext()) {
	            final JsonNode iNode = mainIT.next();
	            String cluster = getString(iNode, "cluster");
	            if (!cluster.startsWith("videometadata")) continue;
	
	            String[] parts = cluster.split("-");
	            if (parts==null || parts.length < 2) continue;
	            String currVip = parts[1];
	            if (vip.equals(currVip))  {
	                return cluster;
	            }
	        }
	    }
    } catch(Exception ex) {
        System.err.println("Failed to connetion to: " + urlStr);
        ex.printStackTrace();
    }
    return String.format("videometadata-%s-%s-cluster-%s", vip, env, DataIOManager.get().getDataNameSpace());
}
%>
<%
   String env=request.getParameter("env");
   String reg=request.getParameter("reg");
   String vip=request.getParameter("vip");
   if (env==null || reg==null || vip==null) { %>
      <pre>
      Finds CLUSTER for specifiec Environment, Region and VIP

      SUPPORTED params:
          - env : test/prod
          - reg : e.g. us-east-1
          - vip : iceland
     </pre>
<% } else { 
        String cluster = getCLUSTER(env, reg, vip);
        if (cluster==null) {
            response.sendError(500);
        } else {
            %><%=cluster%><%
        }
   } 
%>