package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.common.config.TransformerConfig;

public class VipUtil {
    private static final String OVERRIDE_VIP_SUFFIX = "_override";

    private static boolean isOverrideVip(String vip) {
        return vip != null && vip.endsWith(OVERRIDE_VIP_SUFFIX);
    }

    public static boolean isOverrideVip(TransformerConfig cfg) {
        return isOverrideVip(cfg.getTransformerVip());
    }

    public static String getTitleOverrideTransformerVip(TransformerConfig cfg) {
        String overrideTitleDataVip = cfg.getOverrideTitleDataVip();
        if (overrideTitleDataVip != null) return overrideTitleDataVip;

        String vip = cfg.getTransformerVip();
        if (isOverrideVip(vip)) {
            int len = vip.length() - OVERRIDE_VIP_SUFFIX.length();
            return vip.substring(0, len);
        }
        return vip;
    }
}