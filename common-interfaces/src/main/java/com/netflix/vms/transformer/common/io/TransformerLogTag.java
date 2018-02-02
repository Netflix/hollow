package com.netflix.vms.transformer.common.io;

import com.netflix.vms.logging.TaggingLogger.LogTag;

public enum TransformerLogTag implements LogTag {
    WaitForNextCycle,
    TransformCycleBegin,
    TransformCycleSuccess,
    TransformCycleFailed,
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
    CreateDevSlice,

    //// Debug /////
    ReexploreTags,

    /// Multi-Locale Catalog ///
    PrePromotion,
    LocaleMerching,
    LocaleMerchingMissingSubs,
    LocaleMerchingMissingDubs,
    MultiLocaleCountries,
}
