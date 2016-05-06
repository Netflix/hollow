package com.netflix.vms.transformer.input;

import com.netflix.aws.db.ItemAttribute;
import com.netflix.aws.file.FileAccessItem;
import com.netflix.logging.ILog;
import com.netflix.logging.LogManager;
import java.util.List;

@SuppressWarnings("deprecation")
public class FileStoreUtil {

    private static final ILog LOGGER = LogManager.getLogger(FileStoreUtil.class);

    public static long getToVersion(FileAccessItem fileItem) {
        String toVersionStr = getAttribute(fileItem, "toVersion");

        try {
            return Long.parseLong(toVersionStr);
        } catch(Throwable th) {
            LOGGER.error(th);
            return Long.MIN_VALUE;
        }
    }

    public static long getFromVersion(FileAccessItem fileItem) {
        String fromVersionStr = getAttribute(fileItem, "fromVersion");

        if(fromVersionStr == null)
            return Long.MIN_VALUE;

        return Long.parseLong(fromVersionStr);
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
