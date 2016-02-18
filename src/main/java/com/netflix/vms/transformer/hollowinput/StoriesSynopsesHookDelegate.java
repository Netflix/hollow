package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface StoriesSynopsesHookDelegate extends HollowObjectDelegate {

    public int getTypeOrdinal(int ordinal);

    public int getRankOrdinal(int ordinal);

    public int getTranslatedTextsOrdinal(int ordinal);

    public StoriesSynopsesHookTypeAPI getTypeAPI();

}