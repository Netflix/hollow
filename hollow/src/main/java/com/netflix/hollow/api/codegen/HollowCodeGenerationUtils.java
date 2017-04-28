/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.api.codegen;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;

import com.netflix.hollow.api.objects.delegate.HollowListCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;

/**
 * A class containing convenience methods for the {@link HollowAPIGenerator}.  Not intended for external consumption.
 */
public class HollowCodeGenerationUtils {

    public static String typeAPIClassname(String typeName) {
        return uppercase(typeName) + "TypeAPI";
    }

    public static String hollowFactoryClassname(String typeName) {
        return hollowImplClassname(typeName, "Hollow") + "Factory";
    }

    public static String hollowObjectProviderName(String typeName) {
        return substituteInvalidChars(lowercase(typeName)) + "Provider";
    }

    public static String hollowImplClassname(String typeName, String classPostfix) {
        return substituteInvalidChars(uppercase(typeName)) + classPostfix;
    }

    public static String delegateInterfaceName(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "Delegate";
    }

    public static String delegateInterfaceName(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return delegateInterfaceName(schema.getName());
        if(schema instanceof HollowListSchema)
            return HollowListDelegate.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetDelegate.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapDelegate.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

    public static String delegateCachedImplName(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "DelegateCachedImpl";
    }

    public static String delegateCachedClassname(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return delegateCachedImplName(schema.getName());
        if(schema instanceof HollowListSchema)
            return HollowListCachedDelegate.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetCachedDelegate.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapCachedDelegate.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

    public static String delegateLookupImplName(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "DelegateLookupImpl";
    }

    public static String delegateLookupClassname(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return delegateLookupImplName(schema.getName());
        if(schema instanceof HollowListSchema)
            return HollowListLookupDelegate.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetLookupDelegate.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapLookupDelegate.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

    public static String lowercase(String str) {
        if(str == null || str.length() == 0)
            return str;

        StringBuilder builder = new StringBuilder();

        builder.append(str.substring(0, 1).toLowerCase());
        builder.append(str.substring(1));

        return builder.toString();
    }

    public static String uppercase(String str) {
        if(str == null || str.length() == 0)
            return str;

        StringBuilder builder = new StringBuilder();

        builder.append(str.substring(0, 1).toUpperCase());
        builder.append(str.substring(1));

        return builder.toString();
    }

    public static String substituteInvalidChars(String str) {
        str = str.replace(' ', '_');
        str = str.replace('.', '_');
        return str;
    }
}
