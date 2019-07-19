package com.netflix.vms.transformer.util;

import com.netflix.config.FastProperty;

public class DeprecationUtil {
    private static final FastProperty.BooleanProperty DISABLE_ALIASES = new FastProperty.BooleanProperty("disable.videoGeneral.aliases",true);

    // to remove legacy aliases that are OBE and not used anymore in favor of MovieTitleAKA published through Oscar's SearchableTitles cinder feed directly to EDGE
    public static boolean disableVideoGeneralAliases(){
        return DISABLE_ALIASES.get();
    }
}
