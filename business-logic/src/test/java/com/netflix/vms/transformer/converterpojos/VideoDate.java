package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="VideoDate")
public class VideoDate implements Cloneable {

    public long videoId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="VideoDateWindowList")
    public List<VideoDateWindow> window = null;

    public VideoDate setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public VideoDate setWindow(List<VideoDateWindow> window) {
        this.window = window;
        return this;
    }
    public VideoDate addToWindow(VideoDateWindow videoDateWindow) {
        if (this.window == null) {
            this.window = new ArrayList<VideoDateWindow>();
        }
        this.window.add(videoDateWindow);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoDate))
            return false;

        VideoDate o = (VideoDate) other;
        if(o.videoId != videoId) return false;
        if(o.window == null) {
            if(window != null) return false;
        } else if(!o.window.equals(window)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        hashCode = hashCode * 31 + (window == null ? 1237 : window.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoDate{");
        builder.append("videoId=").append(videoId);
        builder.append(",window=").append(window);
        builder.append("}");
        return builder.toString();
    }

    public VideoDate clone() {
        try {
            VideoDate clone = (VideoDate)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}