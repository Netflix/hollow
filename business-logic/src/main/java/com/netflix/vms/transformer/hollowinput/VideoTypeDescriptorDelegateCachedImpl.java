package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class VideoTypeDescriptorDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoTypeDescriptorDelegate {

    private final int countryCodeOrdinal;
    private final int copyrightOrdinal;
    private final int tierTypeOrdinal;
    private final Boolean original;
    private final int mediaOrdinal;
    private final Boolean extended;
    private VideoTypeDescriptorTypeAPI typeAPI;

    public VideoTypeDescriptorDelegateCachedImpl(VideoTypeDescriptorTypeAPI typeAPI, int ordinal) {
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.copyrightOrdinal = typeAPI.getCopyrightOrdinal(ordinal);
        this.tierTypeOrdinal = typeAPI.getTierTypeOrdinal(ordinal);
        this.original = typeAPI.getOriginalBoxed(ordinal);
        this.mediaOrdinal = typeAPI.getMediaOrdinal(ordinal);
        this.extended = typeAPI.getExtendedBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public int getCopyrightOrdinal(int ordinal) {
        return copyrightOrdinal;
    }

    public int getTierTypeOrdinal(int ordinal) {
        return tierTypeOrdinal;
    }

    public boolean getOriginal(int ordinal) {
        if(original == null)
            return false;
        return original.booleanValue();
    }

    public Boolean getOriginalBoxed(int ordinal) {
        return original;
    }

    public int getMediaOrdinal(int ordinal) {
        return mediaOrdinal;
    }

    public boolean getExtended(int ordinal) {
        if(extended == null)
            return false;
        return extended.booleanValue();
    }

    public Boolean getExtendedBoxed(int ordinal) {
        return extended;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoTypeDescriptorTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoTypeDescriptorTypeAPI) typeAPI;
    }

}