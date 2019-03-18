package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.core.memory.encoding.HashCodes;

public class VideoCountryKey {
    private final String country;
    private final int videoId;

    public VideoCountryKey(String country, int videoId) {
        this.country = country;
        this.videoId = videoId;
    }

    public String getCountry() {
        return country;
    }

    public int getVideoId() {
        return videoId;
    }

    @Override
    public int hashCode() {
        return HashCodes.hashInt(country.hashCode()) ^ HashCodes.hashInt(videoId);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof VideoCountryKey) {
            return ((VideoCountryKey) other).getCountry().equals(country) && ((VideoCountryKey) other).getVideoId() == videoId;
        }
        return false;
    }
    @Override
    public String toString() {
        return "VideoCountryKey [country=" + country + ", videoId="
                + videoId + "]";
    }

    public String toShortString() {
        return "["+country + ", "+ videoId + "]";
    }
}
