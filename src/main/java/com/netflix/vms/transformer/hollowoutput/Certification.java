package com.netflix.vms.transformer.hollowoutput;


public class Certification implements Cloneable {

    public MovieCertification movieCert = null;
    public CertificationSystem certSystem = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Certification))
            return false;

        Certification o = (Certification) other;
        if(o.movieCert == null) {
            if(movieCert != null) return false;
        } else if(!o.movieCert.equals(movieCert)) return false;
        if(o.certSystem == null) {
            if(certSystem != null) return false;
        } else if(!o.certSystem.equals(certSystem)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (movieCert == null ? 1237 : movieCert.hashCode());
        hashCode = hashCode * 31 + (certSystem == null ? 1237 : certSystem.hashCode());
        return hashCode;
    }

    public Certification clone() {
        try {
            Certification clone = (Certification)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}