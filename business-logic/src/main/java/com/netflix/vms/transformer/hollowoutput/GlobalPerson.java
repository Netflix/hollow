package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class GlobalPerson implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public List<Integer> aliasesIds = null;
    public List<PersonRole> personRoles = null;
    public BirthDate birthDate = null;
    public List<Strings> spouses = null;
    public List<Strings> partners = null;
    public List<Video> topVideos = null;
    public Strings currentRelationship = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof GlobalPerson))
            return false;

        GlobalPerson o = (GlobalPerson) other;
        if(o.id != id) return false;
        if(o.aliasesIds == null) {
            if(aliasesIds != null) return false;
        } else if(!o.aliasesIds.equals(aliasesIds)) return false;
        if(o.personRoles == null) {
            if(personRoles != null) return false;
        } else if(!o.personRoles.equals(personRoles)) return false;
        if(o.birthDate == null) {
            if(birthDate != null) return false;
        } else if(!o.birthDate.equals(birthDate)) return false;
        if(o.spouses == null) {
            if(spouses != null) return false;
        } else if(!o.spouses.equals(spouses)) return false;
        if(o.partners == null) {
            if(partners != null) return false;
        } else if(!o.partners.equals(partners)) return false;
        if(o.topVideos == null) {
            if(topVideos != null) return false;
        } else if(!o.topVideos.equals(topVideos)) return false;
        if(currentRelationship == null) {
            if(currentRelationship == null) return false;
        } else if(!o.currentRelationship.equals(currentRelationship)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + (aliasesIds == null ? 1237 : aliasesIds.hashCode());
        hashCode = hashCode * 31 + (personRoles == null ? 1237 : personRoles.hashCode());
        hashCode = hashCode * 31 + (birthDate == null ? 1237 : birthDate.hashCode());
        hashCode = hashCode * 31 + (spouses == null ? 1237 : spouses.hashCode());
        hashCode = hashCode * 31 + (partners == null ? 1237 : partners.hashCode());
        hashCode = hashCode * 31 + (topVideos == null ? 1237 : topVideos.hashCode());
        hashCode = hashCode * 31 + (currentRelationship == null ? 1237 : currentRelationship.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("GlobalPerson{");
        builder.append("id=").append(id);
        builder.append(",aliasesIds=").append(aliasesIds);
        builder.append(",personRoles=").append(personRoles);
        builder.append(",birthDate=").append(birthDate);
        builder.append(",spouses=").append(spouses);
        builder.append(",partners=").append(partners);
        builder.append(",topVideos=").append(topVideos);
        builder.append(",currentRelationship=").append(currentRelationship);
        builder.append("}");
        return builder.toString();
    }

    public GlobalPerson clone() {
        try {
            GlobalPerson clone = (GlobalPerson)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}