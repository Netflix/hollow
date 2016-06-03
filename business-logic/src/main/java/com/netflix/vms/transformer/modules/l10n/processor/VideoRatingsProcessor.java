package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoCountryRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoRatingsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class VideoRatingsProcessor extends AbstractL10NVideoProcessor<ConsolidatedVideoRatingsHollow> {

    public VideoRatingsProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper, indexer, IndexSpec.CONSOLIDATED_VIDEO_RATINGS);
    }

    @Override
    protected ConsolidatedVideoRatingsHollow getDataForOrdinal(int ordinal) {
        return api.getConsolidatedVideoRatingsHollow(ordinal);
    }

    @Override
    public void processInput(ConsolidatedVideoRatingsHollow input) {
        final int inputId = (int) input._getVideoId();

        // COMMENT COPIED FROM OLD PIPELINE
        // TODO: this will need to be made country-specific
        // Hard-coding the media type to ED is acceptable in this case for two reasons:
        //   - the upstream feed only provides us with ED ratings
        //   - downstream clients can only ask for ED ratings
        String mediaType = "ED";//MediaType.ED.toString();

        for (ConsolidatedVideoRatingHollow videoRating : input._getRatings()) {
            for (ConsolidatedVideoCountryRatingHollow countryRating : videoRating._getCountryRatings()) {
                int certificationId = (int) countryRating._getCertificationSystemId();
                final String resourceId = L10nResourceIdLookup.getMovieCertificationDescriptionID(inputId, certificationId, mediaType);
                addL10NResources(resourceId, countryRating._getReasons());
            }
        }
    }
}