package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.CharactersHollow;
import com.netflix.vms.transformer.hollowinput.TranslatedTextHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.HashMap;
import java.util.Map;

public class CharactersProcessor extends AbstractL10NProcessor {

    public CharactersProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public int processResources() {
        for (CharactersHollow item : api.getAllCharactersHollow()) {
            final int itemId = (int) item._getId();
            final String prefix = item._getPrefix()._getValue();

            Map<String, TranslatedTextHollow> map = new HashMap<>();
            map.put("b", item._getB());
            map.put("cn", item._getCn());

            for (Map.Entry<String, TranslatedTextHollow> entry : map.entrySet()) {
                String key = entry.getKey();
                TranslatedTextHollow value = entry.getValue();
                if (value == null) continue;

                String resourceId = L10nResourceIdLookup.getGenericResourceId(itemId, prefix, key);
                addL10NResources(resourceId, value._getTranslatedTexts());
            }
        }

        return api.getAllCategoriesHollow().size();
    }
}