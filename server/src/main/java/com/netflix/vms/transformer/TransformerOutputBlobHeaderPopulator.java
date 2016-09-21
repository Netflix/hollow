package com.netflix.vms.transformer;

import com.netflix.hollow.client.HollowClient;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.publish.workflow.job.impl.BlobMetaDataUtil;
import java.util.Map;

public class TransformerOutputBlobHeaderPopulator {
    
    private final HollowClient inputClient;
    private final HollowWriteStateEngine outputStateEngine;
    private final TransformerContext ctx;
    
    public TransformerOutputBlobHeaderPopulator(HollowClient inputClient, HollowWriteStateEngine outputStateEngine, TransformerContext ctx) {
        this.inputClient = inputClient;
        this.outputStateEngine = outputStateEngine;
        this.ctx = ctx;
    }
    

    public void addHeaders(long previousCycleNumber, long currentCycleNumber) {
        
        outputStateEngine.addHeaderTag("sourceDataVersion", String.valueOf(inputClient.getCurrentVersionId()));
        outputStateEngine.addHeaderTag("publishCycleDataTS", String.valueOf(ctx.getNowMillis()));
        outputStateEngine.addHeaderTag("awsAmiId", ctx.getConfig().getAwsAmiId());
        outputStateEngine.addHeaderTags(BlobMetaDataUtil.getPublisherProps(ctx.getConfig().getTransformerVip(), System.currentTimeMillis(), String.valueOf(currentCycleNumber), previousCycleNumber == Long.MIN_VALUE ? "" : String.valueOf(previousCycleNumber)));
        
        /// input versions
        Map<String, String> inputHeaderTags = inputClient.getStateEngine().getHeaderTags();
        for(Map.Entry<String, String> entry : inputHeaderTags.entrySet()) {
            if(entry.getKey().endsWith("_coldstart")) {
                String mutationGroup = entry.getKey().substring(0, entry.getKey().indexOf("_coldstart"));
                String latestColdstartVersion = entry.getValue();
                String latestEventId = inputHeaderTags.get(mutationGroup + "_events");

                outputStateEngine.addHeaderTag("input:" + mutationGroup + "_ColdStartManager", "version:" + latestColdstartVersion);
                outputStateEngine.addHeaderTag("input:" + mutationGroup + "_MutationEventsFetcher", "version:" + latestEventId);
                outputStateEngine.addHeaderTag(mutationGroup + ".lastReadMessageId", latestEventId);
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
                                + "SOURCE_MOVIE_ID:source_movie_id(PassthroughVideo).id(INT)\n"
                                + "IDENTIFIERS:basic_passthrough(ArtworkBasicPassthrough).identifiers(STRING[])\n"
                                + "ACQUISITION_SOURCE:acquisitionSource(AcquisitionSource).value(STRING)\n"
                        );
    }

}
