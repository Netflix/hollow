package com.netflix.hollow.api.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManipulator {

    public static Path createFile(Path dir, String fileName) {
        Path path = Paths.get(dir.toString(), fileName);
        try {
            if(!Files.exists(path))
                Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not create file: " + path.toString(), e);
        }
        return path;
    }
}
