package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Certifications")
public class Certifications implements Cloneable {

    public long certificationTypeId = java.lang.Long.MIN_VALUE;
    public TranslatedText name = null;
    public TranslatedText description = null;

    public Certifications setCertificationTypeId(long certificationTypeId) {
        this.certificationTypeId = certificationTypeId;
        return this;
    }
    public Certifications setName(TranslatedText name) {
        this.name = name;
        return this;
    }
    public Certifications setDescription(TranslatedText description) {
        this.description = description;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Certifications))
            return false;

        Certifications o = (Certifications) other;
        if(o.certificationTypeId != certificationTypeId) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (certificationTypeId ^ (certificationTypeId >>> 32));
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (description == null ? 1237 : description.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Certifications{");
        builder.append("certificationTypeId=").append(certificationTypeId);
        builder.append(",name=").append(name);
        builder.append(",description=").append(description);
        builder.append("}");
        return builder.toString();
    }

    public Certifications clone() {
        try {
            Certifications clone = (Certifications)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}