package com.netflix.vms.transformer.common.publish.workflow;

import java.util.Map;

import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.query.ColumnFamilyQuery;

public interface TransformerCassandraHelper {
    void addVipKeyValuePair(String vip, String key, String value) throws ConnectionException;

    void addKeyValuePair(String key, String value) throws ConnectionException;

    void addKeyColumnValue(String key, String columnName, String value) throws ConnectionException;

    String getVipKeyValuePair(String vip, String key) throws ConnectionException;

    String getKeyValuePair(String key) throws ConnectionException;

    String getKeyColumnValue(String key, String columnName) throws ConnectionException;

    Map<String, String> getColumns(String key) throws ConnectionException;

    MutationBatch createMutationBatch();

    ColumnFamilyQuery<String, String> createQuery();

    ColumnFamily<String, String> getColumnFamily();

    String vipSpecificKey(String vip, String key);
}
