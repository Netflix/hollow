package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.publish.workflow.VipAnnouncer;
import java.util.Map;

public class HermesVipAnnouncer implements VipAnnouncer {

    /* dependencies */
    private final HermesBlobAnnouncer hermesAnnouncer;

    /* fields */
    private long previouslyAnnouncedCanaryVersion = Long.MAX_VALUE;
    private long currentlyAnnouncedCanaryVersion = Long.MAX_VALUE;

    public HermesVipAnnouncer(HermesBlobAnnouncer hermesAnnouncer) {
        this.hermesAnnouncer = hermesAnnouncer;
    }

    @Override
    public boolean announce(String vip, RegionEnum region, boolean canary, long announceVersion) {
        return announce(vip, region, canary, announceVersion, Long.MIN_VALUE);
    }

    @Override
    public boolean announce(String vip, RegionEnum region, boolean canary, long announceVersion, long priorVersion) {
        boolean success = false;
        try {
            success = hermesAnnouncer.publish(region, vip, canary, getAttributes(vip, announceVersion, priorVersion));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(success && !canary) {
            //PublishEventRecorder eventRecorder = VMSInjectionManager.get().getPinEventRecorder();
            //eventRecorder.postAnnounceEvent(String.valueOf(announceVersion), region, hermesTopic);
        }

        if(canary) {
            previouslyAnnouncedCanaryVersion = currentlyAnnouncedCanaryVersion;
            currentlyAnnouncedCanaryVersion = announceVersion;
        }

        return success;
    }

    private Map<String, String> getAttributes(String vip, long cycleVersionId, long priorVersion) {
        String currentVersionStr = String.valueOf(cycleVersionId);
        String priorVersionStr = (priorVersion != Long.MIN_VALUE || priorVersion != cycleVersionId)? String.valueOf(priorVersion) : "";
        return BlobMetaDataUtil.getPublisherProps(vip, System.currentTimeMillis(), currentVersionStr, priorVersionStr);
    }

    @Override
    public long getPreviouslyAnnouncedCanaryVersion(String vip) {
        if(previouslyAnnouncedCanaryVersion == Long.MIN_VALUE || previouslyAnnouncedCanaryVersion == Long.MAX_VALUE)
            initializePreviouslyAnnouncedCanaryVersion(vip);
        //LOGGER.logf(ErrorCode.VMSHermesAnnounceDebug, "Returning %s as previously announced canary version.", currentlyAnnouncedCanaryVersion);
        return previouslyAnnouncedCanaryVersion;
    }

    private void initializePreviouslyAnnouncedCanaryVersion(String vip) {
        previouslyAnnouncedCanaryVersion = hermesAnnouncer.getLatestAnnouncedVersionFromCassandra(vip);
    }

    @JsonDeserialize(builder=HermesAnnounceEvent.Builder.class)
    public static class HermesAnnounceEvent {
        private final Map<String, String> attributes;
        private final FastBlobType fastBlobImageType;

        private HermesAnnounceEvent(Map<String, String> attributes, FastBlobType type) {
            this.attributes = attributes;
            this.fastBlobImageType = type;
        }

        public  Map<String, String> getAttributes() {
            return attributes;
        }

        public FastBlobType getFastBlobImageType() {
            return fastBlobImageType;
        }

        @JsonIgnore
        public String getVersion() {
            return attributes.get("dataVersion");
        }

        public static class Builder {
            private Map<String, String> attributes;
            private FastBlobType fastBlobImageType;

            public Builder withAttributes(Map<String, String> attributes) {
                this.attributes = attributes;
                return this;
            }

            public Builder withFastBlobImageType(FastBlobType type) {
                this.fastBlobImageType = type;
                return this;
            }

            public HermesAnnounceEvent build() {
                return new HermesAnnounceEvent(attributes, fastBlobImageType);
            }
        }

        private static enum FastBlobType {
            SNAPSHOT, DELTA, REVERSEDELTA, STATE;


        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((attributes == null) ? 0 : attributes.hashCode());
            result = prime * result + ((fastBlobImageType == null) ? 0 : fastBlobImageType.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            HermesAnnounceEvent other = (HermesAnnounceEvent) obj;
            if (attributes == null) {
                if (other.attributes != null)
                    return false;
            } else if (!attributes.equals(other.attributes))
                return false;
            if (fastBlobImageType != other.fastBlobImageType)
                return false;
            return true;
        }
    }
}
