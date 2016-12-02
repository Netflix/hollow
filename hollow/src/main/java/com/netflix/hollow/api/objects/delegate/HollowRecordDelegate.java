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
package com.netflix.hollow.api.objects.delegate;

/**
 * A HollowRecordDelegate is used by a generated Hollow Objects API to access data from the data model.
 * <p>
 * Two flavors of delegate currently exist -- lookup and cached.
 * <p>
 * The lookup delegate reads directly from a HollowDataAccess.  The cached delegate will copy the data from a HollowDataAccess,
 * then read from the copy of the data.  The intention is that the cached delegate has the performance profile of a POJO, while
 * the lookup delegate imposes the minor performance penalty incurred by reading directly from Hollow.
 * <p>
 * The performance penalty of a lookup delegate is minor enough that it doesn't usually matter except in the tightest of loops.  
 * If a type exists which has a low cardinality but is accessed disproportionately frequently, then it may be a good candidate
 * to be represented with a cached delegate. 
 * 
 */
public interface HollowRecordDelegate {

}
