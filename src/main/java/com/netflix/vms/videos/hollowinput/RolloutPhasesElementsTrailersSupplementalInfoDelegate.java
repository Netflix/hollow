package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhasesElementsTrailersSupplementalInfoDelegate extends HollowObjectDelegate {

    public int getImageBackgroundToneOrdinal(int ordinal);

    public long getVideoLength(int ordinal);

    public Long getVideoLengthBoxed(int ordinal);

    public int getSubtitleLocaleOrdinal(int ordinal);

    public long getSeasonNumber(int ordinal);

    public Long getSeasonNumberBoxed(int ordinal);

    public int getVideoOrdinal(int ordinal);

    public int getImageTagOrdinal(int ordinal);

    public int getVideoValueOrdinal(int ordinal);

    public long getPriority(int ordinal);

    public Long getPriorityBoxed(int ordinal);

    public RolloutPhasesElementsTrailersSupplementalInfoTypeAPI getTypeAPI();

}