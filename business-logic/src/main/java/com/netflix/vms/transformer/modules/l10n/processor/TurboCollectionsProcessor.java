package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.TranslatedTextHollow;
import com.netflix.vms.transformer.hollowinput.TurboCollectionsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TurboCollectionsProcessor extends AbstractL10NMiscProcessor<TurboCollectionsHollow> {

    public TurboCollectionsProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<TurboCollectionsHollow> getInputs() {
        return api.getAllTurboCollectionsHollow();
    }

    @Override
    public void processInput(TurboCollectionsHollow input) {
        final int inputId = (int) input._getId();
        final String prefix = input._getPrefix()._getValue();

        Map<String, TranslatedTextHollow> map = new HashMap<>();
        map.put("bmt.n", input._getBmt_n());
        map.put("char.n", input._getChar_n());
        map.put("dn", input._getDn());
        map.put("kc.cn", input._getKc_cn());
        map.put("kag.kn", input._getKag_kn());
        map.put("nav.sn", input._getNav_sn());
        map.put("roar.n", input._getRoar_n());
        map.put("sn", input._getSn());
        map.put("st.0", input._getSt_0());
        map.put("st.1", input._getSt_1());
        map.put("st.2", input._getSt_2());
        map.put("st.3", input._getSt_3());
        map.put("st.4", input._getSt_4());
        map.put("st.5", input._getSt_5());
        map.put("st.6", input._getSt_6());
        map.put("st.7", input._getSt_7());
        map.put("st.8", input._getSt_8());
        map.put("st.9", input._getSt_9());
        map.put("tdn", input._getTdn());

        for (Map.Entry<String, TranslatedTextHollow> entry : map.entrySet()) {
            String key = entry.getKey();
            TranslatedTextHollow value = entry.getValue();
            if (value == null) continue;

            String resourceId = L10nResourceIdLookup.getGenericResourceId(inputId, prefix, key);
            addL10NResources(resourceId, value);
        }
    }
}