package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoGeneralEpisodeTypeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoGeneralEpisodeTypeDelegate {

    private final String value;
    private final int valueOrdinal;
    private final String country;
    private final int countryOrdinal;
    private VideoGeneralEpisodeTypeTypeAPI typeAPI;

    public VideoGeneralEpisodeTypeDelegateCachedImpl(VideoGeneralEpisodeTypeTypeAPI typeAPI, int ordinal) {
        this.valueOrdinal = typeAPI.getValueOrdinal(ordinal);
        int valueTempOrdinal = valueOrdinal;
        this.value = valueTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(valueTempOrdinal);
        this.countryOrdinal = typeAPI.getCountryOrdinal(ordinal);
        int countryTempOrdinal = countryOrdinal;
        this.country = countryTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(countryTempOrdinal);
        this.typeAPI = typeAPI;
    }

    public String getValue(int ordinal) {
        return value;
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        if(testValue == null)
            return value == null;
        return testValue.equals(value);
    }

    public int getValueOrdinal(int ordinal) {
        return valueOrdinal;
    }

    public String getCountry(int ordinal) {
        return country;
    }

    public boolean isCountryEqual(int ordinal, String testValue) {
        if(testValue == null)
            return country == null;
        return testValue.equals(country);
    }

    public int getCountryOrdinal(int ordinal) {
        return countryOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoGeneralEpisodeTypeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoGeneralEpisodeTypeTypeAPI) typeAPI;
    }

}