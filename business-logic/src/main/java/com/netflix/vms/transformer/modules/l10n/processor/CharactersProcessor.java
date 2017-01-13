package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.CharactersHollow;
import com.netflix.vms.transformer.hollowinput.TranslatedTextHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CharactersProcessor extends AbstractL10NMiscProcessor<CharactersHollow> {

    public CharactersProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<CharactersHollow> getInputs() {
        return api.getAllCharactersHollow();
    }

    @Override
    public void processInput(CharactersHollow input) {
        final int inputId = (int) input._getId();
        final String prefix = input._getPrefix()._getValue();

        Map<String, TranslatedTextHollow> map = new HashMap<>();
        map.put("b", input._getB());
        map.put("cn", input._getCn());

        for (Map.Entry<String, TranslatedTextHollow> entry : map.entrySet()) {
            String key = entry.getKey();
            TranslatedTextHollow value = entry.getValue();
            if (value == null) continue;

            String resourceId = L10nResourceIdLookup.getGenericResourceId(inputId, prefix, key);
            addL10NResources(resourceId, value);
        }
    }
}