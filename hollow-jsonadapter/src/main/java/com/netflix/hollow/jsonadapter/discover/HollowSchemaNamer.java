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
package com.netflix.hollow.jsonadapter.discover;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HollowSchemaNamer {

    private final ConcurrentHashMap<String, Map<String, String>> listNames = new ConcurrentHashMap<String, Map<String, String>>();
    private final ConcurrentHashMap<String, Map<String, String>> subObjectNames = new ConcurrentHashMap<String, Map<String, String>>();

    public String subObjectName(String typeName, String prefix, String fieldName) {
        return subTypeName(subObjectNames, typeName, prefix, fieldName);
    }

    public String subCollectionName(String typeName, String prefix, String fieldName) {
        return subTypeName(listNames, typeName, prefix, fieldName);
    }

    public String subTypeName(ConcurrentHashMap<String, Map<String, String>> subNamesMap, String typeName, String prefix, String fieldName) {
        Map<String, String> typeNamesMap = subNamesMap.get(typeName);
        if(typeNamesMap == null) {
            synchronized (subNamesMap) {
                typeNamesMap = subNamesMap.get(typeName);
                if(typeNamesMap == null) {
                    typeNamesMap = new ConcurrentHashMap<String, String>();
                    Map<String, String> existingMap = subNamesMap.putIfAbsent(typeName, typeNamesMap);
                    if(existingMap != null)
                        typeNamesMap = existingMap;
                }
            }
        }

        String name = typeNamesMap.get(fieldName);
        if(name == null) {
            synchronized (typeNamesMap) {
                name = typeNamesMap.get(fieldName);
                if(name == null) {
                    name = typeName + prefix + uppercaseFirstCharacter(fieldName);
                    name = name.intern();
                    typeNamesMap.put(fieldName, name);
                }
            }
        }
        return name;
    }

    public String schemaNameFromPropertyPath(String propertyPath) {
        StringBuilder schemaName = new StringBuilder();

        while(propertyPath.indexOf('.') != -1) {
            String nextToken = propertyPath.substring(0, propertyPath.indexOf('.'));
            schemaName.append(uppercaseFirstCharacter(nextToken));
            propertyPath = propertyPath.substring(propertyPath.indexOf('.') + 1);
        }

        schemaName.append(uppercaseFirstCharacter(propertyPath));

        return schemaName.toString();
    }

    private String uppercaseFirstCharacter(String value) {
        if(value == null) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

}
