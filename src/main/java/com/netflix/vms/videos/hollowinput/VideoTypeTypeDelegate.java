package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoTypeTypeDelegate extends HollowObjectDelegate {

    public boolean getIsOriginal(int ordinal);

    public Boolean getIsOriginalBoxed(int ordinal);

    public long getShowMemberTypeId(int ordinal);

    public Long getShowMemberTypeIdBoxed(int ordinal);

    public int getCopyrightOrdinal(int ordinal);

    public int getCountryCodeOrdinal(int ordinal);

    public boolean getIsContentApproved(int ordinal);

    public Boolean getIsContentApprovedBoxed(int ordinal);

    public int getMediaOrdinal(int ordinal);

    public boolean getIsCanon(int ordinal);

    public Boolean getIsCanonBoxed(int ordinal);

    public boolean getIsExtended(int ordinal);

    public Boolean getIsExtendedBoxed(int ordinal);

    public VideoTypeTypeTypeAPI getTypeAPI();

}