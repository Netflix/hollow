package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class SensitiveVideoServerSideUtil {
    public static final long ONE_DAY_AS_MILLIS = 86400000l;
    private static final Set<VideoSetType> EXEMPT_TYPES_SET = Collections.unmodifiableSet(Collections.singleton(VideoSetTypeUtil.EXTENDED));

    // Special Date value which indicate hold back indefinitely
    public static final Date HOLD_BACK_INDEFINITELY_DATE = null;

    public static final Date EXEMPT_HOLD_BACK_DATE = new Date((new DateTime(1997, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC)).getMillis());

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

    private static boolean isExempt(Set<VideoSetType> videoSetTypes) {
        if (videoSetTypes.contains(VideoSetTypeUtil.FUTURE)) return false;

        for (VideoSetType t : EXEMPT_TYPES_SET) {
            if (videoSetTypes.contains(t)) return true;
        }
        return false;
    }

    public static final Date getMetadataAvailabilityDate(Set<VideoSetType> videoSetTypes, Long firstDisplayDate, Long firstPhaseStartDate, Long availabilityDate, Integer prePromoDays, Integer metadataReleaseDays) {
        Long gracePeriodDate = null;
        if (availabilityDate!=null) {
            int gracePeriodDays = getFirstNonNullValue(0, metadataReleaseDays, prePromoDays);
            gracePeriodDate = availabilityDate.longValue() - (gracePeriodDays * ONE_DAY_AS_MILLIS);
        }

        Long minValue = min(firstDisplayDate, firstPhaseStartDate, availabilityDate, gracePeriodDate);
        if (minValue == null) {
            if (isExempt(videoSetTypes)) {
                return EXEMPT_HOLD_BACK_DATE;
            } else {
                return HOLD_BACK_INDEFINITELY_DATE;
            }
        }

        return new Date(minValue);
    }
}