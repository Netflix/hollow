/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core;

import com.netflix.hollow.core.schema.HollowSchema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the header of a serialized blob.  A blob header contains the following elements:  
 * 
 * <dl>
 *      <dt>Header Tags</dt>
 *      <dd>A set of user-specified key-value pairs -- typically used to communicate overall context about the blob's data.</dd>
 *      
 *      <dt>Randomized Tags</dt>
 *      <dd>A single randomized 64-bit value is randomly generated per data state.  This tag is tracked at the client 
 *          as a safety mechanism to ensure that delta transitions are never applied to an incorrect state.</dd>
 *          
 *      <dt>Blob Format Version</dt>
 *      <dd>A 32-bit value used to identify the format of the hollow blob.</dd>
 *      
 * </dl>
 * 
 * @author dkoszewnik
 *
 */
public class HollowBlobHeader {

    public static final int HOLLOW_BLOB_VERSION_HEADER = 1030;

    private Map<String, String> headerTags = new HashMap<String, String>();
    private List<HollowSchema> schemas = new ArrayList<HollowSchema>();
    private long originRandomizedTag;
    private long destinationRandomizedTag;
    private int blobFormatVersion = HOLLOW_BLOB_VERSION_HEADER;

    public Map<String, String> getHeaderTags() {
        return headerTags;
    }
    
    public void setSchemas(List<HollowSchema> schemas) {
        this.schemas = schemas;
    }
    
    public List<HollowSchema> getSchemas() {
        return schemas;
    }

    public void setHeaderTags(Map<String, String> headerTags) {
        this.headerTags = headerTags;
    }

    public long getOriginRandomizedTag() {
        return originRandomizedTag;
    }

    public void setOriginRandomizedTag(long originRandomizedTag) {
        this.originRandomizedTag = originRandomizedTag;
    }

    public long getDestinationRandomizedTag() {
        return destinationRandomizedTag;
    }

    public void setDestinationRandomizedTag(long destinationRandomizedTag) {
        this.destinationRandomizedTag = destinationRandomizedTag;
    }

    public void setBlobFormatVersion(int blobFormatVersion) {
        this.blobFormatVersion = blobFormatVersion;
    }

    public int getBlobFormatVersion() {
        return blobFormatVersion;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof HollowBlobHeader) {
            HollowBlobHeader oh = (HollowBlobHeader)other;
            return blobFormatVersion == oh.blobFormatVersion
                    && headerTags.equals(oh.getHeaderTags())
                    && originRandomizedTag == oh.originRandomizedTag
                    && destinationRandomizedTag == oh.destinationRandomizedTag;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = blobFormatVersion;
        result = 31 * result + Objects.hash(headerTags,
                originRandomizedTag,
                destinationRandomizedTag,
                blobFormatVersion);
        return result;
    }
}
