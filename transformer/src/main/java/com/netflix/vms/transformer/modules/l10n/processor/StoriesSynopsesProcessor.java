package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHollow;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHookHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.HookType;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class StoriesSynopsesProcessor extends AbstractL10NProcessor<StoriesSynopsesHollow> {

    public StoriesSynopsesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<StoriesSynopsesHollow> getInputs() {
        return api.getAllStoriesSynopsesHollow();
    }

    @Override
    public void processInput(StoriesSynopsesHollow input) {
        final int inputId = (int) input._getMovieId();

        { // narrativeText
            final String resourceId = L10nResourceIdLookup.getNarrativeTextId(inputId);
            addL10NResources(resourceId, input._getNarrativeText());
        }

        { // hooks
            for (StoriesSynopsesHookHollow hook : input._getHooks()) {
                String type = hook._getType()._getValue();
                final String resourceId = L10nResourceIdLookup.getHookTextId(inputId, HookType.toHookType(type));
                addL10NResources(resourceId, hook._getTranslatedTexts());
            }
        }
    }
}