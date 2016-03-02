package com.netflix.vms.transformer.modules.passthrough.beehive;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.CharacterElementsHollow;
import com.netflix.vms.transformer.hollowinput.CharacterHollow;
import com.netflix.vms.transformer.hollowinput.CharacterQuoteHollow;
import com.netflix.vms.transformer.hollowinput.CharacterQuoteListHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.Quote;
import com.netflix.vms.transformer.hollowoutput.RolloutCharacter;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class RolloutCharacterModule extends AbstractTransformModule {

    public RolloutCharacterModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper) {
        super(api, mapper);
    }

    @Override
    public void transform() {
        Strings bottomLineKey = new Strings("Blade Bottom Line");
        Strings topLineKey = new Strings("Blade Top Line");
        Strings charBioKey = new Strings("Character Bio");
        Strings charNameKey = new Strings("Character Name");

        Collection<CharacterHollow> inputs = api.getAllCharacterHollow();
        for (CharacterHollow input : inputs) {
            RolloutCharacter out = new RolloutCharacter();
            out.id = (int) input._getCharacterId();

            out.rawL10nAttribs = new HashMap<Strings, Strings>();
            CharacterElementsHollow elems = input._getElements();

            StringHollow bottomLine = elems._getBladeBottomLine();
            if (bottomLine != null) {
                out.rawL10nAttribs.put(bottomLineKey, new Strings(bottomLine._getValue()));
            }
            StringHollow topLine = elems._getBladeTopLine();
            if (topLine != null) {
                out.rawL10nAttribs.put(topLineKey, new Strings(topLine._getValue()));
            }
            StringHollow chrBio = elems._getCharacterBio();
            if (chrBio != null) {
                out.rawL10nAttribs.put(charBioKey, new Strings(chrBio._getValue()));
            }

            StringHollow chrName = elems._getCharacterName();
            if (chrName != null) {
                out.rawL10nAttribs.put(charNameKey, new Strings(chrName._getValue()));
            }

            out.quotes = new ArrayList<Quote>();
            CharacterQuoteListHollow inList = input._getQuotes();
            Iterator<CharacterQuoteHollow> it = inList.iterator();
            while (it.hasNext()) {
                CharacterQuoteHollow item = it.next();
                Quote quote = new Quote();
                quote.sequenceNumber = (int) item._getSequenceNumber();
                quote.characterId = out.id;
                out.quotes.add(quote);
            }
            mapper.addObject(out);
        }
    }
}
