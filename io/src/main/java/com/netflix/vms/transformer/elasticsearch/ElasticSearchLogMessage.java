package com.netflix.vms.transformer.elasticsearch;

public class ElasticSearchLogMessage {
    private final String indexName;
    private final String indexType;
    private final String jsonData;

    public ElasticSearchLogMessage(String indexName, String indexType, String jsonData) {
        this.indexName = indexName;
        this.indexType = indexType;
        this.jsonData = jsonData;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexType() {
        return indexType;
    }

    public String getJsonData() {
        return jsonData;
    }

    @Override
    public String toString() {
        return "ElasticSearchLogMessage [indexName=" + indexName + ", indexType=" + indexType + ", jsonData=" + jsonData + "]";
    }
}
