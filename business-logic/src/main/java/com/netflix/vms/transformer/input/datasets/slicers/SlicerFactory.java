package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.common.slice.OutputDataSlicer;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import java.lang.reflect.Constructor;

public class SlicerFactory {

    public InputDataSlicer getInputDataSlicer(UpstreamDatasetHolder.Dataset dataset,
            int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) throws Exception {

        Class slicerClasz =  dataset.getSlicer();
        Constructor slicerConstructor = slicerClasz.getConstructor(new Class[]{int[].class});
        return (InputDataSlicer) slicerConstructor.newInstance(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }

    public OutputDataSlicer getOutputDataSlicer(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        return new TransformerOutputDataSlicer(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }
}
