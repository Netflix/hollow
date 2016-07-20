package com.netflix.vms.transformer.common.config;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum OutputTypeConfig {
    CompleteVideo("CompleteVideo", "id.value", "country.id"),
    VideoEpisode_CountryList("VideoEpisode_CountryList", "country.id", "item.deliverableVideo.value"),
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
    NamedCollectionHolder("NamedCollectionHolder", "country.id");

    //---------------
    public final static Set<OutputTypeConfig> VIDEO_RELATED_TYPES = Collections.unmodifiableSet(EnumSet.of(
            CompleteVideo, VideoEpisode_CountryList, RolloutVideo, GlobalVideo, FallbackUSArtwork, LanguageRights, VideoPackageData, PackageData));

    public final static Set<OutputTypeConfig> PERSON_RELATED_TYPES = Collections.unmodifiableSet(EnumSet.of(
            GlobalPerson, PersonImages));

    public final static Set<OutputTypeConfig> TOP_LEVEL_NON_VIDEO_TYPES = Collections.unmodifiableSet(EnumSet.of(
            L10NResources,
            GlobalPerson,
            PersonImages, CharacterImages,
            DrmInfoData, DrmSystem, OriginServer, EncodingProfile, EncodingProfileGroup, FileEncodingData, DeploymentIntent));

    public final static Set<OutputTypeConfig> REFERENCED_TYPES = Collections.unmodifiableSet(EnumSet.of(
            ArtWorkImageFormatEntry, ArtWorkImageTypeEntry, ArtWorkImageRecipe, DefaultExtensionRecipe,
            StreamData, DrmKey, WmDrmKey));

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
}