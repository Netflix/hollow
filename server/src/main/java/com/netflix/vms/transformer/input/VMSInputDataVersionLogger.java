package com.netflix.vms.transformer.input;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.InputDataConverterVersionId;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.InputDataVersionIds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.TransformerContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class VMSInputDataVersionLogger {
    private static final String CONVERTER_VIP_PREFIX_FOR_HOLLOW_CONSUMER = "vmsconverter-";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

    public static void logInputVersions(
            HollowConsumer inputConsumer, HollowConsumer gk2StatusConsumer,
            TransformerContext ctx) {

        Map<String, Long> inputs = new HashMap<>();
        inputs.put(CONVERTER_VIP_PREFIX_FOR_HOLLOW_CONSUMER + ctx.getConfig().getConverterVip(),
                inputConsumer.getCurrentVersionId());
        if (gk2StatusConsumer != null) {
            inputs.put(ctx.getConfig().getGatekeeper2Namespace(), gk2StatusConsumer.getCurrentVersionId());
        }
        String intputsLogLine = inputs.entrySet().stream()
                .map(Map.Entry::toString)
                .collect(Collectors.joining(" "));
        ctx.getLogger().info(InputDataConverterVersionId, intputsLogLine);
    }

    public static void logConverterInputVersions(Map<String, String> inputBlobHeaders, TaggingLogger logger) {
        for (Map.Entry<String, String> entry : inputBlobHeaders.entrySet()) {
            if (entry.getKey().endsWith("_coldstart")) {

                String inputName = entry.getKey().substring(0, entry.getKey().indexOf("_coldstart"));
                String latestCombinedVersion = entry.getValue();

                String inputType = inputBlobHeaders.get(inputName + "_coldstartType");
                String coldstartKeybase = inputBlobHeaders.get(inputName + "_coldstartKeybase");
                String coldstartFilename = inputBlobHeaders.get(inputName + "_coldstartFile") != null
                        ? inputBlobHeaders.get(inputName + "_coldstartFile")
                        : "null";
                String formattedPublishDate = "n/a";
                if (inputBlobHeaders.get(inputName + "_coldstartFilePublishTime") != null) {
                    long publishTime = Long.valueOf(inputBlobHeaders.get(inputName + "_coldstartFilePublishTime"));
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date publishDate = new Date(publishTime);
                    formattedPublishDate = dateFormat.format(publishDate);
                }
                String latestEventId = inputBlobHeaders.get(inputName + "_eventsCheckpoints");
                String checkpoint = inputBlobHeaders.get(inputName + "_eventsLatest");

                StringBuilder sb = new StringBuilder();
                sb.append("Input=").append(inputName)
                        .append(" Keybase=").append(coldstartKeybase)
                        .append(" Type=").append(inputType)
                        .append(" Version=").append(latestCombinedVersion)
                        .append(" EventId=").append(latestEventId)
                        .append(" EventCheckpoint=").append(checkpoint)
                        .append(" FileName=").append(coldstartFilename)
                        .append(" PublishTime=").append(formattedPublishDate);

                logger.info(InputDataVersionIds, sb.toString());
            }
        }
    }
}
