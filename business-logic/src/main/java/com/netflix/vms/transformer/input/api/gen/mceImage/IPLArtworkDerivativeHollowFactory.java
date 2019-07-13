package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class IPLArtworkDerivativeHollowFactory<T extends IPLArtworkDerivative> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new IPLArtworkDerivative(((IPLArtworkDerivativeTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new IPLArtworkDerivative(new IPLArtworkDerivativeDelegateCachedImpl((IPLArtworkDerivativeTypeAPI)typeAPI, ordinal), ordinal);
    }

}