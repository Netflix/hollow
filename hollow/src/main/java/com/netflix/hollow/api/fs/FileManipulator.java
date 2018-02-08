/*
 *
 *  Copyright 2017 Netflix, Inc.
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
 *
 */
package com.netflix.hollow.api.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManipulator {

    public static Path createFile(Path dir, String fileName) {
        return createFile(dir.toString(), fileName);
    }

    public static Path createFile(String dir, String fileName) {
        Path path = Paths.get(dir, fileName);
        try {
            if(!Files.exists(path)){
                Files.createFile(path);
            }
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Could not create file: " + path.toString(), e);
        }
    }

    public static Path createDir(Path dir) {
        try {
            if(!Files.exists(dir)){
                Files.createDirectories(dir);
            }
            return dir;
        } catch (IOException e) {
            throw new RuntimeException("Could not create dir: " + dir.toString(), e);
        }
    }
}
