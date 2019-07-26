package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.common.slice.OutputDataSlicer;
import java.lang.reflect.Constructor;

public class SlicerFactory {

    public InputDataSlicer getInputDataSlicer(Class<? extends InputDataSlicer> slicerClasz, int... specificTopNodeIdsToInclude)
            throws Exception {
        Constructor slicerConstructor = slicerClasz.getConstructor(int[].class);
        return (InputDataSlicer) slicerConstructor.newInstance(specificTopNodeIdsToInclude);
    }

    public OutputDataSlicer getOutputDataSlicer(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        return new TransformerOutputDataSlicer(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }
}
