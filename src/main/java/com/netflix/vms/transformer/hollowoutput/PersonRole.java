package com.netflix.vms.transformer.hollowoutput;


public class PersonRole implements Cloneable {

    public VPerson person = null;
    public VRole roleType = null;
    public Video video = null;
    public int weight = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonRole))
            return false;

        PersonRole o = (PersonRole) other;
        if(o.person == null) {
            if(person != null) return false;
        } else if(!o.person.equals(person)) return false;
        if(o.roleType == null) {
            if(roleType != null) return false;
        } else if(!o.roleType.equals(roleType)) return false;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        if(o.weight != weight) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (person == null ? 1237 : person.hashCode());
        hashCode = hashCode * 31 + (roleType == null ? 1237 : roleType.hashCode());
        hashCode = hashCode * 31 + (video == null ? 1237 : video.hashCode());
        hashCode = hashCode * 31 + weight;
        return hashCode;
    }

    public PersonRole clone() {
        try {
            PersonRole clone = (PersonRole)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}