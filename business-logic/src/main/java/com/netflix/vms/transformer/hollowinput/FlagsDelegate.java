package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface FlagsDelegate extends HollowObjectDelegate {

    public boolean getSearchOnly(int ordinal);

    public Boolean getSearchOnlyBoxed(int ordinal);

    public boolean getLocalText(int ordinal);

    public Boolean getLocalTextBoxed(int ordinal);

    public boolean getLanguageOverride(int ordinal);

    public Boolean getLanguageOverrideBoxed(int ordinal);

    public boolean getLocalAudio(int ordinal);

    public Boolean getLocalAudioBoxed(int ordinal);

    public int getFirstDisplayDatesOrdinal(int ordinal);

    public boolean getGoLive(int ordinal);

    public Boolean getGoLiveBoxed(int ordinal);

    public boolean getContentApproved(int ordinal);

    public Boolean getContentApprovedBoxed(int ordinal);

    public boolean getAutoPlay(int ordinal);

    public Boolean getAutoPlayBoxed(int ordinal);

    public int getFirstDisplayDateOrdinal(int ordinal);

    public FlagsTypeAPI getTypeAPI();

}