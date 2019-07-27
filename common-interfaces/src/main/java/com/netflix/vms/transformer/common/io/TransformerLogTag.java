package com.netflix.vms.transformer.common.io;

import com.netflix.vms.logging.TaggingLogger.LogTag;

public enum TransformerLogTag implements LogTag {
    WaitForNextCycle,
    TransformCycleBegin,
    TransformCyclePaused,
    TransformCycleResumed,
    TransformCycleSuccess,
    TransformCycleMonkey,
    TransformCycleFailed,
    DynamicLogicLoading,
    TransformRestore,
    TransformDuration,
    FreezeConfig,
    CycleFastlaneIds,
    TransformProgress,
    TransformInfo,
    InputDataVersionIds,
    FollowVip,
    CinderInputDataVersions,
    ProcessNowMillis,
    PropertyValue,
    WroteBlob,
    WritingBlobsFailed,
    NonVideoSpecificTransformDuration,
    ConfigurationFailure,
    UnexpectedError,
    RollbackStateEngine,
    CyclePinnedTitles,
    CycleInterrupted,
    HideCycleFromDashboard,
    DroppedTopNodeOnFloor,
    ArtworkFallbackMissing,
    VideoFormatMismatch_downloadableIds,
    VideoFormatMismatch_downloadableIds_total,
    VideoFormatMismatch_4K,
    VideoFormatMismatch_encodingProfileIds,
    VideoFormatMismatch_videoIds,
    VideoFormatMismatch_videoIds_missingFormat,

    SupplementalSeasonSeqNumConflict,


    //// TRANSFORMATION ERRORS ////

    IndividualTransformFailed,
    UnknownArtworkImageType,
    MissingLocaleForArtwork,
    InvalidImagesTerritoryCode,
    LanguageRightsError,
    InvalidPhaseTagForArtwork,
    MissingRolloutForArtwork,

    //// TRANSFORMATION MISC ////

    L10NProcessing,
    InteractivePackage,

    //// PUBLISH WORKFLOW ////

    CircuitBreaker,
    PublishedBlob,
    PlaybackMonkey,
    PlaybackMonkeyTestVideo,
    PlaybackMonkeyWarn,
    DataCanary,
    MarkedPoisonState,
    ObservedPoisonState,
    AnnouncementSuccess,
    AnnouncementFailure,
    BlobChecksum,
    BlobState,
    CreateDevSlice,
    DeletedTmpFile,
    PublishWorkflowFailed,

    //// Debug /////
    ReexploreTags,

    /// Multi-Locale Catalog ///
    Language_catalog_PrePromote,// title is in pre-promotion phase
    Language_catalog_Skip_Contract_No_Assets,// title skipped contract since no localized assets were available

    Language_catalog_NoWindows,// title has no locale aware windows
    Language_catalog_NoAssetRights,// title did not have asset rights for the locale
    Language_catalog_WindowFiltered,// title has asset rights but in future so window filtered.
    Language_Catalog_Grandfather,// titles that are grandfathered (merched with no localized assets)

    Language_Catalog_Title_Availability,// titles that do not meet the localized asset requirements - FOR REPORTING

    MultiLocaleCountries,
    Catalog_Size,
}
