package com.netflix.vms.transformer.input;

import com.netflix.aws.db.ItemAttribute;
import com.netflix.aws.file.FileAccessItem;
import com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition;
import com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.UpstreamDatasetConfig;
import java.util.List;
import java.util.Optional;
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

    public static Long getInputVersion(FileAccessItem fileItem, String namespace) {

        String inputVersionStr = getAttribute(fileItem, UpstreamDatasetConfig.getInputVersionAttribute(namespace));
        if (inputVersionStr == null || inputVersionStr.isEmpty()) return null;

        try {
            return Long.parseLong(inputVersionStr);
        } catch(Throwable th) {
            LOGGER.error("Failed to parse version for input namespace {} from FileStore: ", namespace, th);
            throw th;
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