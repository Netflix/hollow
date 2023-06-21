package com.netflix.hollow.core.memory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class MemoryFileUtil {

    public static String filepath() {
        return System.getProperty("java.io.tmpdir");
    }

    // whichData is null for object types, and listPointerData, listElementData, mapEntryData etc. for collection types
    public static String fixedLengthDataFilename(String type, String whichData, int shardNo) {
        return "hollow-fixedLengthData-"
                + (whichData != null ? whichData + "_" : "")
                + type + "_"
                + shardNo + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) + "_"
                + new Random().nextInt();
    }

    public static String varLengthDataFilename(String type, String field, int shardNo) {
        return "hollow-varLengthData-"
                + field + "_"
                + type + "_"
                + shardNo + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) + "_"
                + new Random().nextInt();
    }
}
