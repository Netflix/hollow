package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetHolder.Dataset.CONVERTER;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.input.CycleInputs;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDatasetHolder.UpstreamDatasetConfig;
import com.netflix.vms.transformer.publish.workflow.job.impl.BlobMetaDataUtil;
import java.util.Map;
import java.util.Objects;

public class TransformerOutputBlobHeaderPopulator {
    private final TransformerContext ctx;
    
    public TransformerOutputBlobHeaderPopulator(TransformerContext ctx) {
        this.ctx = ctx;
    }
    

    public Map<String, String> addHeaders(CycleInputs cycleInputs, HollowWriteStateEngine outputStateEngine, long previousCycleNumber, long currentCycleNumber) {

        // input version header tags for all inputs
        cycleInputs.getInputs().forEach(
                (k, v) -> outputStateEngine.addHeaderTag(UpstreamDatasetConfig.getInputVersionAttribute(k), String.valueOf(v.getVersion())));
        InputState converterInput = cycleInputs.getInputs().get(CONVERTER);
        outputStateEngine.addHeaderTag("sourceDataVersion", String.valueOf(converterInput.getVersion()));    // for backwards compatibility, indicates converter input version

        // custom input header tags
        outputStateEngine.addHeaderTag("publishCycleDataTS", String.valueOf(ctx.getNowMillis()));
        outputStateEngine.addHeaderTag("awsAmiId", ctx.getConfig().getAwsAmiId());
        outputStateEngine.addHeaderTags(BlobMetaDataUtil.getPublisherProps(ctx.getConfig().getTransformerVip(), System.currentTimeMillis(), String.valueOf(currentCycleNumber), previousCycleNumber == Long.MIN_VALUE ? "" : String.valueOf(previousCycleNumber)));
        
        // header tags for converter inputs' versions
        Map<String, String> inputHeaderTags = converterInput.getStateEngine().getHeaderTags();
        for(Map.Entry<String, String> entry : inputHeaderTags.entrySet()) {
            if(entry.getKey().endsWith("_coldstart")) {
                String mutationGroup = entry.getKey().substring(0, entry.getKey().indexOf("_coldstart"));
                String latestColdstartVersion = entry.getValue();
                String latestEventId = inputHeaderTags.get(mutationGroup + "_events");

                outputStateEngine.addHeaderTag("input:" + mutationGroup + "_ColdStartManager", "version:" + latestColdstartVersion);
                outputStateEngine.addHeaderTag("input:" + mutationGroup + "_MutationEventsFetcher", "version:" + latestEventId);
                outputStateEngine.addHeaderTag(mutationGroup + ".lastReadMessageId", Objects.toString(latestEventId, ""));

                for(String k : new String[]{
                    "coldstartFile",
                    "coldstartFilePublishTime",
                    "coldstartKeybase",
                    "eventsBackend",
                    "eventsCheckpoints",
                    "eventsLatest"
                  }) {
                  String v = inputHeaderTags.get(mutationGroup + "_" + k);
                  outputStateEngine.addHeaderTag("input:" + mutationGroup + "_" + k, Objects.toString(v, ""));
                }
            }
        }

        
        /// the Artwork Passthrough attribute necessary to make this work on the client
        /// TODO: This currently needs to be updated when attributes are added, in order to make the client work.
        outputStateEngine.addHeaderTag("PASSTHROUGH_ArtWorkDescriptor",
                        "file_seq:file_seq(INT)\n"
                                + "source_file_id:source(ArtworkSourcePassthrough).source_file_id(ArtworkSourceString).value(STRING)\n"
                                + "original_source_file_id:source(ArtworkSourcePassthrough).original_source_file_id(ArtworkSourceString).value(STRING)\n"
                                + "designAttribute:basic_passthrough(ArtworkBasicPassthrough).design_attribute(PassthroughString).value(STRING)\n"
                                + "TONE:basic_passthrough(ArtworkBasicPassthrough).tone(PassthroughString).value(STRING)\n"
                                + "APPROVAL_STATE:basic_passthrough(ArtworkBasicPassthrough).approval_state(BOOLEAN)\n"
                                + "APPROVAL_SOURCE:basic_passthrough(ArtworkBasicPassthrough).approval_source(PassthroughString).value(STRING)\n"
                                + "FOCAL_POINT:basic_passthrough(ArtworkBasicPassthrough).focal_point(PassthroughString).value(STRING)\n"
                                + "GROUP_ID:basic_passthrough(ArtworkBasicPassthrough).group_id(PassthroughString).value(STRING)\n"
                                + "themes:basic_passthrough(ArtworkBasicPassthrough).themes(STRING[])\n"
                                + "AWARD_CAMPAIGNS:basic_passthrough(ArtworkBasicPassthrough).awardCampaigns(STRING[])\n"
                                + "other_list_passthrough:basic_passthrough(ArtworkBasicPassthrough).lists(BasicPassthroughLists).floatList(FLOAT[SuperFloat])\n"
                                + "PERSON_IDS:basic_passthrough(ArtworkBasicPassthrough).personIdStrs(STRING[])\n"
                                + "DESIGN_EFFORT:basic_passthrough(ArtworkBasicPassthrough).design_effort(PassthroughString).value(STRING)\n"
                                + "BRANDING_ALIGNMENT:basic_passthrough(ArtworkBasicPassthrough).branding_alignment(PassthroughString).value(STRING)\n"
                                + "SOURCE_MOVIE_ID:source_movie_id(PassthroughVideo).id(INT)\n"
                                + "IDENTIFIERS:basic_passthrough(ArtworkBasicPassthrough).identifiers(STRING[])\n"
                                + "ACQUISITION_SOURCE:acquisitionSource(AcquisitionSource).value(STRING)\n"
                                + "UNBRANDED:basic_passthrough(ArtworkBasicPassthrough).unbranded(BOOLEAN)\n"
                                + "SCREENSAVER_START_X:basic_passthrough(ArtworkBasicPassthrough).screensaverPassthrough(ArtworkScreensaverPassthrough).startX(INT)\n"
                                + "SCREENSAVER_END_X:basic_passthrough(ArtworkBasicPassthrough).screensaverPassthrough(ArtworkScreensaverPassthrough).endX(INT)\n"
                                + "SCREENSAVER_OFFSET_Y:basic_passthrough(ArtworkBasicPassthrough).screensaverPassthrough(ArtworkScreensaverPassthrough).offsetY(INT)\n"
                        );

        return outputStateEngine.getHeaderTags();
    }
}