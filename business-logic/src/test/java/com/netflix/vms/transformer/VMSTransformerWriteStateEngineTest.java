package com.netflix.vms.transformer;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class VMSTransformerWriteStateEngineTest {
    private static final String POJO_DATAMODEL_PACKAGE = "com.netflix.vms.transformer.hollowoutput";

    private VMSTransformerWriteStateEngine stateEngine;

    
    @Before
    public void setup() {
        stateEngine = new VMSTransformerWriteStateEngine();
    }

    @Test
    public void ensureTypeStateForAllOutput() throws ClassNotFoundException {
        Set<String> missingSet = new HashSet<>();

        Reflections reflections = new Reflections(POJO_DATAMODEL_PACKAGE, new SubTypesScanner(false));
        for (String className : reflections.getAllTypes()) {
            Class<?> clazz = Class.forName(className);
            String typeName = clazz.getSimpleName();
            if (stateEngine.getTypeState(typeName) == null) {
                missingSet.add(typeName);
                System.out.println("\t *** [MISSING]: \t" + typeName);
            } else {
                System.out.println("\t Found: \t" + typeName);
            }
        }

        // @TODO: Need to make sure VMSTransformerWriteStateEngine includes them or have those classes deleted
        String msg = "Found missing types in VMSTransformerWriteStateEngine: " + missingSet;
        if (!missingSet.isEmpty()) System.out.println("\n *** \t" + msg);
        Assert.assertTrue(msg, missingSet.isEmpty());
    }
}