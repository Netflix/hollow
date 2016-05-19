package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ShowMemberTypesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class ShowMemberTypesProcessor extends AbstractL10NMiscProcessor<ShowMemberTypesHollow> {

    public ShowMemberTypesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<ShowMemberTypesHollow> getInputs() {
        return api.getAllShowMemberTypesHollow();
    }

    @Override
    public void processInput(ShowMemberTypesHollow input) {
        final int inputId = (int) input._getShowMemberTypeId();

        final String resourceId = L10nResourceIdLookup.getShowMemberTypeNameID(inputId);
        addL10NResources(resourceId, input._getDisplayName());
    }
}