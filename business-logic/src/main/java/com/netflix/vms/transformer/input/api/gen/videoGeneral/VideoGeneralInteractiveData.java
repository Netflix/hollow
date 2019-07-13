package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoGeneralInteractiveData extends HollowObject {

    public VideoGeneralInteractiveData(VideoGeneralInteractiveDataDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getInteractiveType() {
        return delegate().getInteractiveType(ordinal);
    }

    public boolean isInteractiveTypeEqual(String testValue) {
        return delegate().isInteractiveTypeEqual(ordinal, testValue);
    }

    public HString getInteractiveTypeHollowReference() {
        int refOrdinal = delegate().getInteractiveTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public int getInteractiveShortestRuntime() {
        return delegate().getInteractiveShortestRuntime(ordinal);
    }

    public Integer getInteractiveShortestRuntimeBoxed() {
        return delegate().getInteractiveShortestRuntimeBoxed(ordinal);
    }

    public VideoGeneralAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralInteractiveDataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoGeneralInteractiveDataDelegate delegate() {
        return (VideoGeneralInteractiveDataDelegate)delegate;
    }

}