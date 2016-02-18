package com.netflix.vms.transformer.hollowoutput;


public class VideoEpisode {

    public Video seriesParent;
    public Video deliverableVideo;
    public int sequenceNumber;
    public int showSequenceNumber;
    public int seasonSequenceNumber;
    public int episodeSequenceNumber;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}