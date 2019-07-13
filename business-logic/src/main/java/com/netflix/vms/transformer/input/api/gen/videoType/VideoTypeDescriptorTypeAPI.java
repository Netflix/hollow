package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoTypeDescriptorTypeAPI extends HollowObjectTypeAPI {

    private final VideoTypeDescriptorDelegateLookupImpl delegateLookupImpl;

    public VideoTypeDescriptorTypeAPI(VideoTypeAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryCode",
            "copyright",
            "tierType",
            "original",
            "media",
            "extended"
        });
        this.delegateLookupImpl = new VideoTypeDescriptorDelegateLookupImpl(this);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoTypeDescriptor", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCopyrightOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoTypeDescriptor", ordinal, "copyright");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getCopyrightTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTierTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoTypeDescriptor", ordinal, "tierType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getTierTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getOriginal(int ordinal) {
        if(fieldIndex[3] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "original"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]));
    }

    public Boolean getOriginalBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "original");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public int getMediaOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoTypeDescriptor", ordinal, "media");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public VideoTypeMediaListTypeAPI getMediaTypeAPI() {
        return getAPI().getVideoTypeMediaListTypeAPI();
    }

    public boolean getExtended(int ordinal) {
        if(fieldIndex[5] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "extended"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]));
    }

    public Boolean getExtendedBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "extended");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public VideoTypeDescriptorDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VideoTypeAPI getAPI() {
        return (VideoTypeAPI) api;
    }

}