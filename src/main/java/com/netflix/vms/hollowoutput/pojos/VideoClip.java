package com.netflix.vms.hollowoutput.pojos;

import java.util.List;

public class VideoClip {

    public Video videoId;
    public long msDuration;
    public VideoMoment videoMoment;
    public List<StreamDownloadable> downloadableList;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoClip))
            return false;

        VideoClip o = (VideoClip) other;
        if(o.videoId == null) {
            if(videoId != null) return false;
        } else if(!o.videoId.equals(videoId)) return false;
        if(o.msDuration != msDuration) return false;
        if(o.videoMoment == null) {
            if(videoMoment != null) return false;
        } else if(!o.videoMoment.equals(videoMoment)) return false;
        if(o.downloadableList == null) {
            if(downloadableList != null) return false;
        } else if(!o.downloadableList.equals(downloadableList)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}