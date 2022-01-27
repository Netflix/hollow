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
package com.netflix.hollow.tools.history;

import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateKeyOrdinalMapping;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import java.util.Map;

/**
 * A data state from the past, represented as just the changes which happened on the subsequent transition.
 * Contains links to all subsequent deltas which happened in the interim between the this state 
 * and the now current state.
 *
 */
public class HollowHistoricalState {

    private long version;
    private final HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping;
    private final HollowHistoricalStateDataAccess dataAccess;
    private final Map<String, String> headerEntries;
    private HollowHistoricalState nextState;
    private boolean reverseDelta = false;

    public HollowHistoricalState(long version, HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping, HollowHistoricalStateDataAccess dataAccess, Map<String, String> headerEntries, boolean reverseDelta) {
        this(version, keyOrdinalMapping, dataAccess, headerEntries);
        this.reverseDelta = reverseDelta;
    }

    public HollowHistoricalState(long version, HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping, HollowHistoricalStateDataAccess dataAccess, Map<String, String> headerEntries) {
        this.version = version;
        this.dataAccess = dataAccess;
        this.keyOrdinalMapping = keyOrdinalMapping;
        this.headerEntries = headerEntries;
    }

    public boolean IsReverseDelta() {
        return reverseDelta;
    }

    /**
     * @return The version of this state
     */
    public long getVersion() {
        return version;
    }

    public void setVersion(long newVersion) {
        version = newVersion;
    }
    /**
     * @return A {@link HollowDataAccess} which can be used to retrieve the data from this state.  For example,
     * you can use this with a generated Hollow API or the generic hollow object API.
     */
    public HollowHistoricalStateDataAccess getDataAccess() {
        return dataAccess;
    }

    /**
	 * To find a specific historical record which changed
	 * in this state:  
	 * <ul>
	 * <li>Use the {@link HollowHistoryKeyIndex} from the {@link HollowHistory} to look up a <i>key ordinal</i> by an indexed primary key.</li>
	 * <li>Use the retrieved <i>key ordinal</i> with the {@link HollowHistoricalStateKeyOrdinalMapping} in this state to find the record's ordinal in this state.</li>
	 * </ul>
	 * <p>
	 * If a change isn't found for the key ordinal in this state, you can try walking the chain of states up to
	 * the present using successive calls to {@link #getNextState()}
     * 
     * @return the historical state key ordinal mapping
     */
    public HollowHistoricalStateKeyOrdinalMapping getKeyOrdinalMapping() {
        return keyOrdinalMapping;
    }

    /**
     * @return The subsequent historical state which occurred after this one 
     */
    public HollowHistoricalState getNextState() {
        return nextState;
    }

    /**
     * @return The blob header entries from this state.
     */
    public Map<String, String> getHeaderEntries() {
        return headerEntries;
    }

    void setNextState(HollowHistoricalState nextState) {
        this.nextState = nextState;
    }

}
