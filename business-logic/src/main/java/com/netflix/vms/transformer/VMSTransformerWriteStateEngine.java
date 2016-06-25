package com.netflix.vms.transformer;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import com.netflix.vms.transformer.util.VMSTransformerHashCodeFinder;
import com.netflix.hollow.write.HollowWriteStateEngine;

public class VMSTransformerWriteStateEngine extends HollowWriteStateEngine {

    private final HollowObjectMapper objectMapper;
    
    public VMSTransformerWriteStateEngine() {
        super(new VMSTransformerHashCodeFinder());
        this.objectMapper = new HollowObjectMapper(this);
        initializeAllTypeStates();
    }

    public HollowObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    private void initializeAllTypeStates() {
        HollowObjectMapper mapper = new HollowObjectMapper(this);
        
        try {
            for(Class<?> clazz : getClasses("com.netflix.vms.transformer.hollowoutput")) {
                mapper.initializeTypeState(clazz);
            }
        } catch(Throwable th) {
            throw new RuntimeException(th);
        }
    }
    
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}