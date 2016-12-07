package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TurboCollectionsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TurboCollectionsDelegate {

    private final Long id;
    private final int prefixOrdinal;
    private final int char_nOrdinal;
    private final int nav_snOrdinal;
    private final int dnOrdinal;
    private final int kc_cnOrdinal;
    private final int st_2Ordinal;
    private final int bmt_nOrdinal;
    private final int st_1Ordinal;
    private final int st_4Ordinal;
    private final int st_3Ordinal;
    private final int st_0Ordinal;
    private final int st_9Ordinal;
    private final int snOrdinal;
    private final int kag_knOrdinal;
    private final int roar_nOrdinal;
    private final int st_6Ordinal;
    private final int st_5Ordinal;
    private final int st_8Ordinal;
    private final int tdnOrdinal;
    private final int st_7Ordinal;
   private TurboCollectionsTypeAPI typeAPI;

    public TurboCollectionsDelegateCachedImpl(TurboCollectionsTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.prefixOrdinal = typeAPI.getPrefixOrdinal(ordinal);
        this.char_nOrdinal = typeAPI.getChar_nOrdinal(ordinal);
        this.nav_snOrdinal = typeAPI.getNav_snOrdinal(ordinal);
        this.dnOrdinal = typeAPI.getDnOrdinal(ordinal);
        this.kc_cnOrdinal = typeAPI.getKc_cnOrdinal(ordinal);
        this.st_2Ordinal = typeAPI.getSt_2Ordinal(ordinal);
        this.bmt_nOrdinal = typeAPI.getBmt_nOrdinal(ordinal);
        this.st_1Ordinal = typeAPI.getSt_1Ordinal(ordinal);
        this.st_4Ordinal = typeAPI.getSt_4Ordinal(ordinal);
        this.st_3Ordinal = typeAPI.getSt_3Ordinal(ordinal);
        this.st_0Ordinal = typeAPI.getSt_0Ordinal(ordinal);
        this.st_9Ordinal = typeAPI.getSt_9Ordinal(ordinal);
        this.snOrdinal = typeAPI.getSnOrdinal(ordinal);
        this.kag_knOrdinal = typeAPI.getKag_knOrdinal(ordinal);
        this.roar_nOrdinal = typeAPI.getRoar_nOrdinal(ordinal);
        this.st_6Ordinal = typeAPI.getSt_6Ordinal(ordinal);
        this.st_5Ordinal = typeAPI.getSt_5Ordinal(ordinal);
        this.st_8Ordinal = typeAPI.getSt_8Ordinal(ordinal);
        this.tdnOrdinal = typeAPI.getTdnOrdinal(ordinal);
        this.st_7Ordinal = typeAPI.getSt_7Ordinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    public int getPrefixOrdinal(int ordinal) {
        return prefixOrdinal;
    }

    public int getChar_nOrdinal(int ordinal) {
        return char_nOrdinal;
    }

    public int getNav_snOrdinal(int ordinal) {
        return nav_snOrdinal;
    }

    public int getDnOrdinal(int ordinal) {
        return dnOrdinal;
    }

    public int getKc_cnOrdinal(int ordinal) {
        return kc_cnOrdinal;
    }

    public int getSt_2Ordinal(int ordinal) {
        return st_2Ordinal;
    }

    public int getBmt_nOrdinal(int ordinal) {
        return bmt_nOrdinal;
    }

    public int getSt_1Ordinal(int ordinal) {
        return st_1Ordinal;
    }

    public int getSt_4Ordinal(int ordinal) {
        return st_4Ordinal;
    }

    public int getSt_3Ordinal(int ordinal) {
        return st_3Ordinal;
    }

    public int getSt_0Ordinal(int ordinal) {
        return st_0Ordinal;
    }

    public int getSt_9Ordinal(int ordinal) {
        return st_9Ordinal;
    }

    public int getSnOrdinal(int ordinal) {
        return snOrdinal;
    }

    public int getKag_knOrdinal(int ordinal) {
        return kag_knOrdinal;
    }

    public int getRoar_nOrdinal(int ordinal) {
        return roar_nOrdinal;
    }

    public int getSt_6Ordinal(int ordinal) {
        return st_6Ordinal;
    }

    public int getSt_5Ordinal(int ordinal) {
        return st_5Ordinal;
    }

    public int getSt_8Ordinal(int ordinal) {
        return st_8Ordinal;
    }

    public int getTdnOrdinal(int ordinal) {
        return tdnOrdinal;
    }

    public int getSt_7Ordinal(int ordinal) {
        return st_7Ordinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TurboCollectionsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TurboCollectionsTypeAPI) typeAPI;
    }

}