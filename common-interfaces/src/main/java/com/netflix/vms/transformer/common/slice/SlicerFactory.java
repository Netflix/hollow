package com.netflix.vms.transformer.common.slice;

import java.lang.reflect.Constructor;

public class SlicerFactory {

    // SNAP: make these non reflection based, could dataset class just return them?
    public InputDataSlicer getInputDataSlicer(Class<? extends InputDataSlicer> slicerClasz, int... specificTopNodeIdsToInclude)
            throws Exception {

        Constructor slicerConstructor = slicerClasz.getConstructor(int[].class);
        return (InputDataSlicer) slicerConstructor.newInstance(specificTopNodeIdsToInclude);
    }

    public OutputDataSlicer getOutputDataSlicer(Class<?extends OutputDataSlicer> slicerClasz,
                                                int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude)
                                                throws Exception {

        // return new TransformerOutputDataSlicer(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude); // SNAP:
        Constructor slicerConstructor = slicerClasz.getConstructor(int.class, int[].class);
        return (OutputDataSlicer) slicerConstructor.newInstance(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }
}
