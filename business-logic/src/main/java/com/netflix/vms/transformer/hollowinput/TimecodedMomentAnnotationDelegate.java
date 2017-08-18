package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface TimecodedMomentAnnotationDelegate extends HollowObjectDelegate {

    public int getTypeOrdinal(int ordinal);

    public long getStartMillis(int ordinal);

    public Long getStartMillisBoxed(int ordinal);

    public long getEndMillis(int ordinal);

    public Long getEndMillisBoxed(int ordinal);

    public TimecodedMomentAnnotationTypeAPI getTypeAPI();

}