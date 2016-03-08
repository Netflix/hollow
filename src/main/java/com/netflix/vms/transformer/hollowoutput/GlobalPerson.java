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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + (aliasesIds == null ? 1237 : aliasesIds.hashCode());
        hashCode = hashCode * 31 + (personRoles == null ? 1237 : personRoles.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("GlobalPerson{");
        builder.append("id=").append(id);
        builder.append(",aliasesIds=").append(aliasesIds);
        builder.append(",personRoles=").append(personRoles);
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