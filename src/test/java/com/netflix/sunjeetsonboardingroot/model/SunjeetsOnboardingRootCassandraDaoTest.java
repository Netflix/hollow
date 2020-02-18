package com.netflix.sunjeetsonboardingroot.model;

import com.datastax.driver.core.Session;
import com.google.inject.Inject;
import com.netflix.aeneas.AeneasCassandraCqlUnit;
import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.cinder.consumer.NFHollowBlobRetriever;
import com.netflix.cinder.lifecycle.CinderConsumerModule;
import com.netflix.governator.guice.test.ModulesForTesting;
import com.netflix.governator.guice.test.junit4.GovernatorJunit4ClassRunner;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.runtime.lifecycle.RuntimeCoreModule;
import java.util.function.Supplier;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test starts an embedded cassandra server that matches what Netflix runs in the cloud.
 * The AeneasCassandraCqlUnit API follow the cassandra-unit API. You can find more examples here:
 * https://github.com/jsevellec/cassandra-unit-examples
 *
 * @author This file is auto-generated by runtime@netflix.com. Feel free to modify.
 */
//@RunWith(GovernatorJunit4ClassRunner.class)
//@ModulesForTesting({CinderConsumerModule.class, RuntimeCoreModule.class})
public class SunjeetsOnboardingRootCassandraDaoTest {

    //@Inject Supplier<CinderConsumerBuilder> cinderConsumerBuilder;

    @Rule
    public AeneasCassandraCqlUnit cassandra = new AeneasCassandraCqlUnit(
            new ClassPathCQLDataSet("setUpGreetingsDao.cql", "sunjeetsonboardingroot"));

    private SunjeetsOnboardingRootCassandraDao testDao;

    @Before
    public void before() {
        Session session = cassandra.getSession();
        testDao = new SunjeetsOnboardingRootCassandraDao(session);
    }


    // @Test
    // public void testHollowConsumer() {//
        // // For topic name hollow.vms-feather.snapshot version 20190629023118000 Gutenberg doesn't return any metadata
        // GutenbergFileConsumer proxyFileConsumer = GutenbergFileConsumer.localProxyForProdEnvironment();//
        // HollowConsumer consumer = cinderConsumerBuilder.get()
                // .forNamespace("vms-feather")
                // .withBlobRetriever(new NFHollowBlobRetriever(proxyFileConsumer, "vms-feather"))
                // .noAnnouncementWatcher()
                // .build();//
        // consumer.triggerRefreshTo(20190706030056042l);//
        // System.out.println("SNAP: Yay");
    // }

    @Test
    public void testLoadExistingGreeting() {
        Greeting storedGreeting = testDao.loadGreeting("yodle@yodle.net").get();
        Assert.assertEquals("yodle@yodle.net", storedGreeting.getUserEmail());
        Assert.assertEquals("Yodle", storedGreeting.getFirstName());
        Assert.assertEquals("Yo", storedGreeting.getMessage());
    }

    @Test
    public void testStoreThenLoadGreeting() {
        Greeting greeting = new Greeting();
        greeting.setUserEmail("bobby@bobby.net");
        greeting.setFirstName("Bobby");
        greeting.setMessage("Howdy");
        testDao.storeGreeting(greeting);
        Greeting storedGreeting = testDao.loadGreeting("bobby@bobby.net").get();
        Assert.assertEquals("bobby@bobby.net", storedGreeting.getUserEmail());
        Assert.assertEquals("Bobby", storedGreeting.getFirstName());
        Assert.assertEquals("Howdy", storedGreeting.getMessage());
    }
}