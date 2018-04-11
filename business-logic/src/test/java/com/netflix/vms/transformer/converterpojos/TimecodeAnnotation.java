package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="TimecodeAnnotation")
public class TimecodeAnnotation implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public long packageId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="TimecodeAnnotationsList")
    public List<TimecodedMomentAnnotation> timecodeAnnotations = null;

    public TimecodeAnnotation setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public TimecodeAnnotation setPackageId(long packageId) {
        this.packageId = packageId;
        return this;
    }
    public TimecodeAnnotation setTimecodeAnnotations(List<TimecodedMomentAnnotation> timecodeAnnotations) {
        this.timecodeAnnotations = timecodeAnnotations;
        return this;
    }
    public TimecodeAnnotation addToTimecodeAnnotations(TimecodedMomentAnnotation timecodedMomentAnnotation) {
        if (this.timecodeAnnotations == null) {
            this.timecodeAnnotations = new ArrayList<TimecodedMomentAnnotation>();
        }
        this.timecodeAnnotations.add(timecodedMomentAnnotation);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TimecodeAnnotation))
            return false;

        TimecodeAnnotation o = (TimecodeAnnotation) other;
        if(o.movieId != movieId) return false;
        if(o.packageId != packageId) return false;
        if(o.timecodeAnnotations == null) {
            if(timecodeAnnotations != null) return false;
        } else if(!o.timecodeAnnotations.equals(timecodeAnnotations)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (int) (packageId ^ (packageId >>> 32));
        hashCode = hashCode * 31 + (timecodeAnnotations == null ? 1237 : timecodeAnnotations.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TimecodeAnnotation{");
        builder.append("movieId=").append(movieId);
        builder.append(",packageId=").append(packageId);
        builder.append(",timecodeAnnotations=").append(timecodeAnnotations);
        builder.append("}");
        return builder.toString();
    }

    public TimecodeAnnotation clone() {
        try {
            TimecodeAnnotation clone = (TimecodeAnnotation)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}