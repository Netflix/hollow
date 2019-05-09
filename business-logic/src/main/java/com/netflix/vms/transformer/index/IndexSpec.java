package com.netflix.vms.transformer.index;

import static com.netflix.vms.transformer.index.IndexSpec.IndexType.HASH;
import static com.netflix.vms.transformer.index.IndexSpec.IndexType.PRIMARY_KEY;

public enum IndexSpec {

    SUPPLEMENTAL(PRIMARY_KEY, "Supplementals", "movieId"),
    VIDEO_STATUS(PRIMARY_KEY, "Status", "movieId", "countryCode.value"),
    VIDEO_TYPE(PRIMARY_KEY, "VideoType", "videoId"),
    VIDEO_GENERAL(PRIMARY_KEY, "VideoGeneral", "videoId"),
    STORAGE_GROUPS(PRIMARY_KEY, "StorageGroups", "id.value"),
    CDNS(PRIMARY_KEY, "Cdn", "id"),
    PROTECTION_TYPES(PRIMARY_KEY, "ProtectionTypes", "id"),
    STREAM_PROFILE(PRIMARY_KEY, "StreamProfiles", "id"),
    STREAM_PROFILE_GROUP(PRIMARY_KEY, "StreamProfileGroups", "groupName.value"),
    VIDEO_AWARD(PRIMARY_KEY, "VideoAward", "videoId"),
    VMS_AWARD(PRIMARY_KEY, "VMSAward", "awardId"),
    CSM_REVIEW(PRIMARY_KEY, "CSMReview", "videoId"),
    ARTWORK_IMAGE_FORMAT(PRIMARY_KEY, "ArtWorkImageType", "imageType.value"),
    ARTWORK_RECIPE(PRIMARY_KEY, "ArtworkRecipe", "recipeName.value"),
    ARTWORK_TERRITORY_COUNTRIES(PRIMARY_KEY, "TerritoryCountries", "territoryCode.value"),
    CONSOLIDATED_VIDEO_RATINGS(PRIMARY_KEY, "ConsolidatedVideoRatings", "videoId"),
    CONSOLIDATED_CERT_SYSTEMS(PRIMARY_KEY, "ConsolidatedCertificationSystems", "certificationSystemId"),
    CERT_SYSTEM_RATING(PRIMARY_KEY, "ConsolidatedCertSystemRating", "ratingId"),
    PACKAGES(PRIMARY_KEY, "Package", "packageId"),
    DAM_MERCHSTILLS(PRIMARY_KEY, "DamMerchStills", "assetId.value"),
    PERSON_BIO(PRIMARY_KEY, "PersonBio", "personId"),
    MOVIE_CHARACTER_PERSON(PRIMARY_KEY, "MovieCharacterPerson", "movieId"),

    VIDEO_ARTWORK_SOURCE_BY_SOURCE_ID(PRIMARY_KEY, "VideoArtworkSource", "sourceFileId"),
    PERSON_ARTWORK_SOURCE_BY_SOURCE_ID(PRIMARY_KEY, "PersonArtworkSource", "sourceFileId"),
    CHARACTER_ARTWORK_SOURCE_BY_SOURCE_ID(PRIMARY_KEY, "CharacterArtworkSource", "sourceFileId"),
    
    L10N_STORIES_SYNOPSES(PRIMARY_KEY, "StoriesSynopses", "movieId"),
    L10N_MOVIES(PRIMARY_KEY, "Movies", "movieId"),
    L10N_EPISODES(PRIMARY_KEY, "Episodes", "movieId"),
    L10N_LOCALIZEDMETADATA_BY_VIDEO(HASH, "LocalizedMetadata", "", "movieId"),

    VIDEO_ARTWORK_SOURCE_BY_VIDEO_ID(HASH, "VideoArtworkSource", "", "movieId"),
    ARTWORK_DERIVATIVE_SETS(HASH, "IPLDerivativeGroup", "", "externalId.value"),
    SHOW_SEASON_EPISODE(HASH, "ShowSeasonEpisode", "", "movieId"),
    VIDEO_DATE(HASH, "VideoDate", "window.element", "videoId", "window.element.countryCode.value"),
    PERSONS_BY_VIDEO_ID(HASH, "PersonVideo", "", "roles.element.videoId"),
    PERSON_ROLES_BY_VIDEO_ID(HASH, "PersonVideo", "roles.element", "personId", "roles.element.videoId"),
    VIDEO_TYPE_COUNTRY(HASH, "VideoType", "countryInfos.element", "videoId", "countryInfos.element.countryCode.value"),
    PACKAGES_BY_VIDEO(HASH, "Package", "", "movieId"),
    ALL_VIDEO_STATUS(HASH, "Status", "", "movieId"),
    ROLLOUT_VIDEO_TYPE(HASH, "Rollout", "", "movieId", "rolloutType.value"),
    SHOW_COUNTRY_LABEL(HASH, "ShowCountryLabel", "showMemberTypes.element", "videoId", "showMemberTypes.element.countryCodes.element.value"),
    MOVIE_CHARACTER_PERSON_MOVIES_BY_PERSON_ID(HASH, "MovieCharacterPerson", "", "characters.element.personId"),
    MOVIE_CHARACTER_PERSON_CHARACTERS_BY_PERSON_ID_AND_MOVIE_ID(HASH, "MovieCharacterPerson", "characters.element", "characters.element.personId", "movieId"),

    //Image Schedules
    OVERRIDE_SCHEDULE_BY_VIDEO_ID(HASH, "OverrideSchedule", "", "movieId", "phaseTag.value"),
    MASTER_SCHEDULE_BY_TAG_SHOW(HASH, "MasterSchedule", "", "phaseTag.value", "scheduleId.value"),
    ABSOLUTE_SCHEDULE_BY_VIDEO_ID_TAG(HASH, "AbsoluteSchedule", "", "movieId", "phaseTag.value"),
    

    
    // Merch-language-dates
    MERCH_LANGUAGE_DATE(PRIMARY_KEY, "FeedMovieCountryLanguages", "movieId.value", "countryCode.value"),
    CUP_TOKEN_HINDEX(HASH, "CinderCupTokenRecord", "", "movieId.value", "dealId.value"),    
    CUP_TOKEN_PINDEX(PRIMARY_KEY, "CinderCupTokenRecord", "movieId.value", "dealId.value"),
    PACKAGE_MOVIE_DEAL_COUNTRY_GROUP(PRIMARY_KEY, "PackageMovieDealCountryGroup", "movieId.value", "packageId.value"),
	VMS_ATTRIBUTE_FEED_ENTRY(PRIMARY_KEY, "VmsAttributeFeedEntry", "movieId.value", "dealId.value", "countryCode.value");

    private final IndexType indexType;
    private final String parameters[];

    IndexSpec(IndexType indexType, String... parameters) {
        this.indexType = indexType;
        this.parameters = parameters;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public String[] getParameters() {
        return parameters;
    }

    public enum IndexType {
        PRIMARY_KEY,
        HASH
    }

}
