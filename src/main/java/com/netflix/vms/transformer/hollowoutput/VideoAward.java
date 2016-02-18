package com.netflix.vms.transformer.hollowoutput;


public class VideoAward {

    public Video video;
    public VideoAwardType awardType;
    public VPerson person;
    public boolean isWinner;
    public int year;
    public int sequenceNumber;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoAward))
            return false;

        VideoAward o = (VideoAward) other;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        if(o.awardType == null) {
            if(awardType != null) return false;
        } else if(!o.awardType.equals(awardType)) return false;
        if(o.person == null) {
            if(person != null) return false;
        } else if(!o.person.equals(person)) return false;
        if(o.isWinner != isWinner) return false;
        if(o.year != year) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}