package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;

public class CompleteVideoCountrySpecificData implements Cloneable {

    public Date metadataAvailabilityDate = null;
    public Date firstDisplayDate = null;
    public Map<NFLocale, Date> firstDisplayDateByLocale = null;
    public List<Certification> certificationList = null;
    public SortedMapOfDateWindowToListOfInteger dateWindowWiseSeasonSequenceNumberMap = null;
    public List<VMSAvailabilityWindow> mediaAvailabilityWindows = null;
    public List<VMSAvailabilityWindow> imagesAvailabilityWindows = null;

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
        if(o.mediaAvailabilityWindows == null) {
            if(mediaAvailabilityWindows != null) return false;
        } else if(!o.mediaAvailabilityWindows.equals(mediaAvailabilityWindows)) return false;
        if(o.imagesAvailabilityWindows == null) {
            if(imagesAvailabilityWindows != null) return false;
        } else if(!o.imagesAvailabilityWindows.equals(imagesAvailabilityWindows)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (metadataAvailabilityDate == null ? 1237 : metadataAvailabilityDate.hashCode());
        hashCode = hashCode * 31 + (firstDisplayDate == null ? 1237 : firstDisplayDate.hashCode());
        hashCode = hashCode * 31 + (firstDisplayDateByLocale == null ? 1237 : firstDisplayDateByLocale.hashCode());
        hashCode = hashCode * 31 + (certificationList == null ? 1237 : certificationList.hashCode());
        hashCode = hashCode * 31 + (dateWindowWiseSeasonSequenceNumberMap == null ? 1237 : dateWindowWiseSeasonSequenceNumberMap.hashCode());
        hashCode = hashCode * 31 + (mediaAvailabilityWindows == null ? 1237 : mediaAvailabilityWindows.hashCode());
        hashCode = hashCode * 31 + (imagesAvailabilityWindows == null ? 1237 : imagesAvailabilityWindows.hashCode());
        return hashCode;
    }

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