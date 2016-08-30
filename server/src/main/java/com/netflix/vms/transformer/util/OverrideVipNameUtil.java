package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.common.config.TransformerConfig;

public class OverrideVipNameUtil {
    private static final String OVERRIDE_VIP_SUFFIX = "_override";

    private static boolean isOverrideVip(String vip) {
        return vip != null && vip.endsWith(OVERRIDE_VIP_SUFFIX);
    }

    public static boolean isOverrideVip(TransformerConfig cfg) {
        return isOverrideVip(cfg.getTransformerVip());
    }

    /**
     * Return the Transformer/Output Data Vip - used to figure out where the pinned data comes from
     */
    public static String getPinTitleDataTransformerVip(TransformerConfig cfg) {
        String dataVip = cfg.getOverridePinTitleOutputDataVip();
        if (dataVip != null) return dataVip;

        // By Default use the corresponding normal cluster vip (e.g. berlin_override, then return berlin)
        String vip = cfg.getTransformerVip();
        if (isOverrideVip(vip)) {
            int len = vip.length() - OVERRIDE_VIP_SUFFIX.length();
            return vip.substring(0, len);
        }
        return vip;
    }
}