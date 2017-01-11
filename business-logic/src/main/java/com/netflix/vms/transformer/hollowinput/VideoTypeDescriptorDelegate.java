package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoTypeDescriptorDelegate extends HollowObjectDelegate {

    public int getCountryCodeOrdinal(int ordinal);

    public int getCopyrightOrdinal(int ordinal);

    public int getTierTypeOrdinal(int ordinal);

    public boolean getOriginal(int ordinal);

    public Boolean getOriginalBoxed(int ordinal);

    public int getMediaOrdinal(int ordinal);

    public boolean getExtended(int ordinal);

    public Boolean getExtendedBoxed(int ordinal);

    public VideoTypeDescriptorTypeAPI getTypeAPI();

}