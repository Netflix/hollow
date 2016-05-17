package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ShowMemberTypesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class ShowMemberTypesProcessor extends AbstractL10NProcessor {

    public ShowMemberTypesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public void processResources() {
        for (ShowMemberTypesHollow item : api.getAllShowMemberTypesHollow()) {
            final int itemId = (int) item._getShowMemberTypeId();

            final String resourceId = L10nResourceIdLookup.getShowMemberTypeNameID(itemId);
            addL10NResources(resourceId, item._getDisplayName()._getTranslatedTexts());
        }
    }
}