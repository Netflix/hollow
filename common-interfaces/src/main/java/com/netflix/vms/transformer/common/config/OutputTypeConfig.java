package com.netflix.vms.transformer.common.config;

import com.netflix.hollow.core.index.key.PrimaryKey;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum OutputTypeConfig {
    CompleteVideo("CompleteVideo", "id.value", "country.id"),
    MulticatalogCountryData("MulticatalogCountryData", "videoId.value", "country.id"),
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

    L10NResources("L10NResources", "resourceIdStr"),
    TopNVideoData("TopNVideoData", "countryId"),
    NamedCollectionHolder("NamedCollectionHolder", "country.id");

    //---------------
    private final static EnumSet<OutputTypeConfig> VIDEO_RELATED_TYPES_ENUMSET = EnumSet.of(
            CompleteVideo, MulticatalogCountryData, RolloutVideo, GlobalVideo, FallbackUSArtwork, LanguageRights, VideoPackageData, PackageData, L10NResources);
    public final static Set<OutputTypeConfig> VIDEO_RELATED_TYPES = Collections.unmodifiableSet(VIDEO_RELATED_TYPES_ENUMSET);
    public final static Set<OutputTypeConfig> NON_VIDEO_RELATED_TYPES = Collections.unmodifiableSet(EnumSet.complementOf(VIDEO_RELATED_TYPES_ENUMSET));

    public final static Set<OutputTypeConfig> PERSON_RELATED_TYPES = Collections.unmodifiableSet(EnumSet.of(
            GlobalPerson, PersonImages));

    public final static Set<OutputTypeConfig> TOP_LEVEL_NON_VIDEO_TYPES = Collections.unmodifiableSet(EnumSet.of(
            L10NResources,
            GlobalPerson,
            PersonImages, CharacterImages,
            DrmInfoData, DrmSystem, OriginServer, EncodingProfile, EncodingProfileGroup, FileEncodingData, DeploymentIntent));

    public final static Set<OutputTypeConfig> REFERENCED_TYPES = Collections.unmodifiableSet(EnumSet.of(
            ArtWorkImageFormatEntry, ArtWorkImageTypeEntry, ArtWorkImageRecipe,
            StreamData, DrmKey, WmDrmKey));

    public final static Set<OutputTypeConfig> FASTLANE_EXCLUDED_TYPES = Collections.unmodifiableSet(EnumSet.of(
            GlobalPerson, PersonImages, CharacterImages,
            LanguageRights, TopNVideoData));

    //---------------
    private final PrimaryKey primaryKey;
    private final String[] keyFieldPaths;

    OutputTypeConfig(String type, String... keyFieldPaths) {
        this.primaryKey = new PrimaryKey(type, keyFieldPaths);
        this.keyFieldPaths = keyFieldPaths;
    }

    public String getType() {
        return primaryKey.getType();
    }

    public String[] getKeyFieldPaths() {
        return keyFieldPaths;
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }
}