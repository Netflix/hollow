package com.netflix.vms.transformer.hollowoutput;


public class Certification {

    public MovieCertification movieCert;
    public CertificationSystem certSystem;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}