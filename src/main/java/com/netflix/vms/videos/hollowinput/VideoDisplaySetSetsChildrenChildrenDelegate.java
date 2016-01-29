package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoDisplaySetSetsChildrenChildrenDelegate extends HollowObjectDelegate {

    public long getParentSequenceNumber(int ordinal);

    public Long getParentSequenceNumberBoxed(int ordinal);

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public long getAltId(int ordinal);

    public Long getAltIdBoxed(int ordinal);

    public VideoDisplaySetSetsChildrenChildrenTypeAPI getTypeAPI();

}