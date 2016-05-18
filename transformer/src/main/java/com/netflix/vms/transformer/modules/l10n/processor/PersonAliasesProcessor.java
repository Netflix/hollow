package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.PersonAliasesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class PersonAliasesProcessor extends AbstractL10NProcessor {

    public PersonAliasesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public int processResources() {
        for (PersonAliasesHollow item : api.getAllPersonAliasesHollow()) {
            final int itemId = (int) item._getAliasId();

            final String resourceId = L10nResourceIdLookup.getPersonAliasID(itemId);
            addL10NResources(resourceId, item._getName()._getTranslatedTexts());
        }

        return api.getAllPersonAliasesHollow().size();
    }
}