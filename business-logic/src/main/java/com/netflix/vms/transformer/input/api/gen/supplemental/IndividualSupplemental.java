package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class IndividualSupplemental extends HollowObject {

    public IndividualSupplemental(IndividualSupplementalDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public int getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public String getSubType() {
        return delegate().getSubType(ordinal);
    }

    public boolean isSubTypeEqual(String testValue) {
        return delegate().isSubTypeEqual(ordinal, testValue);
    }

    public HString getSubTypeHollowReference() {
        int refOrdinal = delegate().getSubTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public IndividualSupplementalThemeSet getThemes() {
        int refOrdinal = delegate().getThemesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getIndividualSupplementalThemeSet(refOrdinal);
    }

    public IndividualSupplementalIdentifierSet getIdentifiers() {
        int refOrdinal = delegate().getIdentifiersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getIndividualSupplementalIdentifierSet(refOrdinal);
    }

    public IndividualSupplementalUsageSet getUsages() {
        int refOrdinal = delegate().getUsagesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getIndividualSupplementalUsageSet(refOrdinal);
    }

    public boolean getPostplay() {
        return delegate().getPostplay(ordinal);
    }

    public Boolean getPostplayBoxed() {
        return delegate().getPostplayBoxed(ordinal);
    }

    public boolean getGeneral() {
        return delegate().getGeneral(ordinal);
    }

    public Boolean getGeneralBoxed() {
        return delegate().getGeneralBoxed(ordinal);
    }

    public boolean getThematic() {
        return delegate().getThematic(ordinal);
    }

    public Boolean getThematicBoxed() {
        return delegate().getThematicBoxed(ordinal);
    }

    public boolean getApprovedForExploit() {
        return delegate().getApprovedForExploit(ordinal);
    }

    public Boolean getApprovedForExploitBoxed() {
        return delegate().getApprovedForExploitBoxed(ordinal);
    }

    public SupplementalAPI api() {
        return typeApi().getAPI();
    }

    public IndividualSupplementalTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IndividualSupplementalDelegate delegate() {
        return (IndividualSupplementalDelegate)delegate;
    }

}