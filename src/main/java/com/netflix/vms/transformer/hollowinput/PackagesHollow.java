package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PackagesHollow extends HollowObject {

    public PackagesHollow(PackagesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public Long _getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public PackageMomentListHollow _getMoments() {
        int refOrdinal = delegate().getMomentsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPackageMomentListHollow(refOrdinal);
    }

    public PackageDrmInfoListHollow _getDrmInfo() {
        int refOrdinal = delegate().getDrmInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPackageDrmInfoListHollow(refOrdinal);
    }

    public PackageStreamSetHollow _getDownloadables() {
        int refOrdinal = delegate().getDownloadablesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPackageStreamSetHollow(refOrdinal);
    }

    public StringHollow _getDefaultS3PathComponent() {
        int refOrdinal = delegate().getDefaultS3PathComponentOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PackagesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PackagesDelegate delegate() {
        return (PackagesDelegate)delegate;
    }

}