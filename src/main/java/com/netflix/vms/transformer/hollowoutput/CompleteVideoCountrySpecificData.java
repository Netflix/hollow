package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;

public class CompleteVideoCountrySpecificData {

    public Date metadataAvailabilityDate;
    public Date firstDisplayDate;
    public Map<NFLocale, Date> firstDisplayDateByLocale;
    public List<Certification> certificationList;
    public SortedMapOfDateWindowToListOfInteger dateWindowWiseSeasonSequenceNumberMap;
    public List<VMSAvailabilityWindow> mediaAvailabilityWindows;
    public List<VMSAvailabilityWindow> imagesAvailabilityWindows;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}