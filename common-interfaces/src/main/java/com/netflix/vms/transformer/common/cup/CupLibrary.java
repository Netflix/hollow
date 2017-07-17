package com.netflix.vms.transformer.common.cup;

import com.netflix.encodingtools.videoresolutiontypelibrary.VideoResolutionType;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Encapsulate code to interact with external library to determine VideoResolution
 *
 * For example (just for reference since it changes) :
 *       name=SD, maxHeight=576, maxWidth=960, maxPixels=520000
 *       name=MIN_HD, maxHeight=720, maxWidth=1280, maxPixels=921600
 *       name=FULL_HD, maxHeight=1080, maxWidth=1920, maxPixels=2073600
 *       name=QHD, maxHeight=1440, maxWidth=2560, maxPixels=3686400
 *       name=UHD, maxHeight=2160, maxWidth=3840, maxPixels=8294400
 */
public interface CupLibrary {
    @Deprecated
    int getMaximumVideoHeight(final Set<String> cupTokens, final String deviceCategory);

    VideoResolutionType getResolutionType(int id);

    VideoResolutionType getResolutionType(int width, int height);

    VideoResolutionType getCupMaxVideoResolutionType(@Nullable final Set<String> cupTokens, @Nullable String cupDeviceCategory);
}