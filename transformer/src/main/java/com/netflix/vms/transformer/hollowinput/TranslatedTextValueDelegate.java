package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface TranslatedTextValueDelegate extends HollowObjectDelegate {

    public int getValueOrdinal(int ordinal);

    public TranslatedTextValueTypeAPI getTypeAPI();

}