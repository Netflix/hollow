package com.netflix.vms.hollowoutput.pojos;


public class ProfileTypeDescriptor {

    public int id;
    public Strings name;
    public Strings description;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ProfileTypeDescriptor))
            return false;

        ProfileTypeDescriptor o = (ProfileTypeDescriptor) other;
        if(o.id != id) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}