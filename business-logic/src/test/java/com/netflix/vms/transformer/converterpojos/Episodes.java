package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Episodes")
public class Episodes implements Cloneable {

    public TranslatedText episodeName = null;
    public long movieId = java.lang.Long.MIN_VALUE;
    public long episodeId = java.lang.Long.MIN_VALUE;

    public Episodes setEpisodeName(TranslatedText episodeName) {
        this.episodeName = episodeName;
        return this;
    }
    public Episodes setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public Episodes setEpisodeId(long episodeId) {
        this.episodeId = episodeId;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Episodes))
            return false;

        Episodes o = (Episodes) other;
        if(o.episodeName == null) {
            if(episodeName != null) return false;
        } else if(!o.episodeName.equals(episodeName)) return false;
        if(o.movieId != movieId) return false;
        if(o.episodeId != episodeId) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (episodeName == null ? 1237 : episodeName.hashCode());
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (int) (episodeId ^ (episodeId >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Episodes{");
        builder.append("episodeName=").append(episodeName);
        builder.append(",movieId=").append(movieId);
        builder.append(",episodeId=").append(episodeId);
        builder.append("}");
        return builder.toString();
    }

    public Episodes clone() {
        try {
            Episodes clone = (Episodes)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}