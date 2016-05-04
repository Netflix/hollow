package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.hollowinput.ListOfReleaseDatesHollow;
import com.netflix.vms.transformer.hollowinput.ReleaseDateHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VideoDateWindowHollow;
import com.netflix.vms.transformer.hollowoutput.Date;
import java.util.Calendar;
import java.util.TimeZone;

public class VideoDateUtil {
    public static enum ReleaseDateType {
        DVDStreet, Theatrical, Broadcast
    }

    public static ReleaseDateHollow getReleaseDateType(ReleaseDateType type, VideoDateWindowHollow videoDate_) {
        return getReleaseDateType(type.name(), videoDate_);
    }

    public static ReleaseDateHollow getReleaseDateType(String type, VideoDateWindowHollow videoDate_) {
        ListOfReleaseDatesHollow releaseDateList = videoDate_._getReleaseDates();
        if (releaseDateList == null) {
            return null;
        }

        for (ReleaseDateHollow date_ : releaseDateList) {
            StringHollow type_ = date_._getReleaseDateType();
            if(type_ != null) {
                if(type.equals(type_._getValue())) {
                    return date_;
                }
            }
        }
        return null;
    }

    public static Date convertToHollowOutputDate(ReleaseDateHollow date_) {
        if (date_ == null) {
            return null;
        }

        Date result_ = null;
        Integer year = date_._getYearBoxed();
        Integer month = date_._getMonthBoxed();
        Integer day = date_._getDayBoxed();

        if(year != null && month != null && day != null){
            Calendar cal = new Calendar.Builder()
            // NOTE: Upstream is expected to provide the date (that we are build from day, month, year in PST timezone)
                    .setTimeZone(TimeZone.getTimeZone("America/Los_Angeles")).set(Calendar.DATE, day)
                    // NOTE: Beehive is expected to send us 1 based month and Calendar uses 0 based month.
                    // Ex: Beehive gives 1 for January and Calendar expects 0 for January
                    .set(Calendar.MONTH, month - 1).set(Calendar.YEAR, year).build();
            result_ = new Date(cal.getTimeInMillis());
        }
        return result_;
    }
}
