package com.netflix.vms.transformer.common.slice;

import com.netflix.hollow.write.HollowWriteStateEngine;

public interface DataSlicer {
    
    public SliceTask getSliceTask(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude);
    
    public interface SliceTask {
        
        public HollowWriteStateEngine sliceOutputBlob(HollowWriteStateEngine outputStateEngine);
        
        public HollowWriteStateEngine sliceInputBlob(HollowWriteStateEngine inputStateEngine);
        
    }

}
