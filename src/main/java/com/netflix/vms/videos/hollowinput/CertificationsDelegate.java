package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CertificationsDelegate extends HollowObjectDelegate {

    public int getNameOrdinal(int ordinal);

    public int getDescriptionOrdinal(int ordinal);

    public long getCertificationTypeId(int ordinal);

    public Long getCertificationTypeIdBoxed(int ordinal);

    public CertificationsTypeAPI getTypeAPI();

}