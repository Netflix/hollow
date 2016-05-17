package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.TranslatedTextHollow;
import com.netflix.vms.transformer.hollowinput.TurboCollectionsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.HashMap;
import java.util.Map;

public class TurboCollectionsProcessor extends AbstractL10NProcessor {

    public TurboCollectionsProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public int processResources() {
        for (TurboCollectionsHollow item : api.getAllTurboCollectionsHollow()) {
            final int itemId = (int) item._getId();
            final String prefix = item._getPrefix()._getValue();

            Map<String, TranslatedTextHollow> map = new HashMap<>();
            map.put("bmt.n", item._getBmt_n());
            map.put("char.n", item._getChar_n());
            map.put("dn", item._getDn());
            map.put("kc.cn", item._getKc_cn());
            map.put("kag.kn", item._getKag_kn());
            map.put("nav.sn", item._getNav_sn());
            map.put("roar.n", item._getRoar_n());
            map.put("sn", item._getSn());
            map.put("st.0", item._getSt_0());
            map.put("st.1", item._getSt_1());
            map.put("st.2", item._getSt_2());
            map.put("st.3", item._getSt_3());
            map.put("st.4", item._getSt_4());
            map.put("st.5", item._getSt_5());
            map.put("st.6", item._getSt_6());
            map.put("st.7", item._getSt_7());
            map.put("st.8", item._getSt_8());
            map.put("st.9", item._getSt_9());
            map.put("tdn", item._getTdn());

            for (Map.Entry<String, TranslatedTextHollow> entry : map.entrySet()) {
                String key = entry.getKey();
                TranslatedTextHollow value = entry.getValue();
                if (value == null) continue;

                String resourceId = L10nResourceIdLookup.getGenericResourceId(itemId, prefix, key);
                addL10NResources(resourceId, value._getTranslatedTexts());
            }
        }

        return api.getAllTurboCollectionsHollow().size();
    }
}