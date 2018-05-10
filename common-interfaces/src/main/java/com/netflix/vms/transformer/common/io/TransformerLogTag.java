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
    TransformRestore,
    TransformDuration,
    FreezeConfig,
    CycleFastlaneIds,
    TransformProgress,
    TransformInfo,
    InputDataVersionIds,
    FollowVip,
    InputDataConverterVersionId,
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
    StateEngineCompaction,
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

    //// Debug /////
    ReexploreTags,

    /// Multi-Locale Catalog ///
    Language_catalog_PrePromote,// title is in pre-promotion phase
    Language_catalog_Skip_Contract_No_Assets,// title skipped contract since no localized assets were available
    Language_catalog_Missing_Dubs,// title missing localized dubs
    Language_catalog_Missing_Subs,// title missing localized subs
    Language_catalog_NoWindows,// title has no locale aware windows
    Language_catalog_NoAssetRights,// title did not have asset rights for the locale
    Language_catalog_WindowFiltered,// title has asset rights but in future so window filtered.

    Language_catalog_diff_prePromo,// titles that will get pre-promoted now, (compared to dropping) or will have availability windows.
    Language_catalog_diff_early_promotion,// titles get promoted early

    MultiLocaleCountries,
    Catalog_Size,
}
