package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class PackageMovieDealCountryGroup extends HollowObject {

    public PackageMovieDealCountryGroup(PackageMovieDealCountryGroupDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Long getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public long getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public HLong getPackageIdHollowReference() {
        int refOrdinal = delegate().getPackageIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHLong(refOrdinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public HLong getMovieIdHollowReference() {
        int refOrdinal = delegate().getMovieIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHLong(refOrdinal);
    }

    public String getPackageType() {
        return delegate().getPackageType(ordinal);
    }

    public boolean isPackageTypeEqual(String testValue) {
        return delegate().isPackageTypeEqual(ordinal, testValue);
    }

    public HString getPackageTypeHollowReference() {
        int refOrdinal = delegate().getPackageTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getPackageStatus() {
        return delegate().getPackageStatus(ordinal);
    }

    public boolean isPackageStatusEqual(String testValue) {
        return delegate().isPackageStatusEqual(ordinal, testValue);
    }

    public HString getPackageStatusHollowReference() {
        int refOrdinal = delegate().getPackageStatusOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public ListOfDealCountryGroup getDealCountryGroups() {
        int refOrdinal = delegate().getDealCountryGroupsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfDealCountryGroup(refOrdinal);
    }

    public ListOfPackageTags getTags() {
        int refOrdinal = delegate().getTagsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfPackageTags(refOrdinal);
    }

    public boolean getDefaultPackage() {
        return delegate().getDefaultPackage(ordinal);
    }

    public Boolean getDefaultPackageBoxed() {
        return delegate().getDefaultPackageBoxed(ordinal);
    }

    public PackageDealCountryAPI api() {
        return typeApi().getAPI();
    }

    public PackageMovieDealCountryGroupTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PackageMovieDealCountryGroupDelegate delegate() {
        return (PackageMovieDealCountryGroupDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code PackageMovieDealCountryGroup} that has a primary key.
     * The primary key is represented by the class {@link PackageMovieDealCountryGroup.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<PackageMovieDealCountryGroup, Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, PackageMovieDealCountryGroup.class)
            .bindToPrimaryKey()
            .usingBean(PackageMovieDealCountryGroup.Key.class);
    }

    public static class Key {
        @FieldPath("movieId")
        public final long movieId;

        @FieldPath("packageId")
        public final long packageId;

        public Key(long movieId, long packageId) {
            this.movieId = movieId;
            this.packageId = packageId;
        }
    }

}