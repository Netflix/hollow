package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoAwardMappingDelegate extends HollowObjectDelegate {

    public long getAwardId(int ordinal);

    public Long getAwardIdBoxed(int ordinal);

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public long getYear(int ordinal);

    public Long getYearBoxed(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public boolean getWinner(int ordinal);

    public Boolean getWinnerBoxed(int ordinal);

    public VideoAwardMappingTypeAPI getTypeAPI();

}