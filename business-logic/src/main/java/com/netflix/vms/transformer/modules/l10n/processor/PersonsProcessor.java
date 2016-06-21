package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.PersonsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.L10NStrings;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;
import com.netflix.vms.transformer.util.NFLocaleUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PersonsProcessor extends AbstractL10NMiscProcessor<PersonsHollow> {

    private Set<NFLocale> transliteratedPersonLocales;

    public PersonsProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);

        String localesStr = ctx.getConfig().getTransliteratedPersonLocales();
        transliteratedPersonLocales = NFLocaleUtil.parseLocales(localesStr);
    }

    @Override
    public Collection<PersonsHollow> getInputs() {
        return api.getAllPersonsHollow();
    }

    @Override
    public void processInput(PersonsHollow input) {
        final int inputId = (int) input._getPersonId();

        { // Process Person Bio
            final String resourceId = L10nResourceIdLookup.getPersonBioID(inputId);
            addL10NResources(resourceId, input._getBio());
        }

        { // Process Person Names
            Map<NFLocale, L10NStrings> rawNameMap = processTranslatedText(input._getName()._getTranslatedTexts());
            Map<NFLocale, L10NStrings> regularNameMap = new HashMap<>();
            Map<NFLocale, L10NStrings> transliteratedNameMap = new HashMap<>();
            for (Map.Entry<NFLocale, L10NStrings> entry : rawNameMap.entrySet()) {
                NFLocale locale = entry.getKey();
                L10NStrings value = entry.getValue();
                if (transliteratedPersonLocales.contains(locale)) {
                    transliteratedNameMap.put(locale, value);
                } else {
                    regularNameMap.put(locale, value);
                }
            }

            {
                final String resourceId = L10nResourceIdLookup.getPersonNameID(inputId);
                addL10NResources(resourceId, regularNameMap);
            }
            {
                final String resourceId = L10nResourceIdLookup.getPersonTransliteratedNameResourceID(inputId);
                addL10NResources(resourceId, transliteratedNameMap);
            }
        }
    }
}