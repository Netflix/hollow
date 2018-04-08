package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="CharacterQuote")
public class CharacterQuote implements Cloneable {

    public long sequenceNumber = java.lang.Long.MIN_VALUE;

    public CharacterQuote() { }

    public CharacterQuote(long value) {
        this.sequenceNumber = value;
    }

    public CharacterQuote setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CharacterQuote))
            return false;

        CharacterQuote o = (CharacterQuote) other;
        if(o.sequenceNumber != sequenceNumber) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (sequenceNumber ^ (sequenceNumber >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CharacterQuote{");
        builder.append("sequenceNumber=").append(sequenceNumber);
        builder.append("}");
        return builder.toString();
    }

    public CharacterQuote clone() {
        try {
            CharacterQuote clone = (CharacterQuote)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}