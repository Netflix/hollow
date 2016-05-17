package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.CategoriesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class CategoriesProcessor extends AbstractL10NProcessor {

    public CategoriesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public int processResources() {
        for (CategoriesHollow item : api.getAllCategoriesHollow()) {
            final int itemId = (int) item._getCategoryId();

            {
                final String resourceId = L10nResourceIdLookup.getCategoryNameID(itemId);
                addL10NResources(resourceId, item._getDisplayName()._getTranslatedTexts());
            }
        }

        return api.getAllCategoriesHollow().size();
    }
}