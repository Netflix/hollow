package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractAsset extends HollowObject {

    public RightsContractAsset(RightsContractAssetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getBcp47Code() {
        return delegate().getBcp47Code(ordinal);
    }

    public boolean isBcp47CodeEqual(String testValue) {
        return delegate().isBcp47CodeEqual(ordinal, testValue);
    }

    public HString getBcp47CodeHollowReference() {
        int refOrdinal = delegate().getBcp47CodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getAssetType() {
        return delegate().getAssetType(ordinal);
    }

    public boolean isAssetTypeEqual(String testValue) {
        return delegate().isAssetTypeEqual(ordinal, testValue);
    }

    public HString getAssetTypeHollowReference() {
        int refOrdinal = delegate().getAssetTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public RightsContractAssetTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsContractAssetDelegate delegate() {
        return (RightsContractAssetDelegate)delegate;
    }

}