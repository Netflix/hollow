package com.netflix.vms.transformer.input;

import com.netflix.aws.db.ItemAttribute;
import com.netflix.aws.file.FileAccessItem;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class FileStoreUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStoreUtil.class);

    public static long getToVersion(FileAccessItem fileItem) {
        String toVersionStr = getAttribute(fileItem, "toVersion");
        if (toVersionStr == null) return Long.MIN_VALUE;

        try {
            return Long.parseLong(toVersionStr);
        } catch(Throwable th) {
            LOGGER.error("Exception: ", th);
            return Long.MIN_VALUE;
        }
    }

    public static long getFromVersion(FileAccessItem fileItem) {
        String fromVersionStr = getAttribute(fileItem, "fromVersion");
        if (fromVersionStr == null) return Long.MIN_VALUE;

        try {
            return Long.parseLong(fromVersionStr);
        } catch (Throwable th) {
            LOGGER.error("Exception: ", th);
            return Long.MIN_VALUE;
        }
    }

    public static String getConverterVip(FileAccessItem fileItem) {
        return getAttribute(fileItem, "converterVip");
    }

    public static long getInputDataVersion(FileAccessItem fileItem) {
        String inputVersionStr = getAttribute(fileItem, "inputVersion");
        if (inputVersionStr == null) return Long.MIN_VALUE;

        try {
            return Long.parseLong(inputVersionStr);
        } catch(Throwable th) {
            LOGGER.error("Exception: ", th);
            return Long.MIN_VALUE;
        }
    }

    public static long getGk2InputDataVersion(FileAccessItem fileItem) {
        String inputVersionStr = getAttribute(fileItem, "gk2InputVersion");
        if (inputVersionStr == null) return Long.MIN_VALUE;

        try {
            return Long.parseLong(inputVersionStr);
        } catch(Throwable th) {
            LOGGER.error("Exception: ", th);
            return Long.MIN_VALUE;
        }
    }

    public static long getPublishCycleDataTS(FileAccessItem fileItem) {
        String publishCycleDataTS = getAttribute(fileItem, "publishCycleDataTS");
        if (publishCycleDataTS == null) return Long.MIN_VALUE;

        try {
            return Long.parseLong(publishCycleDataTS);
        } catch(Throwable th) {
            LOGGER.error("Exception: ", th);
            return Long.MIN_VALUE;
        }
    }

    public static String getAttribute(FileAccessItem fileItem, String attributeName) {
        List<ItemAttribute> attributes = fileItem.getAttributes();
        for(ItemAttribute att : attributes) {
            if(attributeName.equals(att.getName()))
                return att.getValue();
        }
        return null;
    }
}