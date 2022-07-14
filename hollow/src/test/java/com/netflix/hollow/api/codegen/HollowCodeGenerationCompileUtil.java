/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.hollow.api.codegen;

import edu.umd.cs.findbugs.FindBugs2;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class HollowCodeGenerationCompileUtil {
    private static final String FILENAME_FINDBUGS = "findbugs.xml";
    private static final String PROPERTY_CLASSPATH = "java.class.path";

    /**
     * Compiles java source files in the provided source directory, to the provided class directory.
     * This also runs findbugs on the compiled classes, throwing an exception if findbugs fails.
     */
    public static void compileSrcFiles(String sourceDirPath, String classDirPath) throws Exception {
        List<String> srcFiles = new ArrayList<>();
        addAllJavaFiles(new File(sourceDirPath), srcFiles);

        File classDir = new File(classDirPath);
        classDir.mkdir();

        List<String> argList = new ArrayList<>();
        argList.add("-d");
        argList.add(classDir.getAbsolutePath());
        argList.add("-classpath");
        argList.add(System.getProperty(PROPERTY_CLASSPATH) + System.getProperty("path.separator") + classDirPath);
        argList.addAll(srcFiles);

        // argList.toArray() for large size had trouble
        String[] args = new String[argList.size()];
        for(int i = 0; i < argList.size(); i++) {
            args[i] = argList.get(i);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int err = compiler.run(null, System.out, System.out, args);
        if(err != 0)
            throw new RuntimeException("compiler errors, see system.out");
        runFindbugs(classDir);
    }

    /**
     * Run findbugs on the provided directory. If findbugs fails, the first found bug is printed out as xml.
     */
    private static void runFindbugs(File classDir) throws Exception {
        ClassLoader classLoader = HollowCodeGenerationCompileUtil.class.getClassLoader();
        FindBugs2.main(new String[]{"-auxclasspath", System.getProperty(PROPERTY_CLASSPATH), "-output",
                classLoader.getResource("").getFile() + FILENAME_FINDBUGS, classDir.getAbsolutePath()});
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(FILENAME_FINDBUGS)));
        String line = "";
        String foundBug = null;
        while((line = reader.readLine()) != null) { // poor man's xml parser
            if(line.contains("<BugInstance")) {
                foundBug = line;
            } else if(foundBug != null) {
                foundBug += "\n" + line;
            }
            if(line.contains("</BugInstance>")) {
                throw new Exception("Findbugs found an error:\n" + foundBug);
            }
        }
    }

    private static void addAllJavaFiles(File folder, List<String> result) throws IOException {
        for(File f : folder.listFiles()) {
            if(f.isDirectory()) {
                addAllJavaFiles(f, result);
            } else if(f.getName().endsWith(".java")) {
                result.add(f.toString());

                System.out.println("Java file: " + f.getName());
                System.out.println("------------------------------\n");
                Path path = f.toPath();
                Files.copy(path, System.out);
                System.out.println("\n------------------------------\n");
            }
        }
    }

    /**
     * Cleanup specified folder based on file older than specified timestamp
     *
     * @param folder - folder to be cleaned up
     * @param timestamp - specify timestamp to cleanup older files
     */
    public static void cleanupFolder(File folder, Long timestamp) {
        System.out.println("Cleaning up folder: " + folder.getAbsolutePath());
        if(folder.exists()) {
            for(File file : folder.listFiles()) {
                if(file.isDirectory()) {
                    cleanupFolder(file, timestamp);
                    file.delete();
                } else if(timestamp == null || (timestamp.longValue() - file.lastModified() >= 5000)) { // cleanup file if it is older than specified timestamp with some buffer time
                    System.out.println(String.format("\t deleting file: %s, lastModified=%s", file.getName(), new Date(file.lastModified())));
                    file.delete();
                }
            }
            folder.delete();
        }
    }
}
