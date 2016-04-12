package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface Bcp47CodeDelegate extends HollowObjectDelegate {

    public long getLanguageId(int ordinal);

    public Long getLanguageIdBoxed(int ordinal);

    public int getIso6392CodeOrdinal(int ordinal);

    public int getBcp47CodeOrdinal(int ordinal);

    public int getIso6391CodeOrdinal(int ordinal);

    public int getIso6393CodeOrdinal(int ordinal);

    public Bcp47CodeTypeAPI getTypeAPI();

}