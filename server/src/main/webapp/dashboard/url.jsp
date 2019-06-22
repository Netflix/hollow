<%@ page import="java.net.HttpURLConnection"%>
<%@ page import="java.net.URL" %>

<%!
public static int getRespCode(String urlStr) throws Exception {
    if (urlStr==null) return -1; 
        
    URL url = new URL(urlStr); 
    HttpURLConnection huc =  (HttpURLConnection)  url.openConnection(); 
    huc.setRequestMethod("GET"); 
    huc.connect(); 
    return huc.getResponseCode();
}
%>
<%
   String action=request.getParameter("action");
   String url=request.getParameter("url");
   if ("status".equals(action)) {
       int status = getRespCode(url);
       if (status!=200) {
         response.sendError(status);
       } else {
         response.setStatus(status);
       }
   }
%>
<% if (action==null) { %>
    <pre>
    This JSP perform URL actions
    
    Supported Params:
    - action=status
    - url : url to perform action on
    </pre>
<% } %>
