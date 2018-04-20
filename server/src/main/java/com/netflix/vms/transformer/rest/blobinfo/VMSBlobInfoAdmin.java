package com.netflix.vms.transformer.rest.blobinfo;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.aws.db.Item;
import com.netflix.aws.db.ItemAttribute;
import com.netflix.aws.file.FileStore;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.fastproperties.ClientPinningUtil;
import com.netflix.vms.transformer.publish.workflow.util.TransformerServerCassandraHelper;
import com.netflix.vms.transformer.rest.blobinfo.BlobImageEntry.AttributeKeys;
import com.netflix.vms.transformer.rest.blobinfo.BlobImageEntry.BlobType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
@Singleton
@Path("/vms/blobinfo")
public class VMSBlobInfoAdmin  {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VMSBlobInfoAdmin.class);

    private static final String COLOR_SAFE_TO_PIN = "#A9F5D0";
    private static final String COLOR_BROKEN_CHAIN = "#F5A9A9 ";
    private final Map<RegionEnum, Set<String>> pinableVersionSets = new HashMap<>();
    public static final RegionEnum[] regions = {RegionEnum.US_EAST_1, RegionEnum.US_WEST_2, RegionEnum.EU_WEST_1};
    
    private final static TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
    private final static FastDateFormat dateFormat = FastDateFormat.getInstance("dd-MMM-yyyy HH:mm:ss z", tz);
    
    private final TransformerConfig transformerConfig;
    private final FileStore fileStore;
    private final TransformerServerCassandraHelper cassandraHelper;
    private final TransformerCassandraColumnFamilyHelper poisonedCassandraHelper;
    
    @Inject
    public VMSBlobInfoAdmin(TransformerConfig config, 
    		FileStore fileStore, 
    		TransformerServerCassandraHelper cassandra) {
        this.transformerConfig = config;
        this.fileStore = fileStore;
        this.cassandraHelper = cassandra;
		this.poisonedCassandraHelper = cassandraHelper.getColumnFamilyHelper(TransformerCassandraHelper.TransformerColumnFamily.POISON_STATES);
    }

    @GET
    @Produces({ "text/plain", "text/html" })
    public String adminOutput(
            @QueryParam("vip") String vip,
            @QueryParam("details") boolean details,
            @QueryParam("format") final String format
            ) throws Exception {
    	
        if (StringUtils.isBlank(vip)) vip = transformerConfig.getTransformerVip();
        
        BlobImageEntryLoader loader = new BlobImageEntryLoader(vip, fileStore);

        SortedMap<String, BlobImageEntry> map = loader.fetchEntryMap();
        Set<String> brokenChains = detectBrokenChain(map);

        boolean isJson = "json".equals(format);

        Map<RegionEnum, String> pinnedVersions = new HashMap<RegionEnum, String>();

        // Build the pinned version map, which keep track of which regions are pinned to which region
        // null value means that region is not pinned
        for(RegionEnum region : regions) {
            pinnedVersions.put(region, ClientPinningUtil.getPinnedVersion(vip, region));
        }

        // Build the announced version map
        Map<RegionEnum, String> announcedVersions = ClientPinningUtil.getAnnouncedVersions(vip);
        if(announcedVersions == null) {
        	announcedVersions = new Hashtable<RegionEnum, String>();
            for(RegionEnum region : regions) {
            	announcedVersions.put(region, (new Long(Long.MAX_VALUE)).toString());
            }        	
        }

        final StringBuilder sb = new StringBuilder();
        if (!isJson) {
            sb.append("</br>vip=").append(vip);
            sb.append("</br>broken_chain_count=").append(brokenChains.size());
            sb.append("</br></br><table cellpadding=1 cellspacing=1>");
            sb.append("<tr><td><b>COLOR CODE INDEX:</b>");
            sb.append("</br> - <span style=\"background-color: ").append(COLOR_SAFE_TO_PIN).append("\">GREEN ROW</span> = Version is safe to Pin");
            sb.append("</br> - <span style=\"background-color: ").append(COLOR_BROKEN_CHAIN).append("\">RED CHAIN</span> = Broken chain detected - missing DELTA/RESERVEDELTA");
            sb.append("</td><td>&nbsp;</td><td><b>ACTIONS:</b>");
            sb.append("</br> - <a href=").append(String.format("?vip=%s&details=%s", vip, true)).append(">[Show Details]</a>");
            sb.append("</br> - <a href=").append(String.format("?vip=%s&details=%s", vip, false)).append(">[Hide Details]</a>");
            sb.append("</td></tr></table></br>");
            sb.append("<br/>");
            // Printing the pinning status for this VIP
            sb.append("<h2>Pinning Status for VIP: " + vip + "</h2>");
            sb.append("<table border='1'>");
            sb.append("<tr><th>Region</th><th>Version</th></tr>");
            for(RegionEnum region : regions) {
                sb.append("<tr>");
                sb.append("<th>" + region.toString() + "</th>");
                sb.append("<td style='text-align: center'>");
                if(Strings.isNullOrEmpty(pinnedVersions.get(region))) {
                    sb.append("---");
                } else {
                    sb.append(pinnedVersions.get(region));
                }
                sb.append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
            sb.append("<br/>");
        }

        String dataOutput = null;
        if("json".equals(format)) {
            dataOutput = formatJson(map, vip, pinnedVersions, announcedVersions);
        } else {
            dataOutput = formatHtml(loader, map, brokenChains, details, vip, pinnedVersions);
        }
        sb.append(dataOutput);

        return sb.toString();
    }

    private Set<String> detectBrokenChain(SortedMap<String, BlobImageEntry> map) {
        Set<String> resultSet = new HashSet<String>();

        for (String version : map.keySet()) {
            BlobImageEntry data = map.get(version);

            // Detech if it is missing Delta/Reverse Detal
            if (data.isBrokenChain()) {
                resultSet.add(data.getVersion());
            }

            // make sure prior version exists
            String priorVersion = data.getPriorVersion();
            if (priorVersion != null && !map.containsKey(priorVersion)) {
                resultSet.add(data.getVersion());
            }
        }

        return resultSet;
    }

    private boolean isBrokenChainVersion(BlobImageEntry data, SortedMap<String, BlobImageEntry> map) {
        if (data.isBrokenChain()) {
            return true;
        }

        // make sure prior version exists
        String priorVersion = data.getPriorVersion();
        if (priorVersion != null && !map.containsKey(priorVersion)) {
            return true;
        }

        return false;
    }

    private Map<String, Object> createItemsAttribMap(Item item) {
    	Map<String, Object> attrMap = new TreeMap<String, Object>();

    	for(ItemAttribute ia : item.getAttributes()) {
    		String name = ia.getName();
    		String value = ia.getValue();
    		attrMap.put(name, value);
    	}

    	return attrMap;
    }

    private String formatJson(final SortedMap<String, BlobImageEntry> map, String vip,
    		Map<RegionEnum, String> pinnedVersions, Map<RegionEnum, String> announcedVersions) {
    	for(RegionEnum region : regions) {
    		pinableVersionSets.put(region, new HashSet<String>());
    	}
    	
    	// Map which will be converted to JSON
    	Map<String, Object> output = new LinkedHashMap<String, Object>();

    	// Meta object which holds the information about the VIP etc
    	Map<String, String> meta = new LinkedHashMap<String, String>();
    	meta.put("vip", vip);

    	// pins map holds which versions are pinned
    	Map<String, String> pins = new LinkedHashMap<String, String>();
    	for(RegionEnum region : pinnedVersions.keySet()) {
    		pins.put(region.toString(), pinnedVersions.get(region));
    	}

    	// Where are the blobs published
    	Map<String, String> announcements = new LinkedHashMap<String, String>();
    	for(RegionEnum region : announcedVersions.keySet()) {
    		announcements.put(region.toString(), announcedVersions.get(region));
    	}
    	

    	List<Map<String, Object>> blobList = new ArrayList<Map<String,Object>>();
    	for(BlobImageEntry entry : map.values()) {
    		Map<String, Object> mapObj = new LinkedHashMap<String, Object>();
    		// Poisoned state placeholder
    		mapObj.put("version", entry.getVersion());
    		mapObj.put("pinable", isPinableVersion(entry));
    		
    		if(isPinableVersion(entry)) {
    			String isPoisoned = "false";
				try {
					String poisonedStateQueryKey = entry.getVersion() + "_" + vip;
					Map<String, String> columns = poisonedCassandraHelper.getColumns(poisonedStateQueryKey);
					// If there are no columns, it means that the blob is not poisoned
					if(columns.size() == 0) 
						isPoisoned = "false";
					else {
						// Get the value of the poisoned column
						isPoisoned = poisonedCassandraHelper.getKeyColumnValue(poisonedStateQueryKey, "val");
						// In case of an exception, the isPoisoned value remains false
					}
				} catch (ConnectionException e) {
					e.printStackTrace();
				} finally {
					mapObj.put("poisoned", Boolean.parseBoolean(isPoisoned));
				}
    		}
    		
    		Map<String, String> entryAttribs = entry.getAttribs();
    		Long ts = entry.getPublishedTimeStamp();
    		if(ts != null) {
    			entryAttribs.put("publishedDate", dateFormat.format(ts));
    		}
    		mapObj.put("attribs", entryAttribs);


    		Map<String, Object> typeMap = new LinkedHashMap<String, Object>();
    		mapObj.put("types", typeMap);

    		for(BlobType type: BlobType.values()) {
    			Item item = entry.getItemMap().get(type);
    			if(item == null) {
    				typeMap.put(type.name(), "n/a");
    			} else {
    				typeMap.put(type.name(), createItemsAttribMap(item));
    			}
    		}
    		blobList.add(mapObj);
    	}

    	// Add the items to the output
    	output.put("meta", meta);
    	output.put("pins", pins);
    	output.put("publish", announcements);
    	output.put("blobs", blobList);

        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
            return mapper.writeValueAsString(output);
        } catch (final IOException e) {
            LOGGER.error("Failed to generate Json", e);
            return e.getMessage();
        }
    }
    
    

    private boolean isPinableVersion(final BlobImageEntry entry) {
        return isPinableVersion(NetflixConfiguration.getRegionEnum(), entry);
    }

    private boolean isPinableVersion(RegionEnum region, final BlobImageEntry entry) {
        final String jarVersion = entry.getJarVersion();
        Set<String> pinableVersionSet = pinableVersionSets.get(region);
        if (pinableVersionSet.isEmpty()) {
            // First version (latest Version) is always pin-able regardless of when its last publish was (in case Server was stopped publishing)
            pinableVersionSet.add(jarVersion);
        } else {
            // Check if jar version was last published within configurable threshold
            final Long ts = entry.getPublishedTimeStamp();
            if (ts != null && (System.currentTimeMillis() - ts < (12*60*60*1000))) {
                pinableVersionSet.add(jarVersion);
            }
        }

        return pinableVersionSet.contains(jarVersion);
    }

    private String formatHtml(BlobImageEntryLoader loader, final SortedMap<String, BlobImageEntry> map, Set<String> brokenChains,
            boolean details, String vip, Map<RegionEnum, String> pinnedVersions) throws Exception {
        final StringBuilder sb = new StringBuilder();
        int i = 0;

        sb.append("<table border=1 cellpadding=2 cellspacing=0>");
        sb.append("<tr><th width=1%>#</th><th>pubTimeStamp</th><th>Version</th>");
        for(RegionEnum region : regions) {
            sb.append("<th width='100'>Pin<br/>" + region.toString() + "</th>");
        }
        sb.append("<th width='100'>Pin<br/>All</th>");
        sb.append("<th>priorVersion</th><th>chain</th><th>releaseJar</th><th>pubServer</th>");
        if (details) sb.append("<th>Details</th>");
        sb.append("</tr>");


        Map<RegionEnum, SortedMap<String, BlobImageEntry>> versionsByRegionsMap = new HashMap<>();
        RegionEnum regionWithMostVersions = null;
        int count = 0;
        for(RegionEnum region : regions) {
            SortedMap<String, BlobImageEntry> availableVersions = loader.fetchEntryMap(region);
            versionsByRegionsMap.put(region, availableVersions);
            if(availableVersions.size() > count) {
                regionWithMostVersions = region;
                count = availableVersions.size();
            }
            pinableVersionSets.put(region, new HashSet<String>());
        }

        for (final Map.Entry<String, BlobImageEntry> entry : versionsByRegionsMap.get(regionWithMostVersions).entrySet()) {
            final BlobImageEntry data = entry.getValue();
            String curVersion = data.getVersion();
            final Map<String, String> attribs = data.getAttribs();

            sb.append("<tr ");
            if (isPinableVersion(data)) sb.append(" bgcolor=").append(COLOR_SAFE_TO_PIN);
            sb.append(" nowrap>");

            sb.append("<td align=right>").append(++i).append("</td>");
            sb.append("<td nowrap>").append(dateFormat.format(data.getPublishedTimeStamp())).append("</td>");
            sb.append("<td>").append(curVersion).append("</td>");

            boolean isPinnableInAllRegions = true;
            boolean isBrokenInOneOrMoreRegions = false;
            for(RegionEnum region : regions) {
                SortedMap<String, BlobImageEntry> availableVersions = versionsByRegionsMap.get(region);
                boolean brokenChainVersion = isBrokenChainVersion(data, availableVersions);
                if(brokenChainVersion) {
                    isBrokenInOneOrMoreRegions = true;
                }
                if(isPinableVersion(availableVersions, region, data)) {
                    // Print per region (un)pin links
                    sb.append(getPerRegionPinUnpinLink(vip, curVersion, region, pinnedVersions, brokenChainVersion));
                }else {
                    isPinnableInAllRegions = false;
                    sb.append("<td>").append("---").append("</td>");
                }
            }

            if(isPinnableInAllRegions) {
                // Print the (un)pin link for all region
                sb.append(getAllRegionPinUnpinLink(vip, curVersion, pinnedVersions));
            }else {
                sb.append("<td>").append("---").append("</td>");
            }

            sb.append("<td>").append(data.getPriorVersion()).append("</td>");

            sb.append("<td ");
            if (isBrokenInOneOrMoreRegions) sb.append(" bgcolor=").append(COLOR_BROKEN_CHAIN);
            sb.append(">").append(isBrokenInOneOrMoreRegions ? "BROKEN" : "OK").append("</td>");

            sb.append("<td>").append(attribs.get(AttributeKeys.ProducedByJarVersion.name())).append("</td>");
            sb.append("<td>").append(attribs.get(AttributeKeys.ProducedByServer.name())).append("</td>");
            if (details) sb.append("<td><pre>").append(data.toJson(true)).append("</pre></td>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        return sb.toString();
    }

    private boolean isPinableVersion(SortedMap<String, BlobImageEntry> sortedMap, RegionEnum region, BlobImageEntry data) {
        return sortedMap.containsKey(data.getVersion()) && isPinableVersion(region, data);
    }

    private String getAllRegionPinUnpinLink(String vip, String version, Map<RegionEnum, String> pinnedVersions) {
        String pinLinkFormat = "<td><a href='/REST/vms/pinclients?vip=%s&version=%s&region=all'>Pin all</a></td>";
        String unpinLinkFormat = "<td bgcolor='#F4FA58'><a href='/REST/vms/unpinclients?vip=%s&region=all'>Unpin all</a></td>";

        // Check the pinned version map, if one of the region is not pinned, we provide the link to pin the clients in all regions
        boolean allRegionsPinned = true;
        for(RegionEnum region : regions) {
            if(Strings.isNullOrEmpty(pinnedVersions.get(region))) {
                allRegionsPinned = false;
            }
        }

        if(!allRegionsPinned) {
            // All regions are not pinned, by default we will provide a link to pin all the regions per version
            return String.format(pinLinkFormat, vip, version);
        } else {
            boolean pinnedToCurrentVersion = true;
            for(RegionEnum region : regions) {
                // We assume that all the regions are pinned to some versions
                // Skipping null check for the returned value
                // If we find a region which is not pinned to current region, we flip the flag
                if(!pinnedVersions.get(region).equals(version)) {
                    pinnedToCurrentVersion = false;
                }
            }
            // Check if all the vetrsions are pinned to same version, if they are provide a link to unpin
            // else provide a link to pin
            if(pinnedToCurrentVersion) {
                return String.format(unpinLinkFormat, vip);
            } else {
                return String.format(pinLinkFormat, vip, version);
            }
        }
    }

    private String getPerRegionPinUnpinLink(String vip, String version, RegionEnum region, Map<RegionEnum, String> pinnedVersions, boolean brokenChainVersion) {
        String bkgColor = brokenChainVersion ? COLOR_BROKEN_CHAIN : "";
        String pinLinkFormat = "<td bgcolor='" + bkgColor + "'><a href='/REST/vms/pinclients?vip=%s&version=%s&region=%s'>Pin %s</a></td>";
        String unpinLinkFormat = "<td bgcolor='#F4FA58'><a href='/REST/vms/unpinclients?vip=%s&region=%s'>Unpin %s</a></td>";

        // If the region is unpinned, i.e. if we get null when we asked the map for the value for that region
        // return pin link
        if(Strings.isNullOrEmpty(pinnedVersions.get(region))) {
            return String.format(pinLinkFormat, vip, version, region.toString(), region.toString());
        }

        // If we reached here, it means that region is pinned to some versions
        // Return:
        //   a. pin link if the version is not pinned to the current version
        //   b. unpin link if the version is pinned to the current version
        if(pinnedVersions.get(region).equals(version)) {
            return String.format(unpinLinkFormat,  vip, region.toString(), region.toString());
        } else {
            return String.format(pinLinkFormat, vip, version, region.toString(), region.toString());
        }
    }


}
