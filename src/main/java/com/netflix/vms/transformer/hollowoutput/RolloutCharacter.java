package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;

public class RolloutCharacter implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public Map<Strings, Strings> rawL10nAttribs = null;
    public List<Quote> quotes = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutCharacter))
            return false;

        RolloutCharacter o = (RolloutCharacter) other;
        if(o.id != id) return false;
        if(o.rawL10nAttribs == null) {
            if(rawL10nAttribs != null) return false;
        } else if(!o.rawL10nAttribs.equals(rawL10nAttribs)) return false;
        if(o.quotes == null) {
            if(quotes != null) return false;
        } else if(!o.quotes.equals(quotes)) return false;
        return true;
    }

    public RolloutCharacter clone() {
        try {
            return (RolloutCharacter)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}