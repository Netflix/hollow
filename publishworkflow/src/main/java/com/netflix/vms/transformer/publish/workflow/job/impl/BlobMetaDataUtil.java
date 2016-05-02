package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.aws.db.ItemAttribute;
import com.netflix.configadmin.ConfigAdmin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

@SuppressWarnings("deprecation")
public class BlobMetaDataUtil {
    private final static TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
    private final static FastDateFormat formatter = FastDateFormat.getInstance("dd-MMM-yyyy HH:mm:ss z", tz);


    public static Map<String, String> getPublisherProps(long publishedTimestamp, String currentVersion, String priorVersion) {
        Map<String, String> map = new HashMap<>();

        map.put("producedTime", formatter.format(publishedTimestamp));
        map.put("publishedTimestamp", formatter.format(publishedTimestamp));
        map.put("dataVersion", currentVersion);
        map.put("priorVersion", priorVersion);

        //map.put("VIP", MetaDataCommonPropertyManager.getVipAddressProperty());
        map.put("ProducedByServer", getServerId());
        map.put("ProducedByJarVersion", getJarVersion());

        return map;
    }

    public static void addAttribute(List<ItemAttribute> attributes, String key, String value) {
        attributes.add(new ItemAttribute(key, value, true));
    }

    public static void addAttribute(List<ItemAttribute> attributes, Enum<?> key, String value) {
        addAttribute(attributes, key.name(), value);
    }

    public static void addPublisherProps(List<ItemAttribute> attributes, long publishedTimestamp, String currentVersion, String priorVersion) {
        for (Map.Entry<String, String> entry : getPublisherProps(publishedTimestamp, currentVersion, priorVersion).entrySet()) {
            addAttribute(attributes, entry.getKey(), entry.getValue());
        }
    }

    public static String getServerId() {
        String hostname = "unknown";
        if (ApplicationInfoManager.getInstance() != null && ApplicationInfoManager.getInstance().getInfo() != null) {
            hostname = ApplicationInfoManager.getInstance().getInfo().getHostName();
        }
        return hostname;
    }

    private static String version = null;

    private static void initLib() {
        final String proposedVersion = ConfigAdmin.getLibraryVersion("transformer-server", BlobMetaDataUtil.class);
        version = StringUtils.isBlank(proposedVersion) ? "Unknown" : proposedVersion;
    }

    public static String getJarVersion() {
        if (null != version) {
            return version;
        }

        initLib();
        return version;
    }


}