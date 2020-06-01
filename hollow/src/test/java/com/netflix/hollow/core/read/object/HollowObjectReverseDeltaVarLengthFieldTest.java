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
package com.netflix.hollow.core.read.object;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectReverseDeltaVarLengthFieldTest extends AbstractStateEngineTest {


	@Test
	public void test() throws IOException {
		HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
		
		mapper.add("one");
		mapper.add("two");
		mapper.add("three");
		mapper.add("four");
		
		roundTripSnapshot();
		
		mapper.add("one");
		mapper.add("four");
		
		roundTripSnapshot();
		
		mapper.add("one");
		
		byte reverseDelta1[] = getReverseDelta();

		roundTripDelta();
		
		mapper.add("one");
		mapper.add("two");
		
		byte reverseDelta2[] = getReverseDelta();
		
		roundTripDelta();

		HollowBlobReader reader = new HollowBlobReader(readStateEngine);
		reader.applyDelta(HollowBlobInput.serial(reverseDelta2));
		reader.applyDelta(HollowBlobInput.serial(reverseDelta1));
		
		Assert.assertEquals("four", ((HollowObjectTypeReadState)readStateEngine.getTypeState("String")).readString(3, 0));
	}

	private byte[] getReverseDelta() throws IOException {
		ByteArrayOutputStream reverseDelta = new ByteArrayOutputStream();
		HollowBlobWriter writer = new HollowBlobWriter(writeStateEngine);
		writer.writeReverseDelta(reverseDelta);
		return reverseDelta.toByteArray();
	}
	
	@Override
	protected void initializeTypeStates() {
		new HollowObjectMapper(writeStateEngine).initializeTypeState(String.class);
	}
	
}
