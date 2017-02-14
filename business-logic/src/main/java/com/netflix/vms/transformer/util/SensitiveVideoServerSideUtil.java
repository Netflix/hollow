package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import java.util.Set;

public class SensitiveVideoServerSideUtil {
    public static final long ONE_DAY_AS_MILLIS = 86400000l;


    private static final int getFirstNonNullValue(int defaultValue, Integer... values) {
        for (Integer val : values) {
            if (val != null) return val.intValue();
        }
        return defaultValue;
    }

    private static final Long min(Long... values) {
        Long min = null;
        for (Long val : values) {
            if (val == null) continue;
            if (min == null || min.longValue() > val.longValue()) {
                min = val;
            }
        }

        return min;
    }

    private static boolean isExempt(Set<VideoSetType> videoSetTypes, CycleConstants constants) {
        if (videoSetTypes.contains(constants.FUTURE)) return false;

        if (videoSetTypes.contains(constants.EXTENDED)) return true;

        return false;
    }

    public static final Date getMetadataAvailabilityDate(Set<VideoSetType> videoSetTypes, Long firstDisplayDate, Long firstPhaseStartDate, 
    		Long availabilityDate, Integer prePromoDays, Integer metadataReleaseDays, CycleConstants constants, Long earliestScheduledPhaseDate) {
        Long gracePeriodDate = null;
        if (availabilityDate!=null) {
            int gracePeriodDays = getFirstNonNullValue(0, metadataReleaseDays, prePromoDays);
            gracePeriodDate = availabilityDate.longValue() - (gracePeriodDays * ONE_DAY_AS_MILLIS);
        }

        Long minValue = min(firstDisplayDate, firstPhaseStartDate, availabilityDate, gracePeriodDate, earliestScheduledPhaseDate);
        if (minValue == null) {
            if (isExempt(videoSetTypes, constants)) {
                return constants.EXEMPT_HOLD_BACK_DATE;
            } else {
                return constants.HOLD_BACK_INDEFINITELY_DATE;
            }
        }

        return OutputUtil.getRoundedDate(minValue);
    }

    public static boolean isSensitiveMetaData(Date metadataAvailabilityDate, TransformerContext ctx) {
        return metadataAvailabilityDate == null || metadataAvailabilityDate.val > ctx.getNowMillis();
    }
}