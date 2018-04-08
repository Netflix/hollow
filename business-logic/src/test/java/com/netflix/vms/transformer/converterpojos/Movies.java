package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Movies")
public class Movies implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public TranslatedText shortDisplayName = null;
    public TranslatedText siteSynopsis = null;
    public TranslatedText originalTitle = null;
    public TranslatedText displayName = null;
    public TranslatedText aka = null;
    public TranslatedText transliterated = null;
    public TranslatedText tvSynopsis = null;

    public Movies setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public Movies setShortDisplayName(TranslatedText shortDisplayName) {
        this.shortDisplayName = shortDisplayName;
        return this;
    }
    public Movies setSiteSynopsis(TranslatedText siteSynopsis) {
        this.siteSynopsis = siteSynopsis;
        return this;
    }
    public Movies setOriginalTitle(TranslatedText originalTitle) {
        this.originalTitle = originalTitle;
        return this;
    }
    public Movies setDisplayName(TranslatedText displayName) {
        this.displayName = displayName;
        return this;
    }
    public Movies setAka(TranslatedText aka) {
        this.aka = aka;
        return this;
    }
    public Movies setTransliterated(TranslatedText transliterated) {
        this.transliterated = transliterated;
        return this;
    }
    public Movies setTvSynopsis(TranslatedText tvSynopsis) {
        this.tvSynopsis = tvSynopsis;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Movies))
            return false;

        Movies o = (Movies) other;
        if(o.movieId != movieId) return false;
        if(o.shortDisplayName == null) {
            if(shortDisplayName != null) return false;
        } else if(!o.shortDisplayName.equals(shortDisplayName)) return false;
        if(o.siteSynopsis == null) {
            if(siteSynopsis != null) return false;
        } else if(!o.siteSynopsis.equals(siteSynopsis)) return false;
        if(o.originalTitle == null) {
            if(originalTitle != null) return false;
        } else if(!o.originalTitle.equals(originalTitle)) return false;
        if(o.displayName == null) {
            if(displayName != null) return false;
        } else if(!o.displayName.equals(displayName)) return false;
        if(o.aka == null) {
            if(aka != null) return false;
        } else if(!o.aka.equals(aka)) return false;
        if(o.transliterated == null) {
            if(transliterated != null) return false;
        } else if(!o.transliterated.equals(transliterated)) return false;
        if(o.tvSynopsis == null) {
            if(tvSynopsis != null) return false;
        } else if(!o.tvSynopsis.equals(tvSynopsis)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (shortDisplayName == null ? 1237 : shortDisplayName.hashCode());
        hashCode = hashCode * 31 + (siteSynopsis == null ? 1237 : siteSynopsis.hashCode());
        hashCode = hashCode * 31 + (originalTitle == null ? 1237 : originalTitle.hashCode());
        hashCode = hashCode * 31 + (displayName == null ? 1237 : displayName.hashCode());
        hashCode = hashCode * 31 + (aka == null ? 1237 : aka.hashCode());
        hashCode = hashCode * 31 + (transliterated == null ? 1237 : transliterated.hashCode());
        hashCode = hashCode * 31 + (tvSynopsis == null ? 1237 : tvSynopsis.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Movies{");
        builder.append("movieId=").append(movieId);
        builder.append(",shortDisplayName=").append(shortDisplayName);
        builder.append(",siteSynopsis=").append(siteSynopsis);
        builder.append(",originalTitle=").append(originalTitle);
        builder.append(",displayName=").append(displayName);
        builder.append(",aka=").append(aka);
        builder.append(",transliterated=").append(transliterated);
        builder.append(",tvSynopsis=").append(tvSynopsis);
        builder.append("}");
        return builder.toString();
    }

    public Movies clone() {
        try {
            Movies clone = (Movies)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}