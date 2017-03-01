package com.netflix.vms.transformer.publish.workflow.logmessage;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.MessageBuilder;

public class PublishBlobMessage {
    private MessageBuilder msgBuilder = new MessageBuilder("Uploaded VMS Blob: ");

    public PublishBlobMessage(String keybase, RegionEnum region, long version, long dataVersion, long size, long duration) {
        msgBuilder.put("keybase", keybase);
        msgBuilder.put("region", region.toString());
        msgBuilder.put("version", version);
        msgBuilder.put("dataVersion", dataVersion);
        msgBuilder.put("size", size);
        msgBuilder.put("duration", duration + "ms");
    }

    @Override
    public String toString() {
        return msgBuilder.toString();
    }
}
