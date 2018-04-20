package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="DeployablePackages")
public class DeployablePackages implements Cloneable {

    public long packageId = java.lang.Long.MIN_VALUE;
    public long movieId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="ISOCountrySet")
    public Set<ISOCountry> countryCodes = null;
    @HollowTypeName(name="ListOfPackageTags")
    public List<String> tags = null;
    public boolean defaultPackage = false;

    public DeployablePackages setPackageId(long packageId) {
        this.packageId = packageId;
        return this;
    }
    public DeployablePackages setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public DeployablePackages setCountryCodes(Set<ISOCountry> countryCodes) {
        this.countryCodes = countryCodes;
        return this;
    }
    public DeployablePackages setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }
    public DeployablePackages setDefaultPackage(boolean defaultPackage) {
        this.defaultPackage = defaultPackage;
        return this;
    }
    public DeployablePackages addToCountryCodes(ISOCountry iSOCountry) {
        if (this.countryCodes == null) {
            this.countryCodes = new HashSet<ISOCountry>();
        }
        this.countryCodes.add(iSOCountry);
        return this;
    }
    public DeployablePackages addToTags(String string) {
        if (this.tags == null) {
            this.tags = new ArrayList<String>();
        }
        this.tags.add(string);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DeployablePackages))
            return false;

        DeployablePackages o = (DeployablePackages) other;
        if(o.packageId != packageId) return false;
        if(o.movieId != movieId) return false;
        if(o.countryCodes == null) {
            if(countryCodes != null) return false;
        } else if(!o.countryCodes.equals(countryCodes)) return false;
        if(o.tags == null) {
            if(tags != null) return false;
        } else if(!o.tags.equals(tags)) return false;
        if(o.defaultPackage != defaultPackage) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (packageId ^ (packageId >>> 32));
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (countryCodes == null ? 1237 : countryCodes.hashCode());
        hashCode = hashCode * 31 + (tags == null ? 1237 : tags.hashCode());
        hashCode = hashCode * 31 + (defaultPackage? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DeployablePackages{");
        builder.append("packageId=").append(packageId);
        builder.append(",movieId=").append(movieId);
        builder.append(",countryCodes=").append(countryCodes);
        builder.append(",tags=").append(tags);
        builder.append(",defaultPackage=").append(defaultPackage);
        builder.append("}");
        return builder.toString();
    }

    public DeployablePackages clone() {
        try {
            DeployablePackages clone = (DeployablePackages)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}