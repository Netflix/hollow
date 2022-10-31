package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MyEntityDelegate extends HollowObjectDelegate {

    public int getId(int ordinal);

    public Integer getIdBoxed(int ordinal);

    public int getIdOrdinal(int ordinal);

    public String getName(int ordinal);

    public boolean isNameEqual(int ordinal, String testValue);

    public int getNameOrdinal(int ordinal);

    public int getProfileId(int ordinal);

    public Integer getProfileIdBoxed(int ordinal);

    public int getProfileIdOrdinal(int ordinal);

    public MyEntityTypeAPI getTypeAPI();

}