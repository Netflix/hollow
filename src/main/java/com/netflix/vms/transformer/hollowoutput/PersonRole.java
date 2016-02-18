package com.netflix.vms.transformer.hollowoutput;


public class PersonRole {

    public VPerson person;
    public VRole roleType;
    public Video video;
    public int weight;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}