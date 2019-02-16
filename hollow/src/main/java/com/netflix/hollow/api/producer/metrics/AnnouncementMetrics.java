package com.netflix.hollow.api.producer.metrics;

import java.util.OptionalLong;

public class AnnouncementMetrics {

    private long dataSizeBytes;                                     // Heap footprint of announced blob in bytes
    private long announcementDurationMillis;                        // Announcement duration in ms, only applicable to completed cycles (skipped cycles dont announce)
    private boolean isAnnouncementSuccess;                          // true if announcement was successful, false if announcement failed
    private OptionalLong lastAnnouncementSuccessTimeNanoOptional;   // monotonic time of last successful announcement (no relation to wall clock), N/A until first successful announcement


    public long getDataSizeBytes() {
        return dataSizeBytes;
    }
    public long getAnnouncementDurationMillis() {
        return announcementDurationMillis;
    }
    public boolean getIsAnnouncementSuccess() {
        return isAnnouncementSuccess;
    }
    public OptionalLong getLastAnnouncementSuccessTimeNanoOptional() {
        return lastAnnouncementSuccessTimeNanoOptional;
    }

    private AnnouncementMetrics(Builder builder) {
        this.dataSizeBytes = builder.dataSizeBytes;
        this.announcementDurationMillis = builder.announcementDurationMillis;
        this.isAnnouncementSuccess = builder.isAnnouncementSuccess;
        this.lastAnnouncementSuccessTimeNanoOptional = builder.lastAnnouncementSuccessTimeNanoOptional;
    }

    public static final class Builder {
        private long dataSizeBytes;
        private long announcementDurationMillis;
        private boolean isAnnouncementSuccess;
        private OptionalLong lastAnnouncementSuccessTimeNanoOptional;

        public Builder() {
            lastAnnouncementSuccessTimeNanoOptional = OptionalLong.empty();
        }

        public Builder setDataSizeBytes(long dataSizeBytes) {
            this.dataSizeBytes = dataSizeBytes;
            return this;
        }
        public Builder setAnnouncementDurationMillis(long announcementDurationMillis) {
            this.announcementDurationMillis = announcementDurationMillis;
            return this;
        }
        public Builder setIsAnnouncementSuccess(boolean isAnnouncementSuccess) {
            this.isAnnouncementSuccess = isAnnouncementSuccess;
            return this;
        }
        public Builder setLastAnnouncementSuccessTimeNanoOptional(long lastAnnouncementSuccessTimeNano) {
            this.lastAnnouncementSuccessTimeNanoOptional = OptionalLong.of(lastAnnouncementSuccessTimeNano);
            return this;
        }

        public AnnouncementMetrics build() {
            return new AnnouncementMetrics(this);
        }
    }
}
