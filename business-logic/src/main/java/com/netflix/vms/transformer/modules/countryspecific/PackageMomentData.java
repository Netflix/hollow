package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.TrickPlayItem;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import java.util.HashMap;
import java.util.Map;

public class PackageMomentData {

    public Map<TrickPlayType, TrickPlayItem> trickPlayItemMap = new HashMap<TrickPlayType, TrickPlayItem>();
    
    public long startMomentOffsetInSeconds;
    public long endMomentOffsetInSeconds;

}
