package com.netflix.vms.transformer.hollowoutput;


public class VideoEpisode implements Cloneable {

    public Video seriesParent = null;
    public Video deliverableVideo = null;
    public int sequenceNumber = java.lang.Integer.MIN_VALUE;
    public int showSequenceNumber = java.lang.Integer.MIN_VALUE;
    public int seasonSequenceNumber = java.lang.Integer.MIN_VALUE;
    public int episodeSequenceNumber = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoEpisode))
            return false;

        VideoEpisode o = (VideoEpisode) other;
        if(o.seriesParent == null) {
            if(seriesParent != null) return false;
        } else if(!o.seriesParent.equals(seriesParent)) return false;
        if(o.deliverableVideo == null) {
            if(deliverableVideo != null) return false;
        } else if(!o.deliverableVideo.equals(deliverableVideo)) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.showSequenceNumber != showSequenceNumber) return false;
        if(o.seasonSequenceNumber != seasonSequenceNumber) return false;
        if(o.episodeSequenceNumber != episodeSequenceNumber) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (seriesParent == null ? 1237 : seriesParent.hashCode());
        hashCode = hashCode * 31 + (deliverableVideo == null ? 1237 : deliverableVideo.hashCode());
        hashCode = hashCode * 31 + sequenceNumber;
        hashCode = hashCode * 31 + showSequenceNumber;
        hashCode = hashCode * 31 + seasonSequenceNumber;
        hashCode = hashCode * 31 + episodeSequenceNumber;
        return hashCode;
    }

    public VideoEpisode clone() {
        try {
            VideoEpisode clone = (VideoEpisode)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}