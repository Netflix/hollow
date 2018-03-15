package com.netflix.vms.transformer.publish.workflow.util;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.BlobState;

import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.logging.TaggingLogger.LogTag;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileStatLogger {

    public static void logFileState(TaggingLogger logger, LogTag logTag, String prefix, File... files) {
        List<LogTag> tagList = Arrays.asList(BlobState, logTag);
        for (File file : files) {
            logger.info(tagList, "[{}] filename={}, file={}, exists={}, size={}, modified={}", prefix, file.getName(), file, file.exists(), file.length(), file.lastModified());
        }
    }

}
