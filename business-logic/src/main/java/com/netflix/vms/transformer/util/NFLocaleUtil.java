package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.hollowoutput.NFLocale;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NFLocaleUtil {
    public static NFLocale createNFLocale(String value) {
        //NOTE: com.netflix.i18n.NFLocale needed to convert pt-BR to pt_BR (Use NFlocale.getName() to be backwards compatible with NFLocaleSerializer) t
        //TODO: How do we get rid of this?
        final NFLocale locale = new NFLocale(com.netflix.i18n.NFLocale.findInstance(value).getName());
        return locale;
    }

    public static Set<NFLocale> parseLocales(String locales) {
        if (locales == null || locales.trim().isEmpty()) return Collections.emptySet();

        Set<NFLocale> result = new HashSet<>();
        for (String locale : locales.split(",")) {
            result.add(NFLocaleUtil.createNFLocale(locale));
        }
        return result;
    }
}
