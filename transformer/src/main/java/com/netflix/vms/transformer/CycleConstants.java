package com.netflix.vms.transformer;

import com.netflix.vms.transformer.hollowoutput.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.hollowoutput.VideoNodeType;

public class CycleConstants {

    public final VideoNodeType MOVIE = new VideoNodeType("MOVIE");
    public final VideoNodeType SHOW = new VideoNodeType("SHOW");
    public final VideoNodeType SEASON = new VideoNodeType("SEASON");
    public final VideoNodeType EPISODE = new VideoNodeType("EPISODE");
    public final VideoNodeType SUPPLEMENTAL = new VideoNodeType("SUPPLEMENTAL");

    public final VideoSetType PAST = new VideoSetType("Past");
    public final VideoSetType PRESENT = new VideoSetType("Present");
    public final VideoSetType FUTURE = new VideoSetType("Future");
    public final VideoSetType EXTENDED = new VideoSetType("Extended");

    public final Date HOLD_BACK_INDEFINITELY_DATE = null;
    public final Date EXEMPT_HOLD_BACK_DATE = new Date((new DateTime(1997, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC)).getMillis());


}
