<%@ page import="com.google.gson.Gson"%>
<%@ page import="com.netflix.config.NetflixConfiguration"%>
<%@ page import="com.netflix.videometadata.config.*"%>
<%@ page import="com.netflix.videometadata.config.DataIOManager.PrimaryVipInfo"%>
<%@ page import="com.netflix.videometadata.s3.*"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.TreeMap" %>

<%
   DataIOManager ioMgr = DataIOManager.get();

   String nameSpace=request.getParameter("nameSpace");
   if (nameSpace==null) nameSpace = ioMgr.getZKNameSpace();

   PrimaryVipInfo pInfo = ioMgr.readPrimaryVipInfoFromNameSpace(nameSpace);
   String primaryVip = pInfo!=null ? pInfo.getPrimaryVip() : ioMgr.getPrimaryVip();

   boolean isJsonFormat = "json".equals(request.getParameter("format"));
   boolean isVerboseVipData = isJsonFormat;
   boolean isPrimaryVipDataOnly = !"true".equals(request.getParameter("showAllCycleData")) && !isVerboseVipData;

   // dataCycle -> Map<vip, data>
   List<String> vips = new ArrayList<String>();
   TreeMap<String, Map<String, Map<String, String>>> dataCycleMap = createDataMap(ioMgr, nameSpace, primaryVip, vips, isVerboseVipData, isPrimaryVipDataOnly);

   if (isJsonFormat) {
       response.setContentType("application/json");
       String json = toJson(nameSpace, pInfo, dataCycleMap);
       out.print(json);
       out.flush();
       return;
   }
%>

<html>
    <head>
        <title>Data IO</title>
        <style>
            .data {
                border:1px solid #d9d9d9;
            }
        </style>
    </head>
<body>
<table  cellpadding=2 cellspacing=2 border=0>
  <tr>
    <th>NameSpace:</th><td><%=nameSpace%></td>
    <td colspan=2 width=50px>&nbsp;</td>
    <th>PrimaryVipInfo:</th><td><%=pInfo%></td>
  </tr>
</table><hr>
<form action="">
    <%String checkedStr=isPrimaryVipDataOnly?"":"checked";%>
    <b>Show all data:</b> <input type="checkbox" name="showAllCycleData" value="true" <%=checkedStr%> onclick="this.form.submit()">
</form>
<br>

<% if (!dataCycleMap.isEmpty())  { %>
    <table class="data" border=1 cellpadding=8 cellspacing=0>
        <tr>
            <th align=center>#</th><th>publishCycleDataTS_dataVersionId</th>
            <% for(String vip : vips) {
                boolean isPrimaryVip = vip.equals(primaryVip);
            %>
                <th colspan=2><%=isPrimaryVip ? vip + " (Primary Vip)" : vip%></th>
            <% } %>
        </tr>
        
        <% int index=dataCycleMap.size();%>
        <% for(String sourceDataVersion : dataCycleMap.descendingKeySet()) {
            Map<String, Map<String, String>> vipMap = dataCycleMap.get(sourceDataVersion);
            String primaryCycleId = null;
        %>
            <tr>
                <td align=right><%=index--%></td><td><%=sourceDataVersion%></td>
                <% for(String vip : vips) {
                    Map<String, String> vipData = vipMap.get(vip);
                    String cycleId = getPubInfo(vipData, CYCLE_ID);

                    boolean isPrimaryVip = vip.equals(primaryVip);
                    if (isPrimaryVip) primaryCycleId = cycleId;

                    String diffURL = isPrimaryVip ? NO_DIFF : createDiffLink(sourceDataVersion, primaryVip, primaryCycleId, vip, cycleId);
                %>
                    <td><%=vipData==null?"":vipData%></td>
                    <td nowrap align=center><%=diffURL%></td>
                <% } %>
            </tr>
        <% } %>
    </table>
    
<% } %>

</body>
</html>

<%!
public static final String CYCLE_ID = "cycleID";
public static final String NO_DIFF = " - ";
public static final Set<String> VERBOSE_ATTRIB_SET = new HashSet<String>(Arrays.asList(BlobMetaData.sourceDataVersion.name(), BlobMetaData.publishCycleDataTS.name()));

public String getPubInfo(Map<String, String> pubInfoEntryMap, String fieldName) {
    if (pubInfoEntryMap==null) return "";

    return pubInfoEntryMap.get(fieldName);
}

public String toJson(String nameSpace, PrimaryVipInfo pInfo, TreeMap<String, Map<String, Map<String, String>>> dataCycleMap) {
    Map<String, Object> jsonMap = new HashMap<String, Object>();
    jsonMap.put("nameSpace", nameSpace);
    jsonMap.put("primaryVip", pInfo.getPrimaryVip());
    jsonMap.put("cycles", dataCycleMap);

    Gson gson = new Gson();
    return gson.toJson(jsonMap);
}

// dataCycleID -> Map<Vip->attribsMap>
public TreeMap<String, Map<String, Map<String, String>>> createDataMap(DataIOManager ioMgr, String nameSpace, String primaryVip, List<String> vips, boolean isVerboseVipData, boolean isPrimaryVipDataOnly) throws Exception {
    TreeMap<String, Map<String, Map<String, String>>> dataCycleMap = new TreeMap<String, Map<String, Map<String, String>>>();

    Set<String> primaryVipKeys = null;
    Set<String> processedVips = new HashSet<String>();
    for(String vip : ioMgr.getVipsOnNameSpace(nameSpace)) {
        boolean isPrimaryVip = primaryVip.equals(vip); 
        if (!isPrimaryVip) {
            processedVips.add(vip);
        }

        // cycleID -> Map<propName, propValue>
        Map<String, Map<String, String>> infoMap = ioMgr.getPubInfo(nameSpace, vip);
        if (infoMap==null) continue;

        Set<String> keys = aggregateBasedOnDataCycleInfo(vip, infoMap, dataCycleMap, isVerboseVipData);
        if (isPrimaryVip) {
            primaryVipKeys = keys;
        }
    }
    
    if (isPrimaryVipDataOnly) {
        // Filterout data that do not match primary vip cycle
        processedVips.clear();
        TreeMap<String, Map<String, Map<String, String>>> primaryDataCycleMap = new TreeMap<String, Map<String, Map<String, String>>>();
        for(String cycleKey: primaryVipKeys) {
            Map<String, Map<String, String>> cycleData = dataCycleMap.get(cycleKey);
            primaryDataCycleMap.put(cycleKey, cycleData);
            processedVips.addAll(cycleData.keySet());
        }
        dataCycleMap=primaryDataCycleMap;
    }

    // Keep primaryVip first and sort the reset
    processedVips.remove(primaryVip);
    vips.addAll(processedVips);
    Collections.sort(vips);
    vips.add(0, primaryVip);

    return dataCycleMap;

}

/**
 * Aggregate data based on sourceDataVersion and publishCycleDataTS
 *
 * @param infoMap - INPUT: cycleID -> Map<propName, propValue>
 * @param dataCycleMap - OUTPUT: TreeMap<String, Map<String, Map<String, String>>>
 * @param isVerboseVipData - indicate whether to use verbose vip data 
 * @return keys for the current infoMap
 */
public Set<String> aggregateBasedOnDataCycleInfo(String vip, Map<String, Map<String, String>> infoMap, TreeMap<String, Map<String, Map<String, String>>> dataCycleMap, boolean isVerboseVipData) {
    Set<String> keys = new HashSet<String>();
    for(Map.Entry<String, Map<String, String>> entry : infoMap.entrySet()) {
        Map<String, String> rawDataMap = entry.getValue();
        String cycleID = entry.getKey();

        String publishCycleDataTS = rawDataMap.get(BlobMetaData.publishCycleDataTS.name());
        String sourceDataVersion = rawDataMap.get(BlobMetaData.sourceDataVersion.name());
        String key = publishCycleDataTS + "_" + sourceDataVersion; 
        keys.add(key);

        Map<String, Map<String, String>> vipMap = dataCycleMap.get(key);
        if (vipMap==null) {
            vipMap = new HashMap<String, Map<String, String>>();
            dataCycleMap.put(key, vipMap);
        }

        // Create Cycle Attribute per Vip
        Map<String, String> cyleAttribMap = new HashMap<String, String>();
        cyleAttribMap.put(CYCLE_ID, cycleID);
        for(Map.Entry<String, String> rawDataEntry: rawDataMap.entrySet()) {
            String attribFN = rawDataEntry.getKey();
            if (!isVerboseVipData && VERBOSE_ATTRIB_SET.contains(attribFN)) continue;

            cyleAttribMap.put(attribFN, rawDataEntry.getValue());
        }
        vipMap.put(vip, cyleAttribMap);
    }

    return keys;
}

public String createDiffURL(String sourceDataVersion, String primaryVip, String primaryCycleId, String secondaryVip, String secondaryCycleId) {
    if (primaryCycleId==null || primaryCycleId.trim().isEmpty() || secondaryCycleId==null || secondaryCycleId.trim().isEmpty()) return null;

    String env = NetflixConfiguration.getEnvironment();
    String diffName = String.format("dataio:vip(%s)_sourceDataVersion(%s)", secondaryVip, sourceDataVersion);
    String url = String.format("http://go/vmsdiff-%s?action=submit&diffName=%s&fromVip=%s&fromVersion=%s&toVip=%s&toVersion=%s", 
            env, diffName, primaryVip, primaryCycleId, secondaryVip, secondaryCycleId);

    return url;
}

public String createDiffLink(String sourceDataVersion, String primaryVip, String primaryCycleId, String secondaryVip, String secondaryCycleId) {
    String url = createDiffURL(sourceDataVersion, primaryVip, primaryCycleId, secondaryVip, secondaryCycleId);
    if (url==null) return NO_DIFF;

    return String.format("[ <a href=\"%s\" target=\"_blank\">diff</a> ]", url);
}
%>