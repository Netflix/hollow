package com.netflix.vms.transformer.index;

import static com.netflix.vms.transformer.index.IndexSpec.IndexType.HASH;
import static com.netflix.vms.transformer.index.IndexSpec.IndexType.PRIMARY_KEY;

public enum IndexSpec {

    SUPPLEMENTAL(PRIMARY_KEY, "Trailer", "movieId"),
    VIDEO_RIGHTS(PRIMARY_KEY, "VideoRights", "movieId", "countryCode.value"),
    ROLLOUT_VIDEO_TYPE(PRIMARY_KEY, "Rollout", "movieId", "rolloutType.value"),
    VIDEO_TYPE(PRIMARY_KEY, "VideoType", "videoId"),
    VIDEO_GENERAL(PRIMARY_KEY, "VideoGeneral", "videoId"),
    STORIES_SYNOPSES(PRIMARY_KEY, "Stories_Synopses", "movieId"),


    VIDEO_DATE(HASH, "VideoDate", "window.element", "videoId", "window.element.countryCode.value"),
    PERSONS_BY_VIDEO_ID(HASH, "VideoPerson", "", "cast.element.videoId"),
    PERSON_ROLES_BY_VIDEO_ID(HASH, "VideoPerson", "cast.element", "personId", "cast.element.videoId"),
    VIDEO_TYPE_COUNTRY(HASH, "VideoType", "type.element", "videoId", "type.element.countryCode.value");


    private final IndexType indexType;
    private final String parameters[];

    private IndexSpec(IndexType indexType, String... parameters) {
        this.indexType = indexType;
        this.parameters = parameters;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public String[] getParameters() {
        return parameters;
    }

    public static enum IndexType {
        PRIMARY_KEY,
        HASH
    }

}
