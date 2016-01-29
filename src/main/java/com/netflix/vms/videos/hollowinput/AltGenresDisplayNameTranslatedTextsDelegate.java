package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface AltGenresDisplayNameTranslatedTextsDelegate extends HollowObjectDelegate {

    public int getValueOrdinal(int ordinal);

    public AltGenresDisplayNameTranslatedTextsTypeAPI getTypeAPI();

}