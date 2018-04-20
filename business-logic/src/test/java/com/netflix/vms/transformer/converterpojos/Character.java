package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="Character")
public class Character implements Cloneable {

    public long characterId = java.lang.Long.MIN_VALUE;
    public CharacterElements elements = null;
    @HollowTypeName(name="CharacterQuoteList")
    public List<CharacterQuote> quotes = null;
    public long lastUpdated = java.lang.Long.MIN_VALUE;

    public Character setCharacterId(long characterId) {
        this.characterId = characterId;
        return this;
    }
    public Character setElements(CharacterElements elements) {
        this.elements = elements;
        return this;
    }
    public Character setQuotes(List<CharacterQuote> quotes) {
        this.quotes = quotes;
        return this;
    }
    public Character setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
    public Character addToQuotes(CharacterQuote characterQuote) {
        if (this.quotes == null) {
            this.quotes = new ArrayList<CharacterQuote>();
        }
        this.quotes.add(characterQuote);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Character))
            return false;

        Character o = (Character) other;
        if(o.characterId != characterId) return false;
        if(o.elements == null) {
            if(elements != null) return false;
        } else if(!o.elements.equals(elements)) return false;
        if(o.quotes == null) {
            if(quotes != null) return false;
        } else if(!o.quotes.equals(quotes)) return false;
        if(o.lastUpdated != lastUpdated) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (characterId ^ (characterId >>> 32));
        hashCode = hashCode * 31 + (elements == null ? 1237 : elements.hashCode());
        hashCode = hashCode * 31 + (quotes == null ? 1237 : quotes.hashCode());
        hashCode = hashCode * 31 + (int) (lastUpdated ^ (lastUpdated >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Character{");
        builder.append("characterId=").append(characterId);
        builder.append(",elements=").append(elements);
        builder.append(",quotes=").append(quotes);
        builder.append(",lastUpdated=").append(lastUpdated);
        builder.append("}");
        return builder.toString();
    }

    public Character clone() {
        try {
            Character clone = (Character)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}