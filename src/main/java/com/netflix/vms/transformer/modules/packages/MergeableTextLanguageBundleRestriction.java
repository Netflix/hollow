package com.netflix.vms.transformer.modules.packages;

import java.util.HashSet;
import java.util.Set;

public class MergeableTextLanguageBundleRestriction {

    private final Set<String> allowedTextLanguages;
    private final Set<String> disallowedTextLanguages;

    public MergeableTextLanguageBundleRestriction() {
        this.allowedTextLanguages = new HashSet<String>();
        this.disallowedTextLanguages = new HashSet<String>();
    }

    public void mergeWith(MergeableTextLanguageBundleRestriction otherRestriction) {
        for(String lang : otherRestriction.allowedTextLanguages)
            allowedTextLanguages.add(lang);

        for(String lang : otherRestriction.disallowedTextLanguages)
            disallowedTextLanguages.add(lang);
    }

    public void addDisallowedTextLanguage(String textLang) {
        disallowedTextLanguages.add(textLang);
    }

    public void addAllowedTextLanguages(Set<String> textLangs) {
        allowedTextLanguages.addAll(textLangs);
    }

    public Set<String> getFinalDisallowedTextLanguages() {
        disallowedTextLanguages.removeAll(allowedTextLanguages);
        return disallowedTextLanguages;
    }

}
