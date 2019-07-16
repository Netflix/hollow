package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieTypeDelegate extends HollowObjectDelegate {

    public boolean getStreamingType(int ordinal);

    public Boolean getStreamingTypeBoxed(int ordinal);

    public boolean getViewable(int ordinal);

    public Boolean getViewableBoxed(int ordinal);

    public boolean getMerchable(int ordinal);

    public Boolean getMerchableBoxed(int ordinal);

    public String get_name(int ordinal);

    public boolean is_nameEqual(int ordinal, String testValue);

    public MovieTypeTypeAPI getTypeAPI();

}