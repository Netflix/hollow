package com.netflix.vms.transformer.modules.passthrough.beehive;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.CharacterHollow;
import com.netflix.vms.transformer.hollowinput.CharacterQuoteHollow;
import com.netflix.vms.transformer.hollowinput.CharacterQuoteListHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.Quote;
import com.netflix.vms.transformer.hollowoutput.RolloutCharacter;
import com.netflix.vms.transformer.hollowoutput.Strings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class RolloutCharacterModule {
    private final VMSHollowVideoInputAPI api;
    private final HollowObjectMapper mapper;

    public RolloutCharacterModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper) {
        this.api = api;
        this.mapper = mapper;
    }

    public void transform() {
        Collection<CharacterHollow> inputs = api.getAllCharacterHollow();
        for (CharacterHollow input : inputs) {
            RolloutCharacter out = new RolloutCharacter();
            out.id = (int) input._getCharacterId();
            out.rawL10nAttribs = new HashMap<Strings, Strings>();
            out.quotes = new ArrayList<Quote>();

            CharacterQuoteListHollow inList = input._getQuotes();
            Iterator<CharacterQuoteHollow> it = inList.iterator();
            while (it.hasNext()) {
                CharacterQuoteHollow item = it.next();
                Quote quote = new Quote();
                quote.sequenceNumber = (int) item._getSequenceNumber();
                out.quotes.add(quote);
            }
            mapper.addObject(out);
        }
    }
}
