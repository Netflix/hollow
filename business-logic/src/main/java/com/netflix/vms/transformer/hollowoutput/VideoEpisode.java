package com.netflix.vms.transformer.hollowoutput;


public class VideoEpisode implements Cloneable {

    public Video seriesParent = null;
    public Video deliverableVideo = null;
    public int sequenceNumber = java.lang.Integer.MIN_VALUE;
    public int showSequenceNumber = java.lang.Integer.MIN_VALUE;
    public int seasonSequenceNumber = java.lang.Integer.MIN_VALUE;
    public int episodeSequenceNumber = java.lang.Integer.MIN_VALUE;

    public Boolean midSeason = false;
    public Boolean seasonFinale = false;
    public Boolean showFinale = false;

    @Override
    public int hashCode() {
        int result = seriesParent.hashCode();
        result = 31 * result + deliverableVideo.hashCode();
        result = 31 * result + sequenceNumber;
        result = 31 * result + showSequenceNumber;
        result = 31 * result + seasonSequenceNumber;
        result = 31 * result + episodeSequenceNumber;
        result = 31 * result + midSeason.hashCode();
        result = 31 * result + seasonFinale.hashCode();
        result = 31 * result + showFinale.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoEpisode)) return false;

        VideoEpisode that = (VideoEpisode) o;

        if (sequenceNumber != that.sequenceNumber) return false;
        if (showSequenceNumber != that.showSequenceNumber) return false;
        if (seasonSequenceNumber != that.seasonSequenceNumber) return false;
        if (episodeSequenceNumber != that.episodeSequenceNumber) return false;
        if (!seriesParent.equals(that.seriesParent)) return false;
        if (!deliverableVideo.equals(that.deliverableVideo)) return false;
        if (!midSeason.equals(that.midSeason)) return false;
        if (!seasonFinale.equals(that.seasonFinale)) return false;
        return showFinale.equals(that.showFinale);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoEpisode{");
        builder.append("seriesParent=").append(seriesParent);
        builder.append(",deliverableVideo=").append(deliverableVideo);
        builder.append(",sequenceNumber=").append(sequenceNumber);
        builder.append(",showSequenceNumber=").append(showSequenceNumber);
        builder.append(",seasonSequenceNumber=").append(seasonSequenceNumber);
        builder.append(",episodeSequenceNumber=").append(episodeSequenceNumber);
        builder.append(",midSeason=").append(midSeason);
        builder.append(",seasonFinale=").append(seasonFinale);
        builder.append(",showFinale=").append(showFinale);
        builder.append("}");
        return builder.toString();
    }

    public VideoEpisode clone() {
        try {
            VideoEpisode clone = (VideoEpisode)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
