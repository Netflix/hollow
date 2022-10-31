package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MyEntityRankIndexDelegate extends HollowObjectDelegate {

    public int getIndexOrdinal(int ordinal);

    public MyEntityRankIndexTypeAPI getTypeAPI();

}