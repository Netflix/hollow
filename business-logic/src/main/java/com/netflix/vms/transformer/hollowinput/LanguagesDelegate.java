package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface LanguagesDelegate extends HollowObjectDelegate {

    public long getLanguageId(int ordinal);

    public Long getLanguageIdBoxed(int ordinal);

    public int getNameOrdinal(int ordinal);

    public LanguagesTypeAPI getTypeAPI();

}