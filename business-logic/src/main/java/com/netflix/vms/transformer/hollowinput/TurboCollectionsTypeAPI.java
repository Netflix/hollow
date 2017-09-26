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
            "st.7",
            "st.10",
            "st.11",
            "st.12",
            "st.13",
            "st.14",
            "st.15",
            "st.16",
            "st.17",
            "st.18",
            "st.19",
            "st.20",
            "st.21",
            "st.22",
            "st.23",
            "st.24",
            "st.25",
            "st.26",
            "st.27",
            "st.28",
            "st.29",
            "st.30",
            "st.31",
            "st.32",
            "st.33",
            "st.34",
            "st.35",
            "st.36",
            "st.37",
            "st.38",
            "st.39",
            "st.40",
            "st.41",
            "st.42",
            "st.43",
            "st.44",
            "st.45",
            "st.46",
            "st.47",
            "st.48",
            "st.49",
            "st.50",
            "st.51",
            "st.52",
            "st.53",
            "st.54",
            "st.55",
            "st.56",
            "st.57",
            "st.58",
            "st.59",
            "st.60",
            "st.61",
            "st.62",
            "st.63",
            "st.64",
            "st.65",
            "st.66",
            "st.67",
            "st.68",
            "st.69",
            "st.70",
            "st.71",
            "st.72",
            "st.73",
            "st.74",
            "st.75",
            "st.76",
            "st.77",
            "st.78",
            "st.79",
            "st.80",
            "st.81",
            "st.82",
            "st.83",
            "st.84",
            "st.85",
            "st.86",
            "st.87",
            "st.88",
            "st.89",
            "st.90",
            "st.91",
            "st.92",
            "st.93",
            "st.94",
            "st.95",
            "st.96",
            "st.97",
            "st.98",
            "st.99",
            "st.100",
            "st.101",
            "st.102",
            "st.103",
            "st.104",
            "st.105",
            "st.106",
            "st.107",
            "st.108",
            "st.109",
            "st.110",
            "st.111",
            "st.112",
            "st.113",
            "st.114",
            "st.115",
            "st.116",
            "st.117",
            "st.118",
            "st.119",
            "st.120",
            "st.121",
            "st.122",
            "st.123",
            "st.124",
            "st.125",
            "st.126",
            "st.127",
            "st.128",
            "st.129",
            "st.130",
            "st.131",
            "st.132",
            "st.133",
            "st.134",
            "st.135",
            "st.136",
            "st.137",
            "st.138",
            "st.139",
            "st.140",
            "st.141",
            "st.142",
            "st.143",
            "st.144",
            "st.145",
            "st.146",
            "st.147",
            "st.148",
            "st.149",
            "st.150",
            "st.151",
            "st.152",
            "st.153",
            "st.154",
            "st.155",
            "st.156",
            "st.157",
            "st.158",
            "st.159",
            "st.160",
            "st.161",
            "st.162",
            "st.163",
            "st.164",
            "st.165",
            "st.166",
            "st.167",
            "st.168",
            "st.169",
            "st.170",
            "st.171",
            "st.172",
            "st.173",
            "st.174",
            "st.175",
            "st.176",
            "st.177",
            "st.178",
            "st.179",
            "st.180",
            "st.181",
            "st.182",
            "st.183",
            "st.184",
            "st.185",
            "st.186",
            "st.187",
            "st.188",
            "st.189",
            "st.190",
            "st.191",
            "st.192",
            "st.193",
            "st.194",
            "st.195",
            "st.196",
            "st.197",
            "st.198",
            "st.199",
            "st.200",
            "st.201",
            "st.202",
            "st.203",
            "st.204",
            "st.205",
            "st.206",
            "st.207",
            "st.208",
            "st.209",
            "st.210",
            "st.211",
            "st.212",
            "st.213",
            "st.214",
            "st.215",
            "st.216",
            "st.217",
            "st.218",
            "st.219",
            "st.220",
            "st.221",
            "st.222",
            "st.223",
            "st.224",
            "st.225",
            "st.226",
            "st.227",
            "st.228",
            "st.229",
            "st.230"
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

    public int getSt_10Ordinal(int ordinal) {
        if(fieldIndex[21] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_10");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[21]);
    }

    public TranslatedTextTypeAPI getSt_10TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_11Ordinal(int ordinal) {
        if(fieldIndex[22] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_11");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[22]);
    }

    public TranslatedTextTypeAPI getSt_11TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_12Ordinal(int ordinal) {
        if(fieldIndex[23] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_12");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[23]);
    }

    public TranslatedTextTypeAPI getSt_12TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_13Ordinal(int ordinal) {
        if(fieldIndex[24] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_13");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[24]);
    }

    public TranslatedTextTypeAPI getSt_13TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_14Ordinal(int ordinal) {
        if(fieldIndex[25] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_14");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[25]);
    }

    public TranslatedTextTypeAPI getSt_14TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_15Ordinal(int ordinal) {
        if(fieldIndex[26] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_15");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[26]);
    }

    public TranslatedTextTypeAPI getSt_15TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_16Ordinal(int ordinal) {
        if(fieldIndex[27] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_16");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[27]);
    }

    public TranslatedTextTypeAPI getSt_16TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_17Ordinal(int ordinal) {
        if(fieldIndex[28] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_17");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[28]);
    }

    public TranslatedTextTypeAPI getSt_17TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_18Ordinal(int ordinal) {
        if(fieldIndex[29] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_18");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[29]);
    }

    public TranslatedTextTypeAPI getSt_18TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_19Ordinal(int ordinal) {
        if(fieldIndex[30] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_19");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[30]);
    }

    public TranslatedTextTypeAPI getSt_19TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_20Ordinal(int ordinal) {
        if(fieldIndex[31] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_20");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[31]);
    }

    public TranslatedTextTypeAPI getSt_20TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_21Ordinal(int ordinal) {
        if(fieldIndex[32] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_21");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[32]);
    }

    public TranslatedTextTypeAPI getSt_21TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_22Ordinal(int ordinal) {
        if(fieldIndex[33] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_22");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[33]);
    }

    public TranslatedTextTypeAPI getSt_22TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_23Ordinal(int ordinal) {
        if(fieldIndex[34] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_23");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[34]);
    }

    public TranslatedTextTypeAPI getSt_23TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_24Ordinal(int ordinal) {
        if(fieldIndex[35] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_24");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[35]);
    }

    public TranslatedTextTypeAPI getSt_24TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_25Ordinal(int ordinal) {
        if(fieldIndex[36] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_25");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[36]);
    }

    public TranslatedTextTypeAPI getSt_25TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_26Ordinal(int ordinal) {
        if(fieldIndex[37] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_26");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[37]);
    }

    public TranslatedTextTypeAPI getSt_26TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_27Ordinal(int ordinal) {
        if(fieldIndex[38] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_27");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[38]);
    }

    public TranslatedTextTypeAPI getSt_27TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_28Ordinal(int ordinal) {
        if(fieldIndex[39] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_28");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[39]);
    }

    public TranslatedTextTypeAPI getSt_28TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_29Ordinal(int ordinal) {
        if(fieldIndex[40] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_29");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[40]);
    }

    public TranslatedTextTypeAPI getSt_29TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_30Ordinal(int ordinal) {
        if(fieldIndex[41] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_30");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[41]);
    }

    public TranslatedTextTypeAPI getSt_30TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_31Ordinal(int ordinal) {
        if(fieldIndex[42] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_31");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[42]);
    }

    public TranslatedTextTypeAPI getSt_31TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_32Ordinal(int ordinal) {
        if(fieldIndex[43] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_32");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[43]);
    }

    public TranslatedTextTypeAPI getSt_32TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_33Ordinal(int ordinal) {
        if(fieldIndex[44] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_33");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[44]);
    }

    public TranslatedTextTypeAPI getSt_33TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_34Ordinal(int ordinal) {
        if(fieldIndex[45] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_34");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[45]);
    }

    public TranslatedTextTypeAPI getSt_34TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_35Ordinal(int ordinal) {
        if(fieldIndex[46] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_35");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[46]);
    }

    public TranslatedTextTypeAPI getSt_35TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_36Ordinal(int ordinal) {
        if(fieldIndex[47] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_36");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[47]);
    }

    public TranslatedTextTypeAPI getSt_36TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_37Ordinal(int ordinal) {
        if(fieldIndex[48] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_37");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[48]);
    }

    public TranslatedTextTypeAPI getSt_37TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_38Ordinal(int ordinal) {
        if(fieldIndex[49] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_38");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[49]);
    }

    public TranslatedTextTypeAPI getSt_38TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_39Ordinal(int ordinal) {
        if(fieldIndex[50] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_39");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[50]);
    }

    public TranslatedTextTypeAPI getSt_39TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_40Ordinal(int ordinal) {
        if(fieldIndex[51] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_40");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[51]);
    }

    public TranslatedTextTypeAPI getSt_40TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_41Ordinal(int ordinal) {
        if(fieldIndex[52] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_41");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[52]);
    }

    public TranslatedTextTypeAPI getSt_41TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_42Ordinal(int ordinal) {
        if(fieldIndex[53] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_42");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[53]);
    }

    public TranslatedTextTypeAPI getSt_42TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_43Ordinal(int ordinal) {
        if(fieldIndex[54] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_43");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[54]);
    }

    public TranslatedTextTypeAPI getSt_43TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_44Ordinal(int ordinal) {
        if(fieldIndex[55] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_44");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[55]);
    }

    public TranslatedTextTypeAPI getSt_44TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_45Ordinal(int ordinal) {
        if(fieldIndex[56] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_45");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[56]);
    }

    public TranslatedTextTypeAPI getSt_45TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_46Ordinal(int ordinal) {
        if(fieldIndex[57] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_46");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[57]);
    }

    public TranslatedTextTypeAPI getSt_46TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_47Ordinal(int ordinal) {
        if(fieldIndex[58] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_47");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[58]);
    }

    public TranslatedTextTypeAPI getSt_47TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_48Ordinal(int ordinal) {
        if(fieldIndex[59] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_48");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[59]);
    }

    public TranslatedTextTypeAPI getSt_48TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_49Ordinal(int ordinal) {
        if(fieldIndex[60] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_49");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[60]);
    }

    public TranslatedTextTypeAPI getSt_49TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_50Ordinal(int ordinal) {
        if(fieldIndex[61] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_50");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[61]);
    }

    public TranslatedTextTypeAPI getSt_50TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_51Ordinal(int ordinal) {
        if(fieldIndex[62] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_51");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[62]);
    }

    public TranslatedTextTypeAPI getSt_51TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_52Ordinal(int ordinal) {
        if(fieldIndex[63] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_52");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[63]);
    }

    public TranslatedTextTypeAPI getSt_52TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_53Ordinal(int ordinal) {
        if(fieldIndex[64] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_53");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[64]);
    }

    public TranslatedTextTypeAPI getSt_53TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_54Ordinal(int ordinal) {
        if(fieldIndex[65] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_54");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[65]);
    }

    public TranslatedTextTypeAPI getSt_54TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_55Ordinal(int ordinal) {
        if(fieldIndex[66] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_55");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[66]);
    }

    public TranslatedTextTypeAPI getSt_55TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_56Ordinal(int ordinal) {
        if(fieldIndex[67] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_56");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[67]);
    }

    public TranslatedTextTypeAPI getSt_56TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_57Ordinal(int ordinal) {
        if(fieldIndex[68] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_57");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[68]);
    }

    public TranslatedTextTypeAPI getSt_57TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_58Ordinal(int ordinal) {
        if(fieldIndex[69] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_58");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[69]);
    }

    public TranslatedTextTypeAPI getSt_58TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_59Ordinal(int ordinal) {
        if(fieldIndex[70] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_59");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[70]);
    }

    public TranslatedTextTypeAPI getSt_59TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_60Ordinal(int ordinal) {
        if(fieldIndex[71] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_60");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[71]);
    }

    public TranslatedTextTypeAPI getSt_60TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_61Ordinal(int ordinal) {
        if(fieldIndex[72] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_61");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[72]);
    }

    public TranslatedTextTypeAPI getSt_61TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_62Ordinal(int ordinal) {
        if(fieldIndex[73] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_62");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[73]);
    }

    public TranslatedTextTypeAPI getSt_62TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_63Ordinal(int ordinal) {
        if(fieldIndex[74] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_63");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[74]);
    }

    public TranslatedTextTypeAPI getSt_63TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_64Ordinal(int ordinal) {
        if(fieldIndex[75] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_64");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[75]);
    }

    public TranslatedTextTypeAPI getSt_64TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_65Ordinal(int ordinal) {
        if(fieldIndex[76] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_65");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[76]);
    }

    public TranslatedTextTypeAPI getSt_65TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_66Ordinal(int ordinal) {
        if(fieldIndex[77] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_66");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[77]);
    }

    public TranslatedTextTypeAPI getSt_66TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_67Ordinal(int ordinal) {
        if(fieldIndex[78] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_67");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[78]);
    }

    public TranslatedTextTypeAPI getSt_67TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_68Ordinal(int ordinal) {
        if(fieldIndex[79] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_68");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[79]);
    }

    public TranslatedTextTypeAPI getSt_68TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_69Ordinal(int ordinal) {
        if(fieldIndex[80] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_69");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[80]);
    }

    public TranslatedTextTypeAPI getSt_69TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_70Ordinal(int ordinal) {
        if(fieldIndex[81] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_70");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[81]);
    }

    public TranslatedTextTypeAPI getSt_70TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_71Ordinal(int ordinal) {
        if(fieldIndex[82] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_71");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[82]);
    }

    public TranslatedTextTypeAPI getSt_71TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_72Ordinal(int ordinal) {
        if(fieldIndex[83] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_72");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[83]);
    }

    public TranslatedTextTypeAPI getSt_72TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_73Ordinal(int ordinal) {
        if(fieldIndex[84] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_73");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[84]);
    }

    public TranslatedTextTypeAPI getSt_73TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_74Ordinal(int ordinal) {
        if(fieldIndex[85] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_74");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[85]);
    }

    public TranslatedTextTypeAPI getSt_74TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_75Ordinal(int ordinal) {
        if(fieldIndex[86] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_75");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[86]);
    }

    public TranslatedTextTypeAPI getSt_75TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_76Ordinal(int ordinal) {
        if(fieldIndex[87] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_76");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[87]);
    }

    public TranslatedTextTypeAPI getSt_76TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_77Ordinal(int ordinal) {
        if(fieldIndex[88] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_77");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[88]);
    }

    public TranslatedTextTypeAPI getSt_77TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_78Ordinal(int ordinal) {
        if(fieldIndex[89] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_78");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[89]);
    }

    public TranslatedTextTypeAPI getSt_78TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_79Ordinal(int ordinal) {
        if(fieldIndex[90] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_79");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[90]);
    }

    public TranslatedTextTypeAPI getSt_79TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_80Ordinal(int ordinal) {
        if(fieldIndex[91] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_80");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[91]);
    }

    public TranslatedTextTypeAPI getSt_80TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_81Ordinal(int ordinal) {
        if(fieldIndex[92] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_81");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[92]);
    }

    public TranslatedTextTypeAPI getSt_81TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_82Ordinal(int ordinal) {
        if(fieldIndex[93] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_82");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[93]);
    }

    public TranslatedTextTypeAPI getSt_82TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_83Ordinal(int ordinal) {
        if(fieldIndex[94] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_83");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[94]);
    }

    public TranslatedTextTypeAPI getSt_83TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_84Ordinal(int ordinal) {
        if(fieldIndex[95] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_84");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[95]);
    }

    public TranslatedTextTypeAPI getSt_84TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_85Ordinal(int ordinal) {
        if(fieldIndex[96] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_85");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[96]);
    }

    public TranslatedTextTypeAPI getSt_85TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_86Ordinal(int ordinal) {
        if(fieldIndex[97] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_86");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[97]);
    }

    public TranslatedTextTypeAPI getSt_86TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_87Ordinal(int ordinal) {
        if(fieldIndex[98] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_87");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[98]);
    }

    public TranslatedTextTypeAPI getSt_87TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_88Ordinal(int ordinal) {
        if(fieldIndex[99] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_88");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[99]);
    }

    public TranslatedTextTypeAPI getSt_88TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_89Ordinal(int ordinal) {
        if(fieldIndex[100] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_89");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[100]);
    }

    public TranslatedTextTypeAPI getSt_89TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_90Ordinal(int ordinal) {
        if(fieldIndex[101] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_90");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[101]);
    }

    public TranslatedTextTypeAPI getSt_90TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_91Ordinal(int ordinal) {
        if(fieldIndex[102] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_91");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[102]);
    }

    public TranslatedTextTypeAPI getSt_91TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_92Ordinal(int ordinal) {
        if(fieldIndex[103] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_92");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[103]);
    }

    public TranslatedTextTypeAPI getSt_92TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_93Ordinal(int ordinal) {
        if(fieldIndex[104] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_93");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[104]);
    }

    public TranslatedTextTypeAPI getSt_93TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_94Ordinal(int ordinal) {
        if(fieldIndex[105] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_94");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[105]);
    }

    public TranslatedTextTypeAPI getSt_94TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_95Ordinal(int ordinal) {
        if(fieldIndex[106] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_95");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[106]);
    }

    public TranslatedTextTypeAPI getSt_95TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_96Ordinal(int ordinal) {
        if(fieldIndex[107] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_96");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[107]);
    }

    public TranslatedTextTypeAPI getSt_96TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_97Ordinal(int ordinal) {
        if(fieldIndex[108] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_97");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[108]);
    }

    public TranslatedTextTypeAPI getSt_97TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_98Ordinal(int ordinal) {
        if(fieldIndex[109] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_98");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[109]);
    }

    public TranslatedTextTypeAPI getSt_98TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_99Ordinal(int ordinal) {
        if(fieldIndex[110] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_99");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[110]);
    }

    public TranslatedTextTypeAPI getSt_99TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_100Ordinal(int ordinal) {
        if(fieldIndex[111] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_100");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[111]);
    }

    public TranslatedTextTypeAPI getSt_100TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_101Ordinal(int ordinal) {
        if(fieldIndex[112] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_101");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[112]);
    }

    public TranslatedTextTypeAPI getSt_101TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_102Ordinal(int ordinal) {
        if(fieldIndex[113] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_102");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[113]);
    }

    public TranslatedTextTypeAPI getSt_102TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_103Ordinal(int ordinal) {
        if(fieldIndex[114] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_103");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[114]);
    }

    public TranslatedTextTypeAPI getSt_103TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_104Ordinal(int ordinal) {
        if(fieldIndex[115] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_104");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[115]);
    }

    public TranslatedTextTypeAPI getSt_104TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_105Ordinal(int ordinal) {
        if(fieldIndex[116] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_105");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[116]);
    }

    public TranslatedTextTypeAPI getSt_105TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_106Ordinal(int ordinal) {
        if(fieldIndex[117] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_106");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[117]);
    }

    public TranslatedTextTypeAPI getSt_106TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_107Ordinal(int ordinal) {
        if(fieldIndex[118] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_107");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[118]);
    }

    public TranslatedTextTypeAPI getSt_107TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_108Ordinal(int ordinal) {
        if(fieldIndex[119] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_108");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[119]);
    }

    public TranslatedTextTypeAPI getSt_108TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_109Ordinal(int ordinal) {
        if(fieldIndex[120] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_109");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[120]);
    }

    public TranslatedTextTypeAPI getSt_109TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_110Ordinal(int ordinal) {
        if(fieldIndex[121] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_110");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[121]);
    }

    public TranslatedTextTypeAPI getSt_110TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_111Ordinal(int ordinal) {
        if(fieldIndex[122] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_111");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[122]);
    }

    public TranslatedTextTypeAPI getSt_111TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_112Ordinal(int ordinal) {
        if(fieldIndex[123] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_112");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[123]);
    }

    public TranslatedTextTypeAPI getSt_112TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_113Ordinal(int ordinal) {
        if(fieldIndex[124] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_113");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[124]);
    }

    public TranslatedTextTypeAPI getSt_113TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_114Ordinal(int ordinal) {
        if(fieldIndex[125] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_114");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[125]);
    }

    public TranslatedTextTypeAPI getSt_114TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_115Ordinal(int ordinal) {
        if(fieldIndex[126] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_115");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[126]);
    }

    public TranslatedTextTypeAPI getSt_115TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_116Ordinal(int ordinal) {
        if(fieldIndex[127] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_116");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[127]);
    }

    public TranslatedTextTypeAPI getSt_116TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_117Ordinal(int ordinal) {
        if(fieldIndex[128] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_117");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[128]);
    }

    public TranslatedTextTypeAPI getSt_117TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_118Ordinal(int ordinal) {
        if(fieldIndex[129] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_118");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[129]);
    }

    public TranslatedTextTypeAPI getSt_118TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_119Ordinal(int ordinal) {
        if(fieldIndex[130] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_119");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[130]);
    }

    public TranslatedTextTypeAPI getSt_119TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_120Ordinal(int ordinal) {
        if(fieldIndex[131] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_120");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[131]);
    }

    public TranslatedTextTypeAPI getSt_120TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_121Ordinal(int ordinal) {
        if(fieldIndex[132] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_121");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[132]);
    }

    public TranslatedTextTypeAPI getSt_121TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_122Ordinal(int ordinal) {
        if(fieldIndex[133] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_122");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[133]);
    }

    public TranslatedTextTypeAPI getSt_122TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_123Ordinal(int ordinal) {
        if(fieldIndex[134] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_123");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[134]);
    }

    public TranslatedTextTypeAPI getSt_123TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_124Ordinal(int ordinal) {
        if(fieldIndex[135] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_124");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[135]);
    }

    public TranslatedTextTypeAPI getSt_124TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_125Ordinal(int ordinal) {
        if(fieldIndex[136] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_125");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[136]);
    }

    public TranslatedTextTypeAPI getSt_125TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_126Ordinal(int ordinal) {
        if(fieldIndex[137] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_126");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[137]);
    }

    public TranslatedTextTypeAPI getSt_126TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_127Ordinal(int ordinal) {
        if(fieldIndex[138] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_127");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[138]);
    }

    public TranslatedTextTypeAPI getSt_127TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_128Ordinal(int ordinal) {
        if(fieldIndex[139] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_128");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[139]);
    }

    public TranslatedTextTypeAPI getSt_128TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_129Ordinal(int ordinal) {
        if(fieldIndex[140] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_129");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[140]);
    }

    public TranslatedTextTypeAPI getSt_129TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_130Ordinal(int ordinal) {
        if(fieldIndex[141] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_130");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[141]);
    }

    public TranslatedTextTypeAPI getSt_130TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_131Ordinal(int ordinal) {
        if(fieldIndex[142] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_131");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[142]);
    }

    public TranslatedTextTypeAPI getSt_131TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_132Ordinal(int ordinal) {
        if(fieldIndex[143] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_132");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[143]);
    }

    public TranslatedTextTypeAPI getSt_132TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_133Ordinal(int ordinal) {
        if(fieldIndex[144] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_133");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[144]);
    }

    public TranslatedTextTypeAPI getSt_133TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_134Ordinal(int ordinal) {
        if(fieldIndex[145] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_134");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[145]);
    }

    public TranslatedTextTypeAPI getSt_134TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_135Ordinal(int ordinal) {
        if(fieldIndex[146] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_135");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[146]);
    }

    public TranslatedTextTypeAPI getSt_135TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_136Ordinal(int ordinal) {
        if(fieldIndex[147] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_136");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[147]);
    }

    public TranslatedTextTypeAPI getSt_136TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_137Ordinal(int ordinal) {
        if(fieldIndex[148] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_137");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[148]);
    }

    public TranslatedTextTypeAPI getSt_137TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_138Ordinal(int ordinal) {
        if(fieldIndex[149] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_138");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[149]);
    }

    public TranslatedTextTypeAPI getSt_138TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_139Ordinal(int ordinal) {
        if(fieldIndex[150] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_139");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[150]);
    }

    public TranslatedTextTypeAPI getSt_139TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_140Ordinal(int ordinal) {
        if(fieldIndex[151] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_140");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[151]);
    }

    public TranslatedTextTypeAPI getSt_140TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_141Ordinal(int ordinal) {
        if(fieldIndex[152] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_141");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[152]);
    }

    public TranslatedTextTypeAPI getSt_141TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_142Ordinal(int ordinal) {
        if(fieldIndex[153] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_142");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[153]);
    }

    public TranslatedTextTypeAPI getSt_142TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_143Ordinal(int ordinal) {
        if(fieldIndex[154] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_143");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[154]);
    }

    public TranslatedTextTypeAPI getSt_143TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_144Ordinal(int ordinal) {
        if(fieldIndex[155] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_144");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[155]);
    }

    public TranslatedTextTypeAPI getSt_144TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_145Ordinal(int ordinal) {
        if(fieldIndex[156] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_145");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[156]);
    }

    public TranslatedTextTypeAPI getSt_145TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_146Ordinal(int ordinal) {
        if(fieldIndex[157] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_146");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[157]);
    }

    public TranslatedTextTypeAPI getSt_146TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_147Ordinal(int ordinal) {
        if(fieldIndex[158] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_147");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[158]);
    }

    public TranslatedTextTypeAPI getSt_147TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_148Ordinal(int ordinal) {
        if(fieldIndex[159] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_148");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[159]);
    }

    public TranslatedTextTypeAPI getSt_148TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_149Ordinal(int ordinal) {
        if(fieldIndex[160] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_149");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[160]);
    }

    public TranslatedTextTypeAPI getSt_149TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_150Ordinal(int ordinal) {
        if(fieldIndex[161] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_150");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[161]);
    }

    public TranslatedTextTypeAPI getSt_150TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_151Ordinal(int ordinal) {
        if(fieldIndex[162] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_151");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[162]);
    }

    public TranslatedTextTypeAPI getSt_151TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_152Ordinal(int ordinal) {
        if(fieldIndex[163] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_152");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[163]);
    }

    public TranslatedTextTypeAPI getSt_152TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_153Ordinal(int ordinal) {
        if(fieldIndex[164] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_153");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[164]);
    }

    public TranslatedTextTypeAPI getSt_153TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_154Ordinal(int ordinal) {
        if(fieldIndex[165] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_154");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[165]);
    }

    public TranslatedTextTypeAPI getSt_154TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_155Ordinal(int ordinal) {
        if(fieldIndex[166] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_155");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[166]);
    }

    public TranslatedTextTypeAPI getSt_155TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_156Ordinal(int ordinal) {
        if(fieldIndex[167] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_156");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[167]);
    }

    public TranslatedTextTypeAPI getSt_156TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_157Ordinal(int ordinal) {
        if(fieldIndex[168] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_157");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[168]);
    }

    public TranslatedTextTypeAPI getSt_157TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_158Ordinal(int ordinal) {
        if(fieldIndex[169] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_158");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[169]);
    }

    public TranslatedTextTypeAPI getSt_158TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_159Ordinal(int ordinal) {
        if(fieldIndex[170] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_159");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[170]);
    }

    public TranslatedTextTypeAPI getSt_159TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_160Ordinal(int ordinal) {
        if(fieldIndex[171] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_160");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[171]);
    }

    public TranslatedTextTypeAPI getSt_160TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_161Ordinal(int ordinal) {
        if(fieldIndex[172] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_161");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[172]);
    }

    public TranslatedTextTypeAPI getSt_161TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_162Ordinal(int ordinal) {
        if(fieldIndex[173] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_162");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[173]);
    }

    public TranslatedTextTypeAPI getSt_162TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_163Ordinal(int ordinal) {
        if(fieldIndex[174] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_163");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[174]);
    }

    public TranslatedTextTypeAPI getSt_163TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_164Ordinal(int ordinal) {
        if(fieldIndex[175] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_164");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[175]);
    }

    public TranslatedTextTypeAPI getSt_164TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_165Ordinal(int ordinal) {
        if(fieldIndex[176] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_165");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[176]);
    }

    public TranslatedTextTypeAPI getSt_165TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_166Ordinal(int ordinal) {
        if(fieldIndex[177] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_166");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[177]);
    }

    public TranslatedTextTypeAPI getSt_166TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_167Ordinal(int ordinal) {
        if(fieldIndex[178] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_167");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[178]);
    }

    public TranslatedTextTypeAPI getSt_167TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_168Ordinal(int ordinal) {
        if(fieldIndex[179] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_168");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[179]);
    }

    public TranslatedTextTypeAPI getSt_168TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_169Ordinal(int ordinal) {
        if(fieldIndex[180] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_169");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[180]);
    }

    public TranslatedTextTypeAPI getSt_169TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_170Ordinal(int ordinal) {
        if(fieldIndex[181] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_170");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[181]);
    }

    public TranslatedTextTypeAPI getSt_170TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_171Ordinal(int ordinal) {
        if(fieldIndex[182] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_171");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[182]);
    }

    public TranslatedTextTypeAPI getSt_171TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_172Ordinal(int ordinal) {
        if(fieldIndex[183] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_172");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[183]);
    }

    public TranslatedTextTypeAPI getSt_172TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_173Ordinal(int ordinal) {
        if(fieldIndex[184] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_173");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[184]);
    }

    public TranslatedTextTypeAPI getSt_173TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_174Ordinal(int ordinal) {
        if(fieldIndex[185] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_174");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[185]);
    }

    public TranslatedTextTypeAPI getSt_174TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_175Ordinal(int ordinal) {
        if(fieldIndex[186] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_175");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[186]);
    }

    public TranslatedTextTypeAPI getSt_175TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_176Ordinal(int ordinal) {
        if(fieldIndex[187] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_176");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[187]);
    }

    public TranslatedTextTypeAPI getSt_176TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_177Ordinal(int ordinal) {
        if(fieldIndex[188] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_177");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[188]);
    }

    public TranslatedTextTypeAPI getSt_177TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_178Ordinal(int ordinal) {
        if(fieldIndex[189] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_178");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[189]);
    }

    public TranslatedTextTypeAPI getSt_178TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_179Ordinal(int ordinal) {
        if(fieldIndex[190] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_179");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[190]);
    }

    public TranslatedTextTypeAPI getSt_179TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_180Ordinal(int ordinal) {
        if(fieldIndex[191] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_180");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[191]);
    }

    public TranslatedTextTypeAPI getSt_180TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_181Ordinal(int ordinal) {
        if(fieldIndex[192] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_181");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[192]);
    }

    public TranslatedTextTypeAPI getSt_181TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_182Ordinal(int ordinal) {
        if(fieldIndex[193] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_182");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[193]);
    }

    public TranslatedTextTypeAPI getSt_182TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_183Ordinal(int ordinal) {
        if(fieldIndex[194] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_183");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[194]);
    }

    public TranslatedTextTypeAPI getSt_183TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_184Ordinal(int ordinal) {
        if(fieldIndex[195] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_184");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[195]);
    }

    public TranslatedTextTypeAPI getSt_184TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_185Ordinal(int ordinal) {
        if(fieldIndex[196] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_185");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[196]);
    }

    public TranslatedTextTypeAPI getSt_185TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_186Ordinal(int ordinal) {
        if(fieldIndex[197] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_186");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[197]);
    }

    public TranslatedTextTypeAPI getSt_186TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_187Ordinal(int ordinal) {
        if(fieldIndex[198] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_187");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[198]);
    }

    public TranslatedTextTypeAPI getSt_187TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_188Ordinal(int ordinal) {
        if(fieldIndex[199] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_188");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[199]);
    }

    public TranslatedTextTypeAPI getSt_188TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_189Ordinal(int ordinal) {
        if(fieldIndex[200] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_189");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[200]);
    }

    public TranslatedTextTypeAPI getSt_189TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_190Ordinal(int ordinal) {
        if(fieldIndex[201] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_190");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[201]);
    }

    public TranslatedTextTypeAPI getSt_190TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_191Ordinal(int ordinal) {
        if(fieldIndex[202] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_191");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[202]);
    }

    public TranslatedTextTypeAPI getSt_191TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_192Ordinal(int ordinal) {
        if(fieldIndex[203] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_192");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[203]);
    }

    public TranslatedTextTypeAPI getSt_192TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_193Ordinal(int ordinal) {
        if(fieldIndex[204] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_193");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[204]);
    }

    public TranslatedTextTypeAPI getSt_193TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_194Ordinal(int ordinal) {
        if(fieldIndex[205] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_194");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[205]);
    }

    public TranslatedTextTypeAPI getSt_194TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_195Ordinal(int ordinal) {
        if(fieldIndex[206] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_195");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[206]);
    }

    public TranslatedTextTypeAPI getSt_195TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_196Ordinal(int ordinal) {
        if(fieldIndex[207] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_196");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[207]);
    }

    public TranslatedTextTypeAPI getSt_196TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_197Ordinal(int ordinal) {
        if(fieldIndex[208] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_197");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[208]);
    }

    public TranslatedTextTypeAPI getSt_197TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_198Ordinal(int ordinal) {
        if(fieldIndex[209] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_198");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[209]);
    }

    public TranslatedTextTypeAPI getSt_198TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_199Ordinal(int ordinal) {
        if(fieldIndex[210] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_199");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[210]);
    }

    public TranslatedTextTypeAPI getSt_199TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_200Ordinal(int ordinal) {
        if(fieldIndex[211] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_200");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[211]);
    }

    public TranslatedTextTypeAPI getSt_200TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_201Ordinal(int ordinal) {
        if(fieldIndex[212] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_201");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[212]);
    }

    public TranslatedTextTypeAPI getSt_201TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_202Ordinal(int ordinal) {
        if(fieldIndex[213] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_202");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[213]);
    }

    public TranslatedTextTypeAPI getSt_202TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_203Ordinal(int ordinal) {
        if(fieldIndex[214] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_203");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[214]);
    }

    public TranslatedTextTypeAPI getSt_203TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_204Ordinal(int ordinal) {
        if(fieldIndex[215] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_204");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[215]);
    }

    public TranslatedTextTypeAPI getSt_204TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_205Ordinal(int ordinal) {
        if(fieldIndex[216] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_205");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[216]);
    }

    public TranslatedTextTypeAPI getSt_205TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_206Ordinal(int ordinal) {
        if(fieldIndex[217] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_206");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[217]);
    }

    public TranslatedTextTypeAPI getSt_206TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_207Ordinal(int ordinal) {
        if(fieldIndex[218] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_207");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[218]);
    }

    public TranslatedTextTypeAPI getSt_207TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_208Ordinal(int ordinal) {
        if(fieldIndex[219] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_208");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[219]);
    }

    public TranslatedTextTypeAPI getSt_208TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_209Ordinal(int ordinal) {
        if(fieldIndex[220] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_209");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[220]);
    }

    public TranslatedTextTypeAPI getSt_209TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_210Ordinal(int ordinal) {
        if(fieldIndex[221] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_210");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[221]);
    }

    public TranslatedTextTypeAPI getSt_210TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_211Ordinal(int ordinal) {
        if(fieldIndex[222] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_211");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[222]);
    }

    public TranslatedTextTypeAPI getSt_211TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_212Ordinal(int ordinal) {
        if(fieldIndex[223] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_212");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[223]);
    }

    public TranslatedTextTypeAPI getSt_212TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_213Ordinal(int ordinal) {
        if(fieldIndex[224] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_213");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[224]);
    }

    public TranslatedTextTypeAPI getSt_213TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_214Ordinal(int ordinal) {
        if(fieldIndex[225] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_214");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[225]);
    }

    public TranslatedTextTypeAPI getSt_214TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_215Ordinal(int ordinal) {
        if(fieldIndex[226] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_215");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[226]);
    }

    public TranslatedTextTypeAPI getSt_215TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_216Ordinal(int ordinal) {
        if(fieldIndex[227] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_216");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[227]);
    }

    public TranslatedTextTypeAPI getSt_216TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_217Ordinal(int ordinal) {
        if(fieldIndex[228] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_217");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[228]);
    }

    public TranslatedTextTypeAPI getSt_217TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_218Ordinal(int ordinal) {
        if(fieldIndex[229] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_218");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[229]);
    }

    public TranslatedTextTypeAPI getSt_218TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_219Ordinal(int ordinal) {
        if(fieldIndex[230] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_219");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[230]);
    }

    public TranslatedTextTypeAPI getSt_219TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_220Ordinal(int ordinal) {
        if(fieldIndex[231] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_220");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[231]);
    }

    public TranslatedTextTypeAPI getSt_220TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_221Ordinal(int ordinal) {
        if(fieldIndex[232] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_221");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[232]);
    }

    public TranslatedTextTypeAPI getSt_221TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_222Ordinal(int ordinal) {
        if(fieldIndex[233] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_222");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[233]);
    }

    public TranslatedTextTypeAPI getSt_222TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_223Ordinal(int ordinal) {
        if(fieldIndex[234] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_223");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[234]);
    }

    public TranslatedTextTypeAPI getSt_223TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_224Ordinal(int ordinal) {
        if(fieldIndex[235] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_224");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[235]);
    }

    public TranslatedTextTypeAPI getSt_224TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_225Ordinal(int ordinal) {
        if(fieldIndex[236] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_225");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[236]);
    }

    public TranslatedTextTypeAPI getSt_225TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_226Ordinal(int ordinal) {
        if(fieldIndex[237] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_226");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[237]);
    }

    public TranslatedTextTypeAPI getSt_226TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_227Ordinal(int ordinal) {
        if(fieldIndex[238] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_227");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[238]);
    }

    public TranslatedTextTypeAPI getSt_227TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_228Ordinal(int ordinal) {
        if(fieldIndex[239] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_228");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[239]);
    }

    public TranslatedTextTypeAPI getSt_228TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_229Ordinal(int ordinal) {
        if(fieldIndex[240] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_229");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[240]);
    }

    public TranslatedTextTypeAPI getSt_229TypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSt_230Ordinal(int ordinal) {
        if(fieldIndex[241] == -1)
            return missingDataHandler().handleReferencedOrdinal("TurboCollections", ordinal, "st_230");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[241]);
    }

    public TranslatedTextTypeAPI getSt_230TypeAPI() {
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