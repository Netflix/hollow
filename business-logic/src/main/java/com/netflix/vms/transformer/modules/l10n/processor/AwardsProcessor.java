package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.AwardsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;
import java.util.Collection;

public class AwardsProcessor extends AbstractL10NMiscProcessor<AwardsHollow> {

    public AwardsProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<AwardsHollow> getInputs() {
        return api.getAllAwardsHollow();
    }

    @Override
    public void processInput(AwardsHollow input) {
        final int inputId = (int) input._getAwardId();
        {
            final String resourceId = L10nResourceIdLookup.getAwardNameID(inputId);
            addL10NResources(resourceId, input._getAwardName());
        }

        {
            final String resourceId = L10nResourceIdLookup.getAwardDescriptionID(inputId);
            addL10NResources(resourceId, input._getDescription());
        }

        {
            final String resourceId = L10nResourceIdLookup.getAwardAlternateNameID(inputId);
            addL10NResources(resourceId, input._getAlternateName());
        }
    }
}