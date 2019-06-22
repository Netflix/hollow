package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.TimecodeAnnotation;
import com.netflix.vms.transformer.hollowoutput.TrickPlayItem;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageMomentData {

    public Map<TrickPlayType, TrickPlayItem> trickPlayItemMap = new HashMap<TrickPlayType, TrickPlayItem>();

    public long startMomentOffsetInMillis = -1;
    public long endMomentOffsetInMillis = -1;
    
    List<TimecodeAnnotation> timecodes = new ArrayList<>();
}
