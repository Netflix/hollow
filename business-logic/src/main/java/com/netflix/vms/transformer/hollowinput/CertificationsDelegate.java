package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CertificationsDelegate extends HollowObjectDelegate {

    public long getCertificationTypeId(int ordinal);

    public Long getCertificationTypeIdBoxed(int ordinal);

    public int getNameOrdinal(int ordinal);

    public int getDescriptionOrdinal(int ordinal);

    public CertificationsTypeAPI getTypeAPI();

}