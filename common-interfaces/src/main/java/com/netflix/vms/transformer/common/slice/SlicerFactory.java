package com.netflix.vms.transformer.common.slice;

import java.lang.reflect.Constructor;
import java.util.Set;

public class SlicerFactory {

    // SNAP: make these non reflection based, could dataset class just return them?
    public InputDataSlicer getInputDataSlicer(Class<? extends InputDataSlicer> slicerClasz,
                                                int... specificTopNodeIdsToInclude)
                                                throws Exception {

        Constructor slicerConstructor = slicerClasz.getConstructor(int[].class);
        return (InputDataSlicer) slicerConstructor.newInstance(specificTopNodeIdsToInclude);
    }

    public OutputDataSlicer getOutputDataSlicer(Class<?extends OutputDataSlicer> slicerClasz,
                                                int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude)
                                                throws Exception {
        Constructor slicerConstructor = slicerClasz.getConstructor(int.class, int[].class);
        return (OutputDataSlicer) slicerConstructor.newInstance(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }

    public OutputDataSlicer getOutputDataSlicer(Class<?extends OutputDataSlicer> slicerClasz,
                                                Set<String> excludedTypes, boolean isIncludeNonVideoL10N,
                                                int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude)
                                                throws Exception {
        Constructor slicerConstructor = slicerClasz.getConstructor(Class.class,
                                                String[].class, boolean.class,
                                                int.class, int[].class);
        return (OutputDataSlicer) slicerConstructor.newInstance(slicerClasz, excludedTypes, isIncludeNonVideoL10N,
                                                numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }
}
