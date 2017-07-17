package com.netflix.vms.transformer;

import com.netflix.encodingtools.videoresolutiontypelibrary.VideoResolutionType;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import java.util.Set;

public class SimpleCupLibrary implements CupLibrary {
    @Override
    public int getMaximumVideoHeight(Set<String> cupTokens, String deviceCategory) {
        return Integer.MAX_VALUE;
    }

    public static final VideoResolutionType DYMMY_VIDEO_RESOLUTION_TYPE = new VideoResolutionType() {

        @Override
        public int compareTo(VideoResolutionType o) {
            return Long.compare(getMaxPixelsPerFrame(), o.getMaxPixelsPerFrame());
        }

        @Override
        public int getId() {
            return Integer.MAX_VALUE;
        }

        @Override
        public String getName() {
            return "Dummy Max Resolution for Testing";
        }

        @Override
        public long getMaxWidth() {
            return Long.MAX_VALUE;
        }

        @Override
        public long getMaxHeight() {
            return Long.MAX_VALUE;
        }

        @Override
        public long getMaxPixelsPerFrame() {
            return Long.MAX_VALUE;
        }
    };

    public static CupLibrary INSTANCE = new SimpleCupLibrary();

    @Override
    public VideoResolutionType getCupMaxVideoResolutionType(Set<String> cupTokens, String cupDeviceCategory) {
        return DYMMY_VIDEO_RESOLUTION_TYPE;
    }

    @Override
    public VideoResolutionType getResolutionType(int id) {
        return DYMMY_VIDEO_RESOLUTION_TYPE;
    }

    @Override
    public VideoResolutionType getResolutionType(int width, int height) {
        return DYMMY_VIDEO_RESOLUTION_TYPE;
    }
}