package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IntegerDelegate extends HollowObjectDelegate {

    public int getValue(int ordinal);

    public Integer getValueBoxed(int ordinal);

    public IntegerTypeAPI getTypeAPI();

}