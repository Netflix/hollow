package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TurboCollectionsTypeAPI extends HollowObjectTypeAPI {

    private final TurboCollectionsDelegateLookupImpl delegateLookupImpl;

    TurboCollectionsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "prefix",
            "char.n",
            "nav.sn",
            "dn",
            "kc.cn",
            "st.2",
            "bmt.n",
            "st.1",
            "st.4",
            "st.3",
            "st.0",
            "st.9",
            "sn",
            "kag.kn",
            "roar.n",
            "st.6",
            "st.5",
            "st.8",
            "tdn",
            "st.7"
        });
        this.delegateLookupImpl = new TurboCollectionsDelegateLookupImpl(this);
    }

    public long getId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("TurboCollections", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("TurboCollections", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getPrefixOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "prefix");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getPrefixTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getChar_nOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "char_n");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public TranslatedTextTypeAPI getChar_nTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getNav_snOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "nav_sn");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public TranslatedTextTypeAPI getNav_snTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getDnOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "dn");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public TranslatedTextTypeAPI getDnTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getKc_cnOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "kc_cn");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public TranslatedTextTypeAPI getKc_cnTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_2Ordinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_2");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public TranslatedTextTypeAPI getSt_2TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getBmt_nOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "bmt_n");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public TranslatedTextTypeAPI getBmt_nTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_1Ordinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_1");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public TranslatedTextTypeAPI getSt_1TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_4Ordinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_4");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public TranslatedTextTypeAPI getSt_4TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_3Ordinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_3");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public TranslatedTextTypeAPI getSt_3TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_0Ordinal(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_0");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[11]);
    }

    public TranslatedTextTypeAPI getSt_0TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_9Ordinal(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_9");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[12]);
    }

    public TranslatedTextTypeAPI getSt_9TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSnOrdinal(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "sn");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[13]);
    }

    public TranslatedTextTypeAPI getSnTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getKag_knOrdinal(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "kag_kn");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[14]);
    }

    public TranslatedTextTypeAPI getKag_knTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getRoar_nOrdinal(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "roar_n");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[15]);
    }

    public TranslatedTextTypeAPI getRoar_nTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_6Ordinal(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_6");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[16]);
    }

    public TranslatedTextTypeAPI getSt_6TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_5Ordinal(int ordinal) {
        if(fieldIndex[17] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_5");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[17]);
    }

    public TranslatedTextTypeAPI getSt_5TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_8Ordinal(int ordinal) {
        if(fieldIndex[18] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_8");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[18]);
    }

    public TranslatedTextTypeAPI getSt_8TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getTdnOrdinal(int ordinal) {
        if(fieldIndex[19] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "tdn");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[19]);
    }

    public TranslatedTextTypeAPI getTdnTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_7Ordinal(int ordinal) {
        if(fieldIndex[20] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_7");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[20]);
    }

    public TranslatedTextTypeAPI getSt_7TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public TurboCollectionsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}