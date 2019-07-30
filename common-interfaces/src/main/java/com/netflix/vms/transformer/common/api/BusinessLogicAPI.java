package com.netflix.vms.transformer.common.api;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.input.CycleInputs;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.common.slice.OutputDataSlicer;
import java.util.List;
import java.util.Set;

public interface BusinessLogicAPI {

    void transform(CycleInputs cycleInputs, HollowWriteStateEngine outputStateEngine, TransformerContext ctx)
            throws Throwable;

    // @@@ List may be modified
    List<HollowSchema> getSchema();

    // @@@ This can be removed if we can get rid of HollowObjectHashCodeFinder use
    HollowObjectHashCodeFinder getHashCodeFinder();

    long getTargetMaxTypeShardSize();

    Class<? extends HollowAPI> getAPI(String namespace);

    Class<? extends OutputDataSlicer> getOutputSlicer();

    Class<? extends InputDataSlicer> getInputSlicer(String namespace);

    String[] getStreamHollowTypes();

    Set<String> getInputNamespaces();
    Set<String> getInputNamespacesForEnv(boolean isProd);
}
