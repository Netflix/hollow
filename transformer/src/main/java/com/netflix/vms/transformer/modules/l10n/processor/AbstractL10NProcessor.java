package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.MapOfTranslatedTextHollow;
import com.netflix.vms.transformer.hollowinput.TranslatedTextHollow;
import com.netflix.vms.transformer.hollowinput.TranslatedTextValueHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.L10NResources;
import com.netflix.vms.transformer.hollowoutput.L10NStrings;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.util.NFLocaleUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractL10NProcessor<K> implements L10NProcessor<K> {
    protected final VMSHollowInputAPI api;
    protected final TransformerContext ctx;
    protected final HollowObjectMapper mapper;

    protected int itemsAdded;

    AbstractL10NProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        this.api = api;
        this.ctx = ctx;
        this.mapper = mapper;
    }

    /**
     * Process Resources and return the number of items processed
     */
    @Override
    public final int processResources() {
        Collection<K> inputs = getInputs();
        for (K input : inputs) {
            processInput(input);
        }
        return inputs.size();
    }

    @Override
    public int getItemsAdded() {
        return itemsAdded;
    }

    protected void addL10NResources(String id, TranslatedTextHollow translatedText) {
        addL10NResources(id, processTranslatedText(translatedText._getTranslatedTexts()));
    }

    protected void addL10NResources(String id, MapOfTranslatedTextHollow mapOfTranslatedText) {
        addL10NResources(id, processTranslatedText(mapOfTranslatedText));
    }

    protected void addL10NResources(String id, Map<NFLocale, L10NStrings> mapOfTranslatedText) {
        L10NResources l10n = new L10NResources();
        l10n.resourceIdStr = id.toCharArray();
        l10n.localizedStrings = mapOfTranslatedText;
        mapper.addObject(l10n);
        itemsAdded++;
    }

    protected Map<NFLocale, L10NStrings> processTranslatedText(MapOfTranslatedTextHollow mapOfTranslatedText) {
        Map<NFLocale, L10NStrings> result = new HashMap<>();

        for (Map.Entry<MapKeyHollow, TranslatedTextValueHollow> entry : mapOfTranslatedText.entrySet()) {
            String locale = entry.getKey()._getValue();
            String value = entry.getValue()._getValue()._getValue();
            result.put(NFLocaleUtil.createNFLocale(locale), new L10NStrings(value));
        }

        return result;
    }
}
