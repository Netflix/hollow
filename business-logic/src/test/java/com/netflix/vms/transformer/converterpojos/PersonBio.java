package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="PersonBio")
public class PersonBio implements Cloneable {

    public List<String> spouses = null;
    public List<String> partners = null;
    public List<String> relationships = null;
    public String currentRelationship = null;
    public long personId = java.lang.Long.MIN_VALUE;
    public ExplicitDate birthDate = null;
    public ExplicitDate deathDate = null;
    @HollowTypeName(name="ListOfVideoIds")
    public List<VideoId> movieIds = null;

    public PersonBio setSpouses(List<String> spouses) {
        this.spouses = spouses;
        return this;
    }
    public PersonBio setPartners(List<String> partners) {
        this.partners = partners;
        return this;
    }
    public PersonBio setRelationships(List<String> relationships) {
        this.relationships = relationships;
        return this;
    }
    public PersonBio setCurrentRelationship(String currentRelationship) {
        this.currentRelationship = currentRelationship;
        return this;
    }
    public PersonBio setPersonId(long personId) {
        this.personId = personId;
        return this;
    }
    public PersonBio setBirthDate(ExplicitDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }
    public PersonBio setDeathDate(ExplicitDate deathDate) {
        this.deathDate = deathDate;
        return this;
    }
    public PersonBio setMovieIds(List<VideoId> movieIds) {
        this.movieIds = movieIds;
        return this;
    }
    public PersonBio addToSpouses(String string) {
        if (this.spouses == null) {
            this.spouses = new ArrayList<String>();
        }
        this.spouses.add(string);
        return this;
    }
    public PersonBio addToPartners(String string) {
        if (this.partners == null) {
            this.partners = new ArrayList<String>();
        }
        this.partners.add(string);
        return this;
    }
    public PersonBio addToRelationships(String string) {
        if (this.relationships == null) {
            this.relationships = new ArrayList<String>();
        }
        this.relationships.add(string);
        return this;
    }
    public PersonBio addToMovieIds(VideoId videoId) {
        if (this.movieIds == null) {
            this.movieIds = new ArrayList<VideoId>();
        }
        this.movieIds.add(videoId);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonBio))
            return false;

        PersonBio o = (PersonBio) other;
        if(o.spouses == null) {
            if(spouses != null) return false;
        } else if(!o.spouses.equals(spouses)) return false;
        if(o.partners == null) {
            if(partners != null) return false;
        } else if(!o.partners.equals(partners)) return false;
        if(o.relationships == null) {
            if(relationships != null) return false;
        } else if(!o.relationships.equals(relationships)) return false;
        if(o.currentRelationship == null) {
            if(currentRelationship != null) return false;
        } else if(!o.currentRelationship.equals(currentRelationship)) return false;
        if(o.personId != personId) return false;
        if(o.birthDate == null) {
            if(birthDate != null) return false;
        } else if(!o.birthDate.equals(birthDate)) return false;
        if(o.deathDate == null) {
            if(deathDate != null) return false;
        } else if(!o.deathDate.equals(deathDate)) return false;
        if(o.movieIds == null) {
            if(movieIds != null) return false;
        } else if(!o.movieIds.equals(movieIds)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (spouses == null ? 1237 : spouses.hashCode());
        hashCode = hashCode * 31 + (partners == null ? 1237 : partners.hashCode());
        hashCode = hashCode * 31 + (relationships == null ? 1237 : relationships.hashCode());
        hashCode = hashCode * 31 + (currentRelationship == null ? 1237 : currentRelationship.hashCode());
        hashCode = hashCode * 31 + (int) (personId ^ (personId >>> 32));
        hashCode = hashCode * 31 + (birthDate == null ? 1237 : birthDate.hashCode());
        hashCode = hashCode * 31 + (deathDate == null ? 1237 : deathDate.hashCode());
        hashCode = hashCode * 31 + (movieIds == null ? 1237 : movieIds.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PersonBio{");
        builder.append("spouses=").append(spouses);
        builder.append(",partners=").append(partners);
        builder.append(",relationships=").append(relationships);
        builder.append(",currentRelationship=").append(currentRelationship);
        builder.append(",personId=").append(personId);
        builder.append(",birthDate=").append(birthDate);
        builder.append(",deathDate=").append(deathDate);
        builder.append(",movieIds=").append(movieIds);
        builder.append("}");
        return builder.toString();
    }

    public PersonBio clone() {
        try {
            PersonBio clone = (PersonBio)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}