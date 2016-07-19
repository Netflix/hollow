package com.netflix.vms.transformer.common.config;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum OutputTypeConfig {
    CompleteVideo("CompleteVideo", "id.value", "country"),
    VideoEpisode_CountryList("VideoEpisode_CountryList", "country", "item.deliverableVideo.value"),
    RolloutVideo("RolloutVideo", "video.value"),
    GlobalVideo("GlobalVideo", "completeVideo.id.value"),
    FallbackUSArtwork("FallbackUSArtwork", "id.value"),
    LanguageRights("LanguageRights", "contractId", "videoId.value"),
    VideoPackageData("VideoPackageData", "videoId.value"),

    PackageData("PackageData", "id"),
    StreamData("StreamData", "downloadableId"),
    DrmKey("DrmKey", "keyId"),
    WmDrmKey("WmDrmKey", "downloadableId"),
    DrmInfoData("DrmInfoData", "packageId"),
    DrmSystem("DrmSystem", "id"),
    EncodingProfileGroup("EncodingProfileGroup", "groupNameStr"),
    EncodingProfile("EncodingProfile", "id"),
    FileEncodingData("FileEncodingData", "downloadableId"),
    OriginServer("OriginServer", "nameStr"),
    DeploymentIntent("DeploymentIntent", "profileId", "bitrate", "country.id"),

    GlobalPerson("GlobalPerson", "id"),

    PersonImages("PersonImages", "id"),
    CharacterImages("CharacterImages", "id"),
    ArtWorkImageFormatEntry("ArtWorkImageFormatEntry", "nameStr"),
    ArtWorkImageTypeEntry("ArtWorkImageTypeEntry", "nameStr"),
    ArtWorkImageRecipe("ArtWorkImageRecipe", "recipeNameStr"),
    DefaultExtensionRecipe("DefaultExtensionRecipe", "extensionStr"),

    L10NResources("L10NResources", "resourceIdStr"),
    TopNVideoData("TopNVideoData", "countryId"),
    NamedCollectionHolder("NamedCollectionHolder", "country");

    //---------------
    public static Set<OutputTypeConfig> VIDEO_TYPES = Collections.unmodifiableSet(EnumSet.of(
            CompleteVideo, VideoEpisode_CountryList, RolloutVideo, GlobalVideo, FallbackUSArtwork, LanguageRights, VideoPackageData));

    public static Set<OutputTypeConfig> PERSON_TYPES = Collections.unmodifiableSet(EnumSet.of(
            GlobalPerson, PersonImages));

    public static Set<OutputTypeConfig> CORE_TYPES = Collections.unmodifiableSet(EnumSet.of(
            CompleteVideo, VideoEpisode_CountryList, RolloutVideo, GlobalVideo, FallbackUSArtwork, LanguageRights, VideoPackageData, PackageData, GlobalPerson));

    //---------------
    private final String type;
    private final String[] keyFieldPaths;

    OutputTypeConfig(String type, String... keyFieldPaths) {
        this.type = type;
        this.keyFieldPaths = keyFieldPaths;
    }

    public String getType() {
        return type;
    }

    public String[] getKeyFieldPaths() {
        return keyFieldPaths;
    }

    public boolean isVideoType() {
        return VIDEO_TYPES.contains(this);
    }
}