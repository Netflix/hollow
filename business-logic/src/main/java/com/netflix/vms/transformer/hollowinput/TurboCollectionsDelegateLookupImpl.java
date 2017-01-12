package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TurboCollectionsDelegateLookupImpl extends HollowObjectAbstractDelegate implements TurboCollectionsDelegate {

    private final TurboCollectionsTypeAPI typeAPI;

    public TurboCollectionsDelegateLookupImpl(TurboCollectionsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Long getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
    }

    public int getPrefixOrdinal(int ordinal) {
        return typeAPI.getPrefixOrdinal(ordinal);
    }

    public int getChar_nOrdinal(int ordinal) {
        return typeAPI.getChar_nOrdinal(ordinal);
    }

    public int getNav_snOrdinal(int ordinal) {
        return typeAPI.getNav_snOrdinal(ordinal);
    }

    public int getDnOrdinal(int ordinal) {
        return typeAPI.getDnOrdinal(ordinal);
    }

    public int getKc_cnOrdinal(int ordinal) {
        return typeAPI.getKc_cnOrdinal(ordinal);
    }

    public int getSt_2Ordinal(int ordinal) {
        return typeAPI.getSt_2Ordinal(ordinal);
    }

    public int getBmt_nOrdinal(int ordinal) {
        return typeAPI.getBmt_nOrdinal(ordinal);
    }

    public int getSt_1Ordinal(int ordinal) {
        return typeAPI.getSt_1Ordinal(ordinal);
    }

    public int getSt_4Ordinal(int ordinal) {
        return typeAPI.getSt_4Ordinal(ordinal);
    }

    public int getSt_3Ordinal(int ordinal) {
        return typeAPI.getSt_3Ordinal(ordinal);
    }

    public int getSt_0Ordinal(int ordinal) {
        return typeAPI.getSt_0Ordinal(ordinal);
    }

    public int getSt_9Ordinal(int ordinal) {
        return typeAPI.getSt_9Ordinal(ordinal);
    }

    public int getSnOrdinal(int ordinal) {
        return typeAPI.getSnOrdinal(ordinal);
    }

    public int getKag_knOrdinal(int ordinal) {
        return typeAPI.getKag_knOrdinal(ordinal);
    }

    public int getRoar_nOrdinal(int ordinal) {
        return typeAPI.getRoar_nOrdinal(ordinal);
    }

    public int getSt_6Ordinal(int ordinal) {
        return typeAPI.getSt_6Ordinal(ordinal);
    }

    public int getSt_5Ordinal(int ordinal) {
        return typeAPI.getSt_5Ordinal(ordinal);
    }

    public int getSt_8Ordinal(int ordinal) {
        return typeAPI.getSt_8Ordinal(ordinal);
    }

    public int getTdnOrdinal(int ordinal) {
        return typeAPI.getTdnOrdinal(ordinal);
    }

    public int getSt_7Ordinal(int ordinal) {
        return typeAPI.getSt_7Ordinal(ordinal);
    }

    public TurboCollectionsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}