package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="StreamDeploymentInfo")
public class StreamDeploymentInfo implements Cloneable {

    @HollowTypeName(name="ISOCountrySet")
    public Set<ISOCountry> cacheDeployedCountries = null;
    @HollowTypeName(name="CdnDeploymentSet")
    public Set<CdnDeployment> cdnDeployments = null;

    public StreamDeploymentInfo setCacheDeployedCountries(Set<ISOCountry> cacheDeployedCountries) {
        this.cacheDeployedCountries = cacheDeployedCountries;
        return this;
    }
    public StreamDeploymentInfo setCdnDeployments(Set<CdnDeployment> cdnDeployments) {
        this.cdnDeployments = cdnDeployments;
        return this;
    }
    public StreamDeploymentInfo addToCacheDeployedCountries(ISOCountry iSOCountry) {
        if (this.cacheDeployedCountries == null) {
            this.cacheDeployedCountries = new HashSet<ISOCountry>();
        }
        this.cacheDeployedCountries.add(iSOCountry);
        return this;
    }
    public StreamDeploymentInfo addToCdnDeployments(CdnDeployment cdnDeployment) {
        if (this.cdnDeployments == null) {
            this.cdnDeployments = new HashSet<CdnDeployment>();
        }
        this.cdnDeployments.add(cdnDeployment);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDeploymentInfo))
            return false;

        StreamDeploymentInfo o = (StreamDeploymentInfo) other;
        if(o.cacheDeployedCountries == null) {
            if(cacheDeployedCountries != null) return false;
        } else if(!o.cacheDeployedCountries.equals(cacheDeployedCountries)) return false;
        if(o.cdnDeployments == null) {
            if(cdnDeployments != null) return false;
        } else if(!o.cdnDeployments.equals(cdnDeployments)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (cacheDeployedCountries == null ? 1237 : cacheDeployedCountries.hashCode());
        hashCode = hashCode * 31 + (cdnDeployments == null ? 1237 : cdnDeployments.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamDeploymentInfo{");
        builder.append("cacheDeployedCountries=").append(cacheDeployedCountries);
        builder.append(",cdnDeployments=").append(cdnDeployments);
        builder.append("}");
        return builder.toString();
    }

    public StreamDeploymentInfo clone() {
        try {
            StreamDeploymentInfo clone = (StreamDeploymentInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}