package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.LocalizedMetadataHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class LocalizedMetadataProcessor extends AbstractL10NProcessor<LocalizedMetadataHollow> {

    public LocalizedMetadataProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<LocalizedMetadataHollow> getInputs() {
        return api.getAllLocalizedMetadataHollow();
    }

    @Override
    public void processInput(LocalizedMetadataHollow input) {
        final int inputId = (int) input._getMovieId();
        final String name = input._getAttributeName()._getValue();
        final String label = input._getLabel()._getValue();

        final String resourceId = L10nResourceIdLookup.getRolloutAttribResourceId(inputId, name, label);
        addL10NResources(resourceId, input._getTranslatedTexts());
    }
}