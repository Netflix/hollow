package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.cup.CupLibrary;

import java.util.Set;

public class SimpleCupLibrary implements CupLibrary {

    public static CupLibrary INSTANCE = new SimpleCupLibrary();

    @Override
    public int getMaximumVideoHeight(Set<String> cupTokens, String deviceCategory) {
        return Integer.MAX_VALUE;
    }
}