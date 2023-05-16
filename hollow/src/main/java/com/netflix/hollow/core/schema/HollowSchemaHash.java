package com.netflix.hollow.core.schema;

import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class HollowSchemaHash {
    private final String hash;

    public HollowSchemaHash(String hash) {
        this.hash = hash;
    }

    public HollowSchemaHash(HollowStateEngine stateEngine) {
        this(stateEngine.getSchemas());
    }

    public HollowSchemaHash(Collection<HollowSchema> schemas) {
        // Order the Schemas
        Map<String, HollowSchema> schemaMap = new TreeMap<>();
        schemas.forEach( s -> schemaMap.put(s.getName(), s));

        // serialize
        StringBuilder schemaSB = new StringBuilder();
        schemaMap.forEach( (k, v) -> schemaSB.append(v));

        this.hash = calculateHash(schemaSB.toString());
    }

    private String calculateHash(String schemaString) {
        int hashCode = HashCodes.hashCode(schemaString);
        return String.valueOf(hashCode);
    }

    public String getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HollowSchemaHash that = (HollowSchemaHash) o;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    @Override
    public String toString() {
        return getHash();
    }
}
