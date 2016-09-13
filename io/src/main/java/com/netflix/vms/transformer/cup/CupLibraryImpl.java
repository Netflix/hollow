package com.netflix.vms.transformer.cup;

import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.cup.ContentUsagePolicyFactory;
import com.netflix.vms.transformer.common.cup.CupLibrary;

@Singleton
public class CupLibraryImpl implements CupLibrary {

    private final ContentUsagePolicyFactory cupFactory;

    @Inject
    public CupLibraryImpl(final ContentUsagePolicyFactory cupFactory) {
        this.cupFactory = cupFactory;
    }
    @Override
    public int getMaximumVideoHeight(Set<String> cupTokens, String deviceCategory) {
        return cupFactory.getCupMaximumVideoHeight(cupTokens, deviceCategory);
    }
}
