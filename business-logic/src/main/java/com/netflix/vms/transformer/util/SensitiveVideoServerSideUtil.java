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

    /**
     * If the title is not an original and is in DVD catalog, then return an exempt date (some date in 1997).
     * <br>
     * Calculate grace period date: Availability Date -  days using metadataReleaseDays or prePromoDays (first non-null value))
     * <br>
     * Calculate minimum of following dates - firstDisplayDate, firstPhaseStartDate, availabilityDate, gracePeriodDate and earliestPhaseDate
     * <br>
     * If minimum is null - then hold back all metadata.
     *
     * @param inDVDCatalog
     * @param isOriginal
     * @param videoSetTypes
     * @param firstDisplayDate When the title was first released/available in Netflix
     * @param firstPhaseStartDate Start date of first phase in Rollouts with tag DISPLAY_PAGE
     * @param availabilityDate Start date of the first window in the availability windows
     * @param prePromoDays Pre-promo days from Contract of the first availability Window
     * @param metadataReleaseDays Metadata release days from VideoGeneral feed
     * @param constants Cycle constants for hold back and exempt dates
     * @param earliestScheduledPhaseDate Earliest scheduled phase of the images for the given title
     * @return Metadata availability date
     */
    public static final Date getMetadataAvailabilityDate(boolean inDVDCatalog, boolean isOriginal, Set<VideoSetType> videoSetTypes, Long firstDisplayDate, Long firstPhaseStartDate,
            Long availabilityDate, Integer prePromoDays, Integer metadataReleaseDays, CycleConstants constants, Long earliestScheduledPhaseDate) {
        if (inDVDCatalog && !isOriginal)
            return constants.EXEMPT_HOLD_BACK_DATE;

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

    /**
     * Check if metadata availability date is in future.
     *
     * @param metadataAvailabilityDate
     * @param ctx
     * @return true if metadata is sensitive.
     */
    public static boolean isSensitiveMetaData(Date metadataAvailabilityDate, TransformerContext ctx) {
        return metadataAvailabilityDate == null || metadataAvailabilityDate.val > ctx.getNowMillis();
    }
}