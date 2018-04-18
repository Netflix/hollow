package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="StoriesSynopses")
public class StoriesSynopses implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public TranslatedText narrativeText = null;
    @HollowTypeName(name="StoriesSynopsesHookList")
    public List<StoriesSynopsesHook> hooks = null;

    public StoriesSynopses setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public StoriesSynopses setNarrativeText(TranslatedText narrativeText) {
        this.narrativeText = narrativeText;
        return this;
    }
    public StoriesSynopses setHooks(List<StoriesSynopsesHook> hooks) {
        this.hooks = hooks;
        return this;
    }
    public StoriesSynopses addToHooks(StoriesSynopsesHook storiesSynopsesHook) {
        if (this.hooks == null) {
            this.hooks = new ArrayList<StoriesSynopsesHook>();
        }
        this.hooks.add(storiesSynopsesHook);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StoriesSynopses))
            return false;

        StoriesSynopses o = (StoriesSynopses) other;
        if(o.movieId != movieId) return false;
        if(o.narrativeText == null) {
            if(narrativeText != null) return false;
        } else if(!o.narrativeText.equals(narrativeText)) return false;
        if(o.hooks == null) {
            if(hooks != null) return false;
        } else if(!o.hooks.equals(hooks)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (narrativeText == null ? 1237 : narrativeText.hashCode());
        hashCode = hashCode * 31 + (hooks == null ? 1237 : hooks.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StoriesSynopses{");
        builder.append("movieId=").append(movieId);
        builder.append(",narrativeText=").append(narrativeText);
        builder.append(",hooks=").append(hooks);
        builder.append("}");
        return builder.toString();
    }

    public StoriesSynopses clone() {
        try {
            StoriesSynopses clone = (StoriesSynopses)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}