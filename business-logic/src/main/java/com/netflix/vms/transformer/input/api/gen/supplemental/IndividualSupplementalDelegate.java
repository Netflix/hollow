package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IndividualSupplementalDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getSequenceNumber(int ordinal);

    public Integer getSequenceNumberBoxed(int ordinal);

    public String getSubType(int ordinal);

    public boolean isSubTypeEqual(int ordinal, String testValue);

    public int getSubTypeOrdinal(int ordinal);

    public int getThemesOrdinal(int ordinal);

    public int getIdentifiersOrdinal(int ordinal);

    public int getUsagesOrdinal(int ordinal);

    public boolean getPostplay(int ordinal);

    public Boolean getPostplayBoxed(int ordinal);

    public boolean getGeneral(int ordinal);

    public Boolean getGeneralBoxed(int ordinal);

    public boolean getThematic(int ordinal);

    public Boolean getThematicBoxed(int ordinal);

    public boolean getApprovedForExploit(int ordinal);

    public Boolean getApprovedForExploitBoxed(int ordinal);

    public IndividualSupplementalTypeAPI getTypeAPI();

}