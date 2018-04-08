package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="PersonVideo")
public class PersonVideo implements Cloneable {

    @HollowTypeName(name="PersonVideoAliasIdsList")
    public List<PersonVideoAliasId> aliasIds = null;
    @HollowTypeName(name="PersonVideoRolesList")
    public List<PersonVideoRole> roles = null;
    public long personId = java.lang.Long.MIN_VALUE;

    public PersonVideo setAliasIds(List<PersonVideoAliasId> aliasIds) {
        this.aliasIds = aliasIds;
        return this;
    }
    public PersonVideo setRoles(List<PersonVideoRole> roles) {
        this.roles = roles;
        return this;
    }
    public PersonVideo setPersonId(long personId) {
        this.personId = personId;
        return this;
    }
    public PersonVideo addToAliasIds(PersonVideoAliasId personVideoAliasId) {
        if (this.aliasIds == null) {
            this.aliasIds = new ArrayList<PersonVideoAliasId>();
        }
        this.aliasIds.add(personVideoAliasId);
        return this;
    }
    public PersonVideo addToRoles(PersonVideoRole personVideoRole) {
        if (this.roles == null) {
            this.roles = new ArrayList<PersonVideoRole>();
        }
        this.roles.add(personVideoRole);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonVideo))
            return false;

        PersonVideo o = (PersonVideo) other;
        if(o.aliasIds == null) {
            if(aliasIds != null) return false;
        } else if(!o.aliasIds.equals(aliasIds)) return false;
        if(o.roles == null) {
            if(roles != null) return false;
        } else if(!o.roles.equals(roles)) return false;
        if(o.personId != personId) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (aliasIds == null ? 1237 : aliasIds.hashCode());
        hashCode = hashCode * 31 + (roles == null ? 1237 : roles.hashCode());
        hashCode = hashCode * 31 + (int) (personId ^ (personId >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PersonVideo{");
        builder.append("aliasIds=").append(aliasIds);
        builder.append(",roles=").append(roles);
        builder.append(",personId=").append(personId);
        builder.append("}");
        return builder.toString();
    }

    public PersonVideo clone() {
        try {
            PersonVideo clone = (PersonVideo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}