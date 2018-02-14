package com.netflix.vms.transformer.publish.workflow.util;

import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.logging.TaggingLogger.LogTag;
import java.io.File;

public class FileStatLogger {

    public static void logFileState(TaggingLogger logger, LogTag logTag, String prefix, File... files) {
        for (File file : files) {
            logger.info(logTag, "[{}] filename={}, file={}, exists={}, size={}, modified={}", prefix, file.getName(), file, file.exists(), file.length(), file.lastModified());
        }
    }

}
