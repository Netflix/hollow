package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.aws.db.ItemAttribute;
import com.netflix.configadmin.ConfigAdmin;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

@SuppressWarnings("deprecation")
public class BlobMetaDataUtil {
    private final static TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
    private final static FastDateFormat formatter = FastDateFormat.getInstance("dd-MMM-yyyy HH:mm:ss z", tz);

    public static enum HEADER {
        VIP, dataVersion, priorVersion, producedTime, publishedTimestamp, ProducedByServer, ProducedByJarVersion;
    }

    public static Map<String, String> fetchCoreHeaders(HollowReadStateEngine stateengine) {
        if (stateengine == null) return Collections.emptyMap();

        Map<String, String> map = new LinkedHashMap<>();

        for (HEADER header : HEADER.values()) {
            String name = header.name();
            String value = stateengine.getHeaderTag(name);
            map.put(name, value);
        }

        return map;
    }

    public static Map<String, String> getPublisherProps(String vip, long publishedTimestamp, String currentVersion, String priorVersion) {
        Map<String, String> map = new HashMap<>();

        map.put(HEADER.producedTime.name(), formatter.format(publishedTimestamp));
        map.put(HEADER.publishedTimestamp.name(), String.valueOf(publishedTimestamp));
        map.put(HEADER.dataVersion.name(), currentVersion);
        map.put(HEADER.priorVersion.name(), priorVersion);

        map.put(HEADER.VIP.name(), vip);
        map.put(HEADER.ProducedByServer.name(), getServerId());
        map.put(HEADER.ProducedByJarVersion.name(), getJarVersion());

        return map;
    }

    public static void addAttribute(List<ItemAttribute> attributes, String key, String value) {
        attributes.add(new ItemAttribute(key, value, true));
    }

    public static void addAttribute(List<ItemAttribute> attributes, Enum<?> key, String value) {
        addAttribute(attributes, key.name(), value);
    }

    public static void addPublisherProps(String vip, List<ItemAttribute> attributes, long publishedTimestamp, String currentVersion, String priorVersion) {
        for (Map.Entry<String, String> entry : getPublisherProps(vip, publishedTimestamp, currentVersion, priorVersion).entrySet()) {
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

    public static String getJarVersion() {
        if (null != version) {
            return version;
        }

        initLib();
        return version;
    }

    private static void initLib() {
        final String proposedVersion = ConfigAdmin.getLibraryVersion("vmstransformer-business-logic", BlobMetaDataUtil.class);
        version = StringUtils.isBlank(proposedVersion) ? "Unknown" : proposedVersion;
    }

}