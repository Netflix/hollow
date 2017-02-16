package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;

public class CompleteVideoCountrySpecificData implements Cloneable {

    public boolean isSensitiveMetaData = false;
    public Date metadataAvailabilityDate = null;
    public Date firstDisplayDate = null;
    public Map<NFLocale, Date> firstDisplayDateByLocale = null;
    public List<Certification> certificationList = null;
    public SortedMapOfDateWindowToListOfInteger dateWindowWiseSeasonSequenceNumberMap = null;
    public List<VMSAvailabilityWindow> availabilityWindows = null;

    @Override
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CompleteVideoCountrySpecificData))
            return false;

        CompleteVideoCountrySpecificData o = (CompleteVideoCountrySpecificData) other;
        if(o.metadataAvailabilityDate == null) {
            if(metadataAvailabilityDate != null) return false;
        } else if(!o.metadataAvailabilityDate.equals(metadataAvailabilityDate)) return false;
        if(o.firstDisplayDate == null) {
            if(firstDisplayDate != null) return false;
        } else if(!o.firstDisplayDate.equals(firstDisplayDate)) return false;
        if(o.firstDisplayDateByLocale == null) {
            if(firstDisplayDateByLocale != null) return false;
        } else if(!o.firstDisplayDateByLocale.equals(firstDisplayDateByLocale)) return false;
        if(o.certificationList == null) {
            if(certificationList != null) return false;
        } else if(!o.certificationList.equals(certificationList)) return false;
        if(o.dateWindowWiseSeasonSequenceNumberMap == null) {
            if(dateWindowWiseSeasonSequenceNumberMap != null) return false;
        } else if(!o.dateWindowWiseSeasonSequenceNumberMap.equals(dateWindowWiseSeasonSequenceNumberMap)) return false;
        if(o.availabilityWindows == null) {
            if(availabilityWindows != null) return false;
        } else if(!o.availabilityWindows.equals(availabilityWindows)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (metadataAvailabilityDate == null ? 1237 : metadataAvailabilityDate.hashCode());
        hashCode = hashCode * 31 + (firstDisplayDate == null ? 1237 : firstDisplayDate.hashCode());
        hashCode = hashCode * 31 + (firstDisplayDateByLocale == null ? 1237 : firstDisplayDateByLocale.hashCode());
        hashCode = hashCode * 31 + (certificationList == null ? 1237 : certificationList.hashCode());
        hashCode = hashCode * 31 + (dateWindowWiseSeasonSequenceNumberMap == null ? 1237 : dateWindowWiseSeasonSequenceNumberMap.hashCode());
        hashCode = hashCode * 31 + (availabilityWindows == null ? 1237 : availabilityWindows.hashCode());
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("CompleteVideoCountrySpecificData{");
        builder.append("metadataAvailabilityDate=").append(metadataAvailabilityDate);
        builder.append(",firstDisplayDate=").append(firstDisplayDate);
        builder.append(",firstDisplayDateByLocale=").append(firstDisplayDateByLocale);
        builder.append(",certificationList=").append(certificationList);
        builder.append(",dateWindowWiseSeasonSequenceNumberMap=").append(dateWindowWiseSeasonSequenceNumberMap);
        builder.append(",mediaAvailabilityWindows=").append(availabilityWindows);
        builder.append("}");
        return builder.toString();
    }

    @Override
    public CompleteVideoCountrySpecificData clone() {
        try {
            CompleteVideoCountrySpecificData clone = (CompleteVideoCountrySpecificData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}