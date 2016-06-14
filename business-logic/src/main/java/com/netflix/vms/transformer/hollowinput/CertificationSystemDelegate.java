package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CertificationSystemDelegate extends HollowObjectDelegate {

    public long getCertificationSystemId(int ordinal);

    public Long getCertificationSystemIdBoxed(int ordinal);

    public int getCountryCodeOrdinal(int ordinal);

    public int getRatingOrdinal(int ordinal);

    public int getOfficialURLOrdinal(int ordinal);

    public CertificationSystemTypeAPI getTypeAPI();

}