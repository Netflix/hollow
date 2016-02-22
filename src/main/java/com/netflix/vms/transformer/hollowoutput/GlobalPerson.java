package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class GlobalPerson implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public List<Integer> aliasesIds = null;
    public List<PersonRole> personRoles = null;

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
        return true;
    }

    public GlobalPerson clone() {
        try {
            return (GlobalPerson)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}