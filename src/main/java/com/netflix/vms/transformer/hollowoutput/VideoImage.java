package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoImage {

    public Video videoId;
    public VideoMoment videoMoment;
    public List<ImageDownloadable> downloadableList;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoImage))
            return false;

        VideoImage o = (VideoImage) other;
        if(o.videoId == null) {
            if(videoId != null) return false;
        } else if(!o.videoId.equals(videoId)) return false;
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