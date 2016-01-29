package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;
import java.util.List;

public class RolloutCharacter {

    public int id;
    public Map<Strings, Strings> rawL10nAttribs;
    public List<Quote> quotes;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}