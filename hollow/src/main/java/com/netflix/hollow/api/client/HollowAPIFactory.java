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
package com.netflix.hollow.api.client;

import com.netflix.hollow.api.codegen.HollowAPIClassJavaGenerator;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An interface which can be implemented and passed to a {@link HollowClient} to inject the {@link HollowAPI} creation behavior.
 * 
 * This is used to cause the HollowClient to create a specific api which has been generated (via the {@link HollowAPIClassJavaGenerator})
 * to conform to a specific data model. 
 * 
 * A default implementation, which will create a default {@link HollowAPI} is available at {@link HollowAPIFactory#DEFAULT_FACTORY}.
 *
 */
public interface HollowAPIFactory {

    public HollowAPI createAPI(HollowDataAccess dataAccess);

    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI);


    public static HollowAPIFactory DEFAULT_FACTORY = new HollowAPIFactory() {

        @Override
        public HollowAPI createAPI(HollowDataAccess dataAccess) {
            return new HollowAPI(dataAccess);
        }

        @Override
        public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
            return createAPI(dataAccess);
        }

    };

    public static class ForGeneratedAPI<T extends HollowAPI> implements HollowAPIFactory {

        private final Class<T> generatedAPIClass;
        private final Set<String> cachedTypes;

        public ForGeneratedAPI(Class<T> generatedAPIClass) {
            this(generatedAPIClass, new String[0]);
        }

        public ForGeneratedAPI(Class<T> generatedAPIClass, String... cachedTypes) {
            this.generatedAPIClass = generatedAPIClass;
            this.cachedTypes = new HashSet<String>(Arrays.asList(cachedTypes));
        }


        @Override
        public T createAPI(HollowDataAccess dataAccess) {
            try {
                Constructor<T> constructor = generatedAPIClass.getConstructor(HollowDataAccess.class, Set.class);
                return constructor.newInstance(dataAccess, cachedTypes);
            } catch (Exception e) {
                try {
                    Constructor<T> constructor = generatedAPIClass.getConstructor(HollowDataAccess.class);
                    return constructor.newInstance(dataAccess);
                } catch (Exception e2) {
                    throw new RuntimeException(e2);
                }
            }
        }

        @Override
        public T createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
            try {
                Constructor<T> constructor = generatedAPIClass.getConstructor(HollowDataAccess.class, Set.class, Map.class, generatedAPIClass);
                return constructor.newInstance(dataAccess, cachedTypes, Collections.emptyMap(), previousCycleAPI);
            } catch (Exception e) {
                try {
                    Constructor<T> constructor = generatedAPIClass.getConstructor(HollowDataAccess.class);
                    return constructor.newInstance(dataAccess);
                } catch (Exception e2) {
                    throw new RuntimeException(e2);
                }
            }
        }
    }

}
