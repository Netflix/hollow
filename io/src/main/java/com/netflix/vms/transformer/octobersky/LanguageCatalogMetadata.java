package com.netflix.vms.transformer.octobersky;

import java.util.List;

@SuppressWarnings("unused")
public class LanguageCatalogMetadata {

    private String catalogName;
    private List<String> catalogLanguages;
    private List<String> otherLanguagesIfOriginalLanguage;

    public LanguageCatalogMetadata() {

    }

    public LanguageCatalogMetadata(String catalogName, List<String> catalogLanguages, List<String> otherLanguagesIfOriginalLanguage) {
        this.catalogName = catalogName;
        this.catalogLanguages = catalogLanguages;
        this.otherLanguagesIfOriginalLanguage = otherLanguagesIfOriginalLanguage;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public List<String> getCatalogLanguages() {
        return catalogLanguages;
    }

    public List<String> getOtherLanguagesIfOriginalLanguage() {
        return otherLanguagesIfOriginalLanguage;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public void setCatalogLanguages(List<String> catalogLanguages) {
        this.catalogLanguages = catalogLanguages;
    }

    public void setOtherLanguagesIfOriginalLanguage(List<String> otherLanguagesIfOriginalLanguage) {
        this.otherLanguagesIfOriginalLanguage = otherLanguagesIfOriginalLanguage;
    }
}
