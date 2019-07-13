package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoGeneralInteractiveDataDelegate extends HollowObjectDelegate {

    public String getInteractiveType(int ordinal);

    public boolean isInteractiveTypeEqual(int ordinal, String testValue);

    public int getInteractiveTypeOrdinal(int ordinal);

    public int getInteractiveShortestRuntime(int ordinal);

    public Integer getInteractiveShortestRuntimeBoxed(int ordinal);

    public VideoGeneralInteractiveDataTypeAPI getTypeAPI();

}