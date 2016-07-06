package com.netflix.vms.transformer.common.io;

import com.netflix.vms.io.TaggingLogger.LogTag;

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

    //// TRANSFORMATION ERRORS ////

    IndividualTransformFailed,
    UnknownArtworkImageType,
    MissingLocaleForArtwork,
    InvalidImagesTerritoryCode,
    LanguageRightsError,

    //// TRANSFORMATION MISC ////

    L10NProcessing,


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
    BlobChecksum
}
