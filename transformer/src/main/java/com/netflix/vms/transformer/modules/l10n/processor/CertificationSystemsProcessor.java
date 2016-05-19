package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ConsolidatedCertSystemRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedCertificationSystemsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class CertificationSystemsProcessor extends AbstractL10NProcessor<ConsolidatedCertificationSystemsHollow> {

    public CertificationSystemsProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<ConsolidatedCertificationSystemsHollow> getInputs() {
        return api.getAllConsolidatedCertificationSystemsHollow();
    }

    @Override
    public void processInput(ConsolidatedCertificationSystemsHollow input) {
        final int inputId = (int) input._getCertificationSystemId();

        {
            final String resourceId = L10nResourceIdLookup.getCertificationSystemNameID(inputId);
            addL10NResources(resourceId, input._getName());
        }

        {
            final String resourceId = L10nResourceIdLookup.getCertificationSystemDescriptionID(inputId);
            addL10NResources(resourceId, input._getDescription());
        }

        for (ConsolidatedCertSystemRatingHollow rating : input._getRating()) {
            int ratingId = (int) rating._getRatingId();
            {
                final String resourceId = L10nResourceIdLookup.getCertificationNameID(ratingId);
                addL10NResources(resourceId, rating._getRatingCodes());
            }

            {
                final String resourceId = L10nResourceIdLookup.getCertificationDescriptionID(ratingId);
                addL10NResources(resourceId, rating._getDescriptions());
            }
        }
    }
}