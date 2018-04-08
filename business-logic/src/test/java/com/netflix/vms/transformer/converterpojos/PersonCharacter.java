package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="PersonCharacter")
public class PersonCharacter implements Cloneable {

    public long personId = java.lang.Long.MIN_VALUE;
    public long characterId = java.lang.Long.MIN_VALUE;

    public PersonCharacter setPersonId(long personId) {
        this.personId = personId;
        return this;
    }
    public PersonCharacter setCharacterId(long characterId) {
        this.characterId = characterId;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonCharacter))
            return false;

        PersonCharacter o = (PersonCharacter) other;
        if(o.personId != personId) return false;
        if(o.characterId != characterId) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (personId ^ (personId >>> 32));
        hashCode = hashCode * 31 + (int) (characterId ^ (characterId >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PersonCharacter{");
        builder.append("personId=").append(personId);
        builder.append(",characterId=").append(characterId);
        builder.append("}");
        return builder.toString();
    }

    public PersonCharacter clone() {
        try {
            PersonCharacter clone = (PersonCharacter)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}