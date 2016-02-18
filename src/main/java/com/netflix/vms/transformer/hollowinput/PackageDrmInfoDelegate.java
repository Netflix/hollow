package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PackageDrmInfoDelegate extends HollowObjectDelegate {

    public int getContentPackagerPublicKeyOrdinal(int ordinal);

    public int getKeySeedOrdinal(int ordinal);

    public long getKeyId(int ordinal);

    public Long getKeyIdBoxed(int ordinal);

    public long getDrmKeyGroup(int ordinal);

    public Long getDrmKeyGroupBoxed(int ordinal);

    public int getKeyOrdinal(int ordinal);

    public int getDrmHeaderInfoOrdinal(int ordinal);

    public PackageDrmInfoTypeAPI getTypeAPI();

}