package com.netflix.vms.transformer.logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.elasticsearch.ElasticSearchClient;
import java.util.Collection;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformerServerLogger implements TransformerLogger {

    private final ElasticSearchClient esClient;
    private final TransformerConfig config;
    private final Logger consoleLogger;
    private final long currentCycleId;

    private final String elasticSearchIndexName;
    private final ObjectMapper objectMapper;

    public TransformerServerLogger(TransformerConfig config, ElasticSearchClient esClient) {
        this(
                config,
                esClient,
                LoggerFactory.getLogger("Transformer"),
                -1
        );
    }

    private TransformerServerLogger(TransformerConfig config, ElasticSearchClient esClient, Logger consoleLogger, long cycleId) {
        this.consoleLogger = consoleLogger;
        this.esClient = esClient;
        this.config = config;
        this.currentCycleId = cycleId;
        this.elasticSearchIndexName = buildIndexName();
        this.objectMapper = new ObjectMapper();
    }

    public TransformerServerLogger withCurrentCycleId(long cycleId) {
        return new TransformerServerLogger(config, esClient, consoleLogger, cycleId);
    }

    @Override
    public void log(Severity severity, Collection<LogTag> tags, String message, Throwable th) {
        for(LogTag tag : tags) {
            String taggedMessage = tag.toString() + ": " + message;
            switch(severity) {
            case ERROR:
                if(th == null)
                    consoleLogger.error(taggedMessage, th);
                else
                    consoleLogger.error(taggedMessage);
                break;
            case WARN:
                if(th == null)
                    consoleLogger.warn(taggedMessage, th);
                else
                    consoleLogger.warn(taggedMessage);
                break;
            case INFO:
                if(th == null)
                    consoleLogger.info(taggedMessage);
                else
                    consoleLogger.info(taggedMessage, th);
                break;
            }

            if(config.isElasticSearchLoggingEnabled()) {
                try {
                    esClient.addData(elasticSearchIndexName, "vmsserver", toJsonString(severity, tag, message, th));
                } catch (JsonProcessingException e) {
                    consoleLogger.error("Unable to create json for ES log message", e);
                }
            }
        }
    }

    private String toJsonString(Severity severity, LogTag logTag, String message, Throwable th) throws JsonProcessingException {
        if(th != null)
            message += "; Exception: " + ExceptionUtils.getStackTrace(th);

        TransformerLogMessage msg = new TransformerLogMessage(severity, logTag, message, String.valueOf(currentCycleId), System.currentTimeMillis(), config.getAwsInstanceId());

        ObjectWriter writer = objectMapper.writer();
        return writer.writeValueAsString(msg);
    }


    private String buildIndexName() {
        final StringBuilder builder = new StringBuilder("vms-");
        builder.append(config.getTransformerVip()).append("-cyc_").append(currentCycleId / 1000000000);
        return builder.toString().toLowerCase();
    }

}
