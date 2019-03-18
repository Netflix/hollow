package com.netflix.vms.transformer.publish.workflow.logmessage;

import com.netflix.vms.transformer.common.MessageBuilder;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import java.util.List;

public class PbmsMessage {
    private final MessageBuilder msgBuilder;

    public PbmsMessage(boolean success, String msg) {
        this(success, msg, null);
    }

    public PbmsMessage(boolean success, String msg, List<VideoCountryKey> videoCountry) {
        msgBuilder = success ? new MessageBuilder("PBM validation SUCCESS, " + msg) : new MessageBuilder("PBM validation FAIL, " + msg);

        if (videoCountry != null) {
            StringBuilder sbuilder = new StringBuilder(", FailedIds={");
            int indx = 0;
            for (VideoCountryKey vc : videoCountry) {
                if (indx != 0)
                    sbuilder.append(",");
                sbuilder.append("[").append(vc.getCountry()).append(", ").append(vc.getVideoId()).append("]");
                indx++;
            }
            sbuilder.append("}");
            msgBuilder.put("country-videos", sbuilder.toString());
            msgBuilder.put("id-count", videoCountry.size());
        }
    }

    @Override
    public String toString() {
        return msgBuilder.toString();
    }
}
