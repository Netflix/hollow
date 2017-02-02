package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.Integer;
import java.util.concurrent.TimeUnit;

public class OutputUtil {
    public static final long FIVE_SECOND_MILLIS = 5000l;

    public static Integer getNullableInteger(int value) {
        if (java.lang.Integer.MIN_VALUE == value) return null;

        return new Integer(value);
    }

    public static int minValueToZero(int value) {
        if (java.lang.Integer.MIN_VALUE == value) return 0;

        return value;
    }

    public static long round(final long date, final long roundByMillis) {
        return (date / roundByMillis) * roundByMillis;
    }

    public static long getRoundedTimeStamp(long value) {
        return round(value, FIVE_SECOND_MILLIS);
    }

    public static Date getRoundedDate(long value) {
        return new Date(getRoundedTimeStamp(value));
    }

    public static Date getRoundedDate(DateHollow dateHollow) {
        return dateHollow != null ? getRoundedDate(dateHollow._getValue()) : null;
    }

    public static String formatDuration(final long milliseconds, final boolean isShowActualValue) {
        final String duration = formatAsElapsedTime(milliseconds, " ");
        return isShowActualValue ? String.format("%d - (%s)", milliseconds, duration) : duration;
    }

    public static String formatAsElapsedTime(final long milliseconds, final String delim) {
        long _millis = milliseconds;
        final long _days = TimeUnit.MILLISECONDS.toDays(_millis);
        _millis -= TimeUnit.DAYS.toMillis(_days);
        final long _hours = TimeUnit.MILLISECONDS.toHours(_millis);
        _millis -= TimeUnit.HOURS.toMillis(_hours);
        final long _minutes = TimeUnit.MILLISECONDS.toMinutes(_millis);
        _millis -= TimeUnit.MINUTES.toMillis(_minutes);
        final long _seconds = TimeUnit.MILLISECONDS.toSeconds(_millis);

        String format = "H:%02d%sM:%02d%sS:%02d";
        if (_days > 0) {
            format = "Days:%d " + format;
            return String.format(format, _days, _hours, delim, _minutes, delim, _seconds);
        }

        return String.format(format, _hours, delim, _minutes, delim, _seconds);
    }
}