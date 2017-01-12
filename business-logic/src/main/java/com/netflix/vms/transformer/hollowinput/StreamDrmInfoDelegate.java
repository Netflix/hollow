package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamDrmInfoDelegate extends HollowObjectDelegate {

    public int getKeyIdOrdinal(int ordinal);

    public int getKeyOrdinal(int ordinal);

    public int getContentPackagerPublicKeyOrdinal(int ordinal);

    public int getKeySeedOrdinal(int ordinal);

    public int getTypeOrdinal(int ordinal);

    public StreamDrmInfoTypeAPI getTypeAPI();

}