package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.util.slice.DataSlicer;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.CreateDevSliceJob;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;

public class CreateHollowDevSliceJob extends CreateDevSliceJob {

    private final HollowBlobDataProvider dataProvider;
    
    public CreateHollowDevSliceJob(PublishWorkflowContext ctx, AnnounceJob dependency, HollowBlobDataProvider dataProvider, long currentCycleId) {
        super(ctx, dependency, currentCycleId);
        this.dataProvider = dataProvider;
    }

    @Override
    protected boolean executeJob() {
        HollowReadStateEngine stateEngine = dataProvider.getStateEngine();
        
        DataSlicer slicer = new DataSlicer(0, specificTopNodeIdsToInclude)
    }
    
    
    
    
}
