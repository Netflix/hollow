package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface TurboCollectionsDelegate extends HollowObjectDelegate {

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public int getPrefixOrdinal(int ordinal);

    public int getChar_nOrdinal(int ordinal);

    public int getNav_snOrdinal(int ordinal);

    public int getDnOrdinal(int ordinal);

    public int getKc_cnOrdinal(int ordinal);

    public int getSt_2Ordinal(int ordinal);

    public int getBmt_nOrdinal(int ordinal);

    public int getSt_1Ordinal(int ordinal);

    public int getSt_4Ordinal(int ordinal);

    public int getSt_3Ordinal(int ordinal);

    public int getSt_0Ordinal(int ordinal);

    public int getSt_9Ordinal(int ordinal);

    public int getSnOrdinal(int ordinal);

    public int getKag_knOrdinal(int ordinal);

    public int getRoar_nOrdinal(int ordinal);

    public int getSt_6Ordinal(int ordinal);

    public int getSt_5Ordinal(int ordinal);

    public int getSt_8Ordinal(int ordinal);

    public int getTdnOrdinal(int ordinal);

    public int getSt_7Ordinal(int ordinal);

    public TurboCollectionsTypeAPI getTypeAPI();

}