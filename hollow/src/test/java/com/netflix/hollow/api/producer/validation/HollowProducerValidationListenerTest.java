/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.producer.validation;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Announcer;
import com.netflix.hollow.api.producer.HollowProducer.Blob;
import com.netflix.hollow.api.producer.HollowProducer.Builder;
import com.netflix.hollow.api.producer.HollowProducer.Populator;
import com.netflix.hollow.api.producer.HollowProducer.Publisher;
import com.netflix.hollow.api.producer.HollowProducer.Validator;
import com.netflix.hollow.api.producer.HollowProducer.Validator.ValidationException;
import com.netflix.hollow.api.producer.HollowProducer.WriteState;
import com.netflix.hollow.api.producer.HollowProducerListener.Status;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

public class HollowProducerValidationListenerTest {

	private HollowProducer hollowProducer;
	private HollowValidationFakeListener validationListener;
	private Publisher publisher;
	private Announcer announcer;

	@Before
	public void setup(){
		publisher = new Publisher() {
			@Override
			public void publish(Blob blob) {
				// TODO Auto-generated method stub
			}
		};
		announcer = new Announcer() {
			@Override
			public void announce(long stateVersion) {
				// TODO Auto-generated method stub
			}
		};
	}
	
	@Test
	public void testValidationListenerOnValidationSuccess(){
		createHollowProducerAndRunCycle("MovieWithPrimaryKey", true);
		assertOnValidationStatus(2, Status.SUCCESS, true, validationListener.getVersion());
	}
	
	
	@Test(expected=ValidationException.class)
	public void testValidationListenerOnFailure(){
		createHollowProducerAndRunCycle("MovieWithoutPrimaryKey", true);
		assertOnValidationStatus(2, Status.FAIL, false, validationListener.getVersion());
	}
	
	@Test
	public void testValidationListenerWithOnlyRecordCountValidator(){
		createHollowProducerAndRunCycle("MovieWithPrimaryKey", false);
		assertOnValidationStatus(1, Status.SUCCESS, true, validationListener.getVersion());
		// Expecting only record count validator status
		SingleValidationStatus validatorStatus = validationListener.getStatus().getValidationStatusList().get(0);
		Assert.assertNotNull(validatorStatus);
		// ValidationStatus builds record validator status based toString of RecordCountValidatorStatus for now.
		Assert.assertEquals(Status.SUCCESS, validatorStatus.getStatus());
		Assert.assertNull(validatorStatus.getThrowable());
		// Record count validator would have skipped validation because the previous record count is 0 in this test. 
		// But that status for now is only passed as string through toString method of the validator. 
		Assert.assertTrue(validatorStatus.getMessage().contains("MovieWithPrimaryKey"));
		Assert.assertTrue(validatorStatus.getMessage().contains("Previous record count is 0"));
	}

	private void createHollowProducerAndRunCycle(final String typeName, boolean addPrimaryKeyValidator) {
		Validator dupeValidator = new DuplicateDataDetectionValidator(typeName);
		Validator countValidator = new RecordCountVarianceValidator(typeName, 3.0f);
		validationListener = new HollowValidationFakeListener();
		Builder builder = HollowProducer.withPublisher(publisher).withAnnouncer(announcer)
				.withValidationListeners(validationListener)
				.withValidator(countValidator);
		if(addPrimaryKeyValidator)
			builder = builder.withValidator(dupeValidator);
		
		hollowProducer = builder.build();
		if(typeName.equals("MovieWithPrimaryKey"))
			hollowProducer.initializeDataModel(MovieWithPrimaryKey.class);
		else 
			hollowProducer.initializeDataModel(MovieWithoutPrimaryKey.class);
		
		hollowProducer.runCycle(new Populator() {
			@Override
			public void populate(WriteState newState) throws Exception {
				List<String> actors = Arrays.asList("Angelina Jolie", "Brad Pitt");
				if(typeName.equals("MovieWithPrimaryKey")){
					newState.add(new MovieWithPrimaryKey(123, "someTitle1", actors));
					newState.add(new MovieWithPrimaryKey(123, "someTitle1", actors));
				} else {
					newState.add(new MovieWithoutPrimaryKey(123, "someTitle1", actors));
					newState.add(new MovieWithoutPrimaryKey(1233, "someTitle2", actors));
				}
			}
		});
	}

	private void assertOnValidationStatus(int size, Status result, boolean isThrowableNull, long version) {
		AllValidationStatus status = validationListener.getStatus();
		Assert.assertNotNull("Stats null indicates HollowValidationFakeListener.onValidationComplete() was not called on runCycle.", status);
		Assert.assertEquals(size, status.getValidationStatusList().size());
		Assert.assertEquals(result, status.getStatus());
	}
}

@HollowPrimaryKey(fields = { "id" })
class MovieWithPrimaryKey extends MovieWithoutPrimaryKey{

	public MovieWithPrimaryKey(int id, String title, List<String> actors) {
		super(id, title, actors);
	}
}

class MovieWithoutPrimaryKey{
	private final int id;
	private final String title;
	private final List<String> actors;
	
	public MovieWithoutPrimaryKey(int id, String title, List<String> actors) {
		this.id = id;
		this.title = title;
		this.actors = actors;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getActors() {
		return actors;
	}
}

class HollowValidationFakeListener implements HollowValidationListener {
	private long version;
	private AllValidationStatus status;
	@Override
	public void onValidationStart(long version) {
		this.version = version;
	}

	@Override
	public void onValidationComplete(AllValidationStatus status, long elapsed, TimeUnit unit) {
		this.status = status;
	}

	public long getVersion() {
		return version;
	}

	public AllValidationStatus getStatus() {
		return status;
	}
	public void reset(){
		version = -1;
		status = null;
	}
}

