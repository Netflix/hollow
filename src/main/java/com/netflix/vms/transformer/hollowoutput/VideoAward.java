package com.netflix.vms.transformer.hollowoutput;


public class VideoAward implements Cloneable {

    public Video video = null;
    public VideoAwardType awardType = null;
    public VPerson person = null;
    public boolean isWinner = false;
    public int year = java.lang.Integer.MIN_VALUE;
    public int sequenceNumber = java.lang.Integer.MIN_VALUE;

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

    public VideoAward clone() {
        try {
            VideoAward clone = (VideoAward)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}