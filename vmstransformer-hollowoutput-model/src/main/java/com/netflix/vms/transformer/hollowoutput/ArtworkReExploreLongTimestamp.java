package com.netflix.vms.transformer.hollowoutput;

public class ArtworkReExploreLongTimestamp {

    public long reExploreTimestamp = Long.MIN_VALUE;

    public ArtworkReExploreLongTimestamp(long ts) {
        this.reExploreTimestamp = ts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtworkReExploreLongTimestamp)) return false;

        ArtworkReExploreLongTimestamp that = (ArtworkReExploreLongTimestamp) o;

        return reExploreTimestamp == that.reExploreTimestamp;
    }

    @Override
    public int hashCode() {
        return (int) (reExploreTimestamp ^ (reExploreTimestamp >>> 32));
    }

    @Override
    public String toString() {
        return String.valueOf(reExploreTimestamp);
    }
    
    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
