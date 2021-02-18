/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.core.read;

import com.netflix.hollow.core.memory.MemoryMode;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OptionalBlobPartInput {

    private final Map<String, Object> inputsByPartName;

    public OptionalBlobPartInput() {
        this.inputsByPartName = new HashMap<>();
    }
    
    public void addInput(String partName, File file) {
        inputsByPartName.put(partName, file);
    }

    public void addInput(String partName, InputStream in) {
        inputsByPartName.put(partName, in);
    }

    public File getFile(String partName) {
        Object f = inputsByPartName.get(partName);
        if(f instanceof File)
            return (File)f;
        throw new UnsupportedOperationException();
    }
    
    public InputStream getInputStream(String partName) throws IOException {
        Object o = inputsByPartName.get(partName);
        if(o instanceof File)
            return new BufferedInputStream(new BufferedInputStream(new FileInputStream((File)o)));
        return (InputStream)o;
    }
    
    public Set<String> getPartNames() {
        return inputsByPartName.keySet();
    }
    
    public Map<String, HollowBlobInput> getInputsByPartName(MemoryMode mode) throws IOException {
        Map<String, HollowBlobInput> map = new HashMap<>(inputsByPartName.size());
        
        for(String part : getPartNames()) {
            map.put(part, HollowBlobInput.modeBasedSelector(mode, this, part));
        }
        
        return map;

    }
    
    public Map<String, InputStream> getInputStreamsByPartName() throws IOException {
        Map<String, InputStream> map = new HashMap<>(inputsByPartName.size());
        
        for(String part : getPartNames()) {
            map.put(part, getInputStream(part));
        }
        
        return map;
    }

}
