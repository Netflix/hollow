package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Persons")
public class Persons implements Cloneable {

    public long personId = java.lang.Long.MIN_VALUE;
    public TranslatedText name = null;
    public TranslatedText bio = null;

    public Persons setPersonId(long personId) {
        this.personId = personId;
        return this;
    }
    public Persons setName(TranslatedText name) {
        this.name = name;
        return this;
    }
    public Persons setBio(TranslatedText bio) {
        this.bio = bio;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Persons))
            return false;

        Persons o = (Persons) other;
        if(o.personId != personId) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.bio == null) {
            if(bio != null) return false;
        } else if(!o.bio.equals(bio)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (personId ^ (personId >>> 32));
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (bio == null ? 1237 : bio.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Persons{");
        builder.append("personId=").append(personId);
        builder.append(",name=").append(name);
        builder.append(",bio=").append(bio);
        builder.append("}");
        return builder.toString();
    }

    public Persons clone() {
        try {
            Persons clone = (Persons)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}