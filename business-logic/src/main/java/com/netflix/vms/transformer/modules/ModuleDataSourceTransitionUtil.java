package com.netflix.vms.transformer.modules;

import com.netflix.config.FastProperty;

public class ModuleDataSourceTransitionUtil {
    private static final FastProperty.BooleanProperty USE_OSCAR_INCREMENTAL_VIDEO_GENERAL = new FastProperty.BooleanProperty("use.oscar.incremental.videoGeneral",false);
    public static boolean useOscarFeedVideoGeneral(){
        return USE_OSCAR_INCREMENTAL_VIDEO_GENERAL.get();
    }

    private static final FastProperty.BooleanProperty USE_TOPN_JSON = new FastProperty.BooleanProperty("use.topn.json",false);
    public static boolean useTopNJson(){
        return USE_TOPN_JSON.get();
    }
}
