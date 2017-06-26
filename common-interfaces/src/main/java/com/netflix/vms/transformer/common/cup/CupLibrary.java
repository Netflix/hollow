package com.netflix.vms.transformer.common.cup;

import com.netflix.encodingtools.videoresolutiontypelibrary.VideoResolutionType;
import java.util.Set;
import javax.annotation.Nullable;

public interface CupLibrary {
    public VideoResolutionType getResolutionType(int id);

    public VideoResolutionType getResolutionType(int width, int height);

    VideoResolutionType getCupMaxVideoResolutionType(@Nullable final Set<String> cupTokens, @Nullable String cupDeviceCategory);
}