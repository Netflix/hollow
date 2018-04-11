package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="ConsolidatedVideoRating")
public class ConsolidatedVideoRating implements Cloneable {

    @HollowTypeName(name="ConsolidatedVideoCountryRatingList")
    public List<ConsolidatedVideoCountryRating> countryRatings = null;
    @HollowTypeName(name="ISOCountryList")
    public List<ISOCountry> countryList = null;

    public ConsolidatedVideoRating setCountryRatings(List<ConsolidatedVideoCountryRating> countryRatings) {
        this.countryRatings = countryRatings;
        return this;
    }
    public ConsolidatedVideoRating setCountryList(List<ISOCountry> countryList) {
        this.countryList = countryList;
        return this;
    }
    public ConsolidatedVideoRating addToCountryRatings(ConsolidatedVideoCountryRating consolidatedVideoCountryRating) {
        if (this.countryRatings == null) {
            this.countryRatings = new ArrayList<ConsolidatedVideoCountryRating>();
        }
        this.countryRatings.add(consolidatedVideoCountryRating);
        return this;
    }
    public ConsolidatedVideoRating addToCountryList(ISOCountry iSOCountry) {
        if (this.countryList == null) {
            this.countryList = new ArrayList<ISOCountry>();
        }
        this.countryList.add(iSOCountry);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ConsolidatedVideoRating))
            return false;

        ConsolidatedVideoRating o = (ConsolidatedVideoRating) other;
        if(o.countryRatings == null) {
            if(countryRatings != null) return false;
        } else if(!o.countryRatings.equals(countryRatings)) return false;
        if(o.countryList == null) {
            if(countryList != null) return false;
        } else if(!o.countryList.equals(countryList)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (countryRatings == null ? 1237 : countryRatings.hashCode());
        hashCode = hashCode * 31 + (countryList == null ? 1237 : countryList.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ConsolidatedVideoRating{");
        builder.append("countryRatings=").append(countryRatings);
        builder.append(",countryList=").append(countryList);
        builder.append("}");
        return builder.toString();
    }

    public ConsolidatedVideoRating clone() {
        try {
            ConsolidatedVideoRating clone = (ConsolidatedVideoRating)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}