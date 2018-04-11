package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="Package")
public class Package implements Cloneable {

    public long packageId = java.lang.Long.MIN_VALUE;
    public long movieId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="PackageMomentList")
    public List<PackageMoment> moments = null;
    @HollowTypeName(name="PackageDrmInfoList")
    public List<PackageDrmInfo> drmInfo = null;
    @HollowTypeName(name="PackageStreamSet")
    public Set<PackageStream> downloadables = null;
    public String defaultS3PathComponent = null;

    public Package setPackageId(long packageId) {
        this.packageId = packageId;
        return this;
    }
    public Package setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public Package setMoments(List<PackageMoment> moments) {
        this.moments = moments;
        return this;
    }
    public Package setDrmInfo(List<PackageDrmInfo> drmInfo) {
        this.drmInfo = drmInfo;
        return this;
    }
    public Package setDownloadables(Set<PackageStream> downloadables) {
        this.downloadables = downloadables;
        return this;
    }
    public Package setDefaultS3PathComponent(String defaultS3PathComponent) {
        this.defaultS3PathComponent = defaultS3PathComponent;
        return this;
    }
    public Package addToMoments(PackageMoment packageMoment) {
        if (this.moments == null) {
            this.moments = new ArrayList<PackageMoment>();
        }
        this.moments.add(packageMoment);
        return this;
    }
    public Package addToDrmInfo(PackageDrmInfo packageDrmInfo) {
        if (this.drmInfo == null) {
            this.drmInfo = new ArrayList<PackageDrmInfo>();
        }
        this.drmInfo.add(packageDrmInfo);
        return this;
    }
    public Package addToDownloadables(PackageStream packageStream) {
        if (this.downloadables == null) {
            this.downloadables = new HashSet<PackageStream>();
        }
        this.downloadables.add(packageStream);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Package))
            return false;

        Package o = (Package) other;
        if(o.packageId != packageId) return false;
        if(o.movieId != movieId) return false;
        if(o.moments == null) {
            if(moments != null) return false;
        } else if(!o.moments.equals(moments)) return false;
        if(o.drmInfo == null) {
            if(drmInfo != null) return false;
        } else if(!o.drmInfo.equals(drmInfo)) return false;
        if(o.downloadables == null) {
            if(downloadables != null) return false;
        } else if(!o.downloadables.equals(downloadables)) return false;
        if(o.defaultS3PathComponent == null) {
            if(defaultS3PathComponent != null) return false;
        } else if(!o.defaultS3PathComponent.equals(defaultS3PathComponent)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (packageId ^ (packageId >>> 32));
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (moments == null ? 1237 : moments.hashCode());
        hashCode = hashCode * 31 + (drmInfo == null ? 1237 : drmInfo.hashCode());
        hashCode = hashCode * 31 + (downloadables == null ? 1237 : downloadables.hashCode());
        hashCode = hashCode * 31 + (defaultS3PathComponent == null ? 1237 : defaultS3PathComponent.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Package{");
        builder.append("packageId=").append(packageId);
        builder.append(",movieId=").append(movieId);
        builder.append(",moments=").append(moments);
        builder.append(",drmInfo=").append(drmInfo);
        builder.append(",downloadables=").append(downloadables);
        builder.append(",defaultS3PathComponent=").append(defaultS3PathComponent);
        builder.append("}");
        return builder.toString();
    }

    public Package clone() {
        try {
            Package clone = (Package)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}