package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieSetContentLabelDelegate extends HollowObjectDelegate {

    public String getDescription(int ordinal);

    public boolean isDescriptionEqual(int ordinal, String testValue);

    public int getDescriptionOrdinal(int ordinal);

    public int getId(int ordinal);

    public Integer getIdBoxed(int ordinal);

    public String get_name(int ordinal);

    public boolean is_nameEqual(int ordinal, String testValue);

    public MovieSetContentLabelTypeAPI getTypeAPI();

}