package com.netflix.vms.transformer.common.cup;

import java.util.Set;

public interface CupLibrary {

    int getMaximumVideoHeight(final Set<String> cupTokens, final String deviceCategory);

}