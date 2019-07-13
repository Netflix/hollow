package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class ReleaseDate extends HollowObject {

    public ReleaseDate(ReleaseDateDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getReleaseDateType() {
        return delegate().getReleaseDateType(ordinal);
    }

    public boolean isReleaseDateTypeEqual(String testValue) {
        return delegate().isReleaseDateTypeEqual(ordinal, testValue);
    }

    public HString getReleaseDateTypeHollowReference() {
        int refOrdinal = delegate().getReleaseDateTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getDistributorName() {
        return delegate().getDistributorName(ordinal);
    }

    public boolean isDistributorNameEqual(String testValue) {
        return delegate().isDistributorNameEqual(ordinal, testValue);
    }

    public HString getDistributorNameHollowReference() {
        int refOrdinal = delegate().getDistributorNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public int getMonth() {
        return delegate().getMonth(ordinal);
    }

    public Integer getMonthBoxed() {
        return delegate().getMonthBoxed(ordinal);
    }

    public int getYear() {
        return delegate().getYear(ordinal);
    }

    public Integer getYearBoxed() {
        return delegate().getYearBoxed(ordinal);
    }

    public int getDay() {
        return delegate().getDay(ordinal);
    }

    public Integer getDayBoxed() {
        return delegate().getDayBoxed(ordinal);
    }

    public String getBcp47code() {
        return delegate().getBcp47code(ordinal);
    }

    public boolean isBcp47codeEqual(String testValue) {
        return delegate().isBcp47codeEqual(ordinal, testValue);
    }

    public HString getBcp47codeHollowReference() {
        int refOrdinal = delegate().getBcp47codeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public VideoDateAPI api() {
        return typeApi().getAPI();
    }

    public ReleaseDateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ReleaseDateDelegate delegate() {
        return (ReleaseDateDelegate)delegate;
    }

}