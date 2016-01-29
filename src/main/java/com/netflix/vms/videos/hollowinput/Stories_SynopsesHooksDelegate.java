package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface Stories_SynopsesHooksDelegate extends HollowObjectDelegate {

    public int getTranslatedTextsOrdinal(int ordinal);

    public int getRankOrdinal(int ordinal);

    public int getTypeOrdinal(int ordinal);

    public Stories_SynopsesHooksTypeAPI getTypeAPI();

}