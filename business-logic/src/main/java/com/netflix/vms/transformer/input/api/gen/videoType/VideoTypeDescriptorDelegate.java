package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoTypeDescriptorDelegate extends HollowObjectDelegate {

    public String getCountryCode(int ordinal);

    public boolean isCountryCodeEqual(int ordinal, String testValue);

    public int getCountryCodeOrdinal(int ordinal);

    public String getCopyright(int ordinal);

    public boolean isCopyrightEqual(int ordinal, String testValue);

    public int getCopyrightOrdinal(int ordinal);

    public String getTierType(int ordinal);

    public boolean isTierTypeEqual(int ordinal, String testValue);

    public int getTierTypeOrdinal(int ordinal);

    public boolean getOriginal(int ordinal);

    public Boolean getOriginalBoxed(int ordinal);

    public int getMediaOrdinal(int ordinal);

    public boolean getExtended(int ordinal);

    public Boolean getExtendedBoxed(int ordinal);

    public VideoTypeDescriptorTypeAPI getTypeAPI();

}