package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface TranslatedTextDelegate extends HollowObjectDelegate {

    public int getTranslatedTextsOrdinal(int ordinal);

    public TranslatedTextTypeAPI getTypeAPI();

}