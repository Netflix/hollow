package com.netflix.vms.transformer.publish.workflow.logmessage;

import com.netflix.vms.transformer.common.MessageBuilder;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import java.util.List;

public class PbmsMessage {
    private final MessageBuilder msgBuilder;

    public PbmsMessage(String msg, List<VideoCountryKey> videoCountry) {
        msgBuilder = new MessageBuilder(msg);
        StringBuilder sbuilder = new StringBuilder();
        if (videoCountry != null) {
            int indx = 0;
            for (VideoCountryKey vc : videoCountry) {
                if (indx != 0)
                    sbuilder.append(",");
                sbuilder.append("[").append(vc.getCountry()).append(", ").append(vc.getVideoId()).append("]");
                indx++;
            }
        }
        msgBuilder.put("country-videos", sbuilder.toString());
    }

    @Override
    public String toString() {
        return msgBuilder.toString();
    }
}
