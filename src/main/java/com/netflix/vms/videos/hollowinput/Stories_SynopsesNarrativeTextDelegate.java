package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface Stories_SynopsesNarrativeTextDelegate extends HollowObjectDelegate {

    public int getTranslatedTextsOrdinal(int ordinal);

    public Stories_SynopsesNarrativeTextTypeAPI getTypeAPI();

}