package com.netflix.vms.transformer.cup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.cup.ContentUsagePolicyFactory;
import com.netflix.encodingtools.videoresolutiontypelibrary.VideoResolutionType;
import com.netflix.encodingtools.videoresolutiontypelibrary.VideoResolutionTypeFactory;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import java.util.Set;

@Singleton
public class CupLibraryImpl implements CupLibrary {

    private final VideoResolutionTypeFactory videoResTypeFactory;
    private final ContentUsagePolicyFactory cupFactory;

    @Inject
    public CupLibraryImpl(final VideoResolutionTypeFactory videoResTypeFactory, final ContentUsagePolicyFactory cupFactory) {
        this.videoResTypeFactory = videoResTypeFactory;
        this.cupFactory = cupFactory;
    }

    @Override
    public int getMaximumVideoHeight(Set<String> cupTokens, String deviceCategory) {
        return cupFactory.getCupMaximumVideoHeight(cupTokens, deviceCategory);
    }

    @Override
    public VideoResolutionType getCupMaxVideoResolutionType(Set<String> cupTokens, String cupDeviceCategory) {
        return cupFactory.getCupVideoResolutionTypeForVMS(cupTokens, cupDeviceCategory);
    }

    @Override
    public VideoResolutionType getResolutionType(int id) {
        return videoResTypeFactory.getResolutionType(id);
    }

    @Override
    public VideoResolutionType getResolutionType(int width, int height) {
        return videoResTypeFactory.getResolutionType(width, height);
    }
}
