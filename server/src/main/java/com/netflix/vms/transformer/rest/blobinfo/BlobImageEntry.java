package com.netflix.vms.transformer.rest.blobinfo;

import com.netflix.aws.db.Item;
import com.netflix.aws.db.ItemAttribute;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.StandardToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.FastDateFormat;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * Holds Blob attributes per published version for all published FastBlobTypes
 *
 * @author dsu
 */
@SuppressWarnings("deprecation")
public class BlobImageEntry {
    private final static TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
    private final static FastDateFormat formatter = FastDateFormat.getInstance("dd-MMM-yyyy HH:mm:ss z", tz);
	
    private final String version;
    private Long publishedTimeStamp = null;
    private final Map<BlobType, Item> itemMap = new HashMap<>();
    private final Map<String, String> attribs = new TreeMap<String, String>();

    public BlobImageEntry(final String version) {
        this.version = version;
    }

    /**
     * Return true if missing DELTA or REVERSE DELTA
     */
    public boolean isBrokenChain() {
        return !itemMap.containsKey(BlobType.DELTA);
    }

    public void put(BlobType type, Item item) {
        itemMap.put(type, item);

        for (AttributeKeys key : AttributeKeys.values()) {
            final String attribName = key.name();
            ItemAttribute attrib = item.getAttribute(attribName);

            // TEMP: Backward compatible - due to name change
            if (attrib == null) attrib = item.getAttribute(Character.toLowerCase(attribName.charAt(0)) + attribName.substring(1));

            final String attribValue = attrib == null ? null : attrib.getValue();
            attribs.put(attribName, attribValue);
        }
        publishedTimeStamp = getPublishedTimeStamp(item);
    }

    private static Long getPublishedTimeStamp(final Item item) {
        final ItemAttribute attrib = item.getAttribute(AttributeKeys.publishedTimestamp.name());
        if (attrib==null) return null;

        final String value = attrib.getValue();
        if (!StringUtils.isBlank(value)) {
            try {
                return Long.parseLong(value);
            } catch (final Exception ex) {
            }
        }
        return null;
    }

    public String getVersion() {
        return version;
    }

    public String getPriorVersion() {
        return attribs.get(AttributeKeys.priorVersion.name());
    }

    public String getJarVersion() {
        return attribs.get(AttributeKeys.ProducedByJarVersion.name());
    }

    public Long getPublishedTimeStamp() {
        return publishedTimeStamp;
    }

    public Map<String, String> getAttribs() {
        return attribs;
    }

    public Map<BlobType, Item> getItemMap() {
        return Collections.unmodifiableMap(itemMap);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribs == null) ? 0 : attribs.hashCode());
        result = prime * result + ((itemMap == null) ? 0 : itemMap.hashCode());
        result = prime * result + ((publishedTimeStamp == null) ? 0 : publishedTimeStamp.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final BlobImageEntry other = (BlobImageEntry) obj;
        if (attribs == null) {
            if (other.attribs != null) return false;
        } else if (!attribs.equals(other.attribs)) return false;
        if (itemMap == null) {
            if (other.itemMap != null) return false;
        } else if (!itemMap.equals(other.itemMap)) return false;
        if (publishedTimeStamp == null) {
            if (other.publishedTimeStamp != null) return false;
        } else if (!publishedTimeStamp.equals(other.publishedTimeStamp)) return false;
        if (version == null) {
            if (other.version != null) return false;
        } else if (!version.equals(other.version)) return false;
        return true;
    }

    private Map<String, Object> createItemAttribsMap(final Item item) {
        final Map<String, Object> attrMap = new TreeMap<String, Object>();

        for (final ItemAttribute ia : item.getAttributes()) {
            final String name = ia.getName();
            final String value = ia.getValue();
            attrMap.put(name, value);
        }

        final Long ts = getPublishedTimeStamp(item);
        if (ts != null) {
            attribs.put("publishedDate", formatDateForDisplay(ts));
            
        }

        return attrMap;
    }

    public String toJson(final boolean isPrint) {
        final Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("version", version);
        map.put("attribs", attribs);

        final Map<String, Object> typeMap = new LinkedHashMap<String, Object>();
        map.put("types", typeMap);

        for (BlobType type : BlobType.values()) {
            final Item item = itemMap.get(type);
            if (item == null) {
                typeMap.put(type.name(), "n/a");
                continue;
            }

            final Map<String, Object> attrMap = createItemAttribsMap(item);
            typeMap.put(type.name(), attrMap);
        }

        return toJson(map, isPrint);
    }

    @Override
    public String toString() {
        final VMSToStringBuilder toStringBuilder = new VMSToStringBuilder(this);
        toStringBuilder.append("version", version);
        toStringBuilder.append("attribs", attribs);

        final StringBuilder sb = new StringBuilder();
        for (final BlobType type : BlobType.values()) {
            final Item item = itemMap.get(type);

            sb.append("\n\t").append(type.name()).append("=");
            if (item == null) {
                sb.append("=n/a");
                continue;
            }

            final Map<String, Object> attrMap = createItemAttribsMap(item);
            sb.append(attrMap);
        }
        toStringBuilder.append(sb);
        return toStringBuilder.toString();
    }

    public static class VMSToStringBuilder extends ToStringBuilder {

        public VMSToStringBuilder(final Object object) {
            super(object, VMSToStringStyle.INSTANCE);
        }
    }

    public static class VMSToStringStyle extends StandardToStringStyle {

        private static final long serialVersionUID = 17195679727926667L;
        public static final VMSToStringStyle INSTANCE = new VMSToStringStyle();

        private VMSToStringStyle() {
            super();
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
            this.setFieldSeparator(", ");
            this.setContentStart(" [");
        }
    }

    public static enum BlobType {
        SNAPSHOT,
        DELTA,
        REVERSEDELTA
    }
    
    
    static enum AttributeKeys {
        dataVersion, priorVersion, ProducedByServer, ProducedByJarVersion, VIP, sourceDataVersion, publishCycleDataTS, publishedTimestamp
    }
    
    private static String toJson(final Object object, final boolean isPrint) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            if (isPrint) mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
            return mapper.writeValueAsString(object);
        } catch (final IOException e) {
            return e.getMessage();
        }
    }
    
    private static String formatDateForDisplay(final long milis) {
        final Date nextDate = new Date(milis);
        return formatter.format(nextDate);
    }



}
