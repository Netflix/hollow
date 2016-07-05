package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoTypeMediaDelegate extends HollowObjectDelegate {

    public int getValueOrdinal(int ordinal);

    public VideoTypeMediaTypeAPI getTypeAPI();

}