/*
 * Copyright 2015 AML Innovation & Consulting LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.amlinv.activemq.topo.discovery;

import com.amlinv.activemq.topo.jmxutil.polling.JmxActiveMQUtil;
import com.amlinv.activemq.topo.registry.DestinationRegistry;
import com.amlinv.activemq.topo.registry.model.DestinationState;
import com.amlinv.jmxutil.connection.MBeanAccessConnection;
import com.amlinv.jmxutil.connection.MBeanAccessConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.slf4j.Logger;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by art on 8/29/15.
 */
public class MBeanDestinationDiscovererTest {

    private MBeanDestinationDiscoverer discoverer;

    private DestinationRegistry mockDestinationRegistry;
    private MBeanAccessConnectionFactory mockAccessConnectionFactory;
    private MBeanAccessConnection mockAccessConnection;
    private DestinationState mockState1;
    private DestinationState mockState2;
    private Logger mockLogger;

    private ObjectName searchDestinationPattern;

    private Set<ObjectName> destObjectNameSet001;
    private Set<ObjectName> destObjectNameSet002;

    @Before
    public void setupTest() throws Exception {
        this.discoverer = new MBeanDestinationDiscoverer("x-dest-type-x", "x-broker-id-x");

        this.mockDestinationRegistry = Mockito.mock(DestinationRegistry.class);
        this.mockAccessConnectionFactory = Mockito.mock(MBeanAccessConnectionFactory.class);
        this.mockAccessConnection = Mockito.mock(MBeanAccessConnection.class);
        this.mockState1 = Mockito.mock(DestinationState.class);
        this.mockState2 = Mockito.mock(DestinationState.class);
        this.mockLogger = Mockito.mock(Logger.class);

        this.searchDestinationPattern = JmxActiveMQUtil.getDestinationObjectName("*", "*", "x-dest-type-x");

        this.destObjectNameSet001 = new HashSet<>();
        this.destObjectNameSet001.add(new ObjectName("x-domain-x:destinationName=x-dest1-x"));
        this.destObjectNameSet001.add(new ObjectName("x-domain-x:destinationName=x-dest2-x"));

        this.destObjectNameSet002 = new HashSet<>();
        this.destObjectNameSet002.add(new ObjectName("x-domain-x:destinationName=x-dest1-x"));
    }

    @Test
    public void testGetSetRegistry() throws Exception {
        assertNull(this.discoverer.getRegistry());

        this.discoverer.setRegistry(this.mockDestinationRegistry);
        assertSame(this.mockDestinationRegistry, this.discoverer.getRegistry());
    }

    @Test
    public void testGetSetBrokerName() throws Exception {
        assertEquals("*", this.discoverer.getBrokerName());

        this.discoverer.setBrokerName("x-broker-name-x");
        assertEquals("x-broker-name-x", this.discoverer.getBrokerName());
    }

    @Test
    public void testGetSetDestinationType() throws Exception {
        assertEquals("x-dest-type-x", this.discoverer.getDestinationType());

        this.discoverer.setDestinationType("x-dest-type2-x");
        assertEquals("x-dest-type2-x", this.discoverer.getDestinationType());
    }

    @Test
    public void testGetSetmBeanAccessConnectionFactory() throws Exception {
        assertNull(this.discoverer.getmBeanAccessConnectionFactory());

        this.discoverer.setmBeanAccessConnectionFactory(this.mockAccessConnectionFactory);
        assertEquals(this.mockAccessConnectionFactory, this.discoverer.getmBeanAccessConnectionFactory());
    }

    @Test
    public void testGetSetLog() throws Exception {
        assertNotNull(this.discoverer.getLog());
        assertNotSame(this.mockLogger, this.discoverer.getLog());

        this.discoverer.setLog(this.mockLogger);
        assertSame(this.mockLogger, this.discoverer.getLog());
    }

    @Test
    public void testPollOnce() throws Exception {
        //
        // SETUP
        //
        this.preparePoll();
        Mockito.when(this.mockAccessConnection.queryNames(this.searchDestinationPattern, null))
                .thenReturn(this.destObjectNameSet001);

        //
        // EXECUTE
        //
        this.discoverer.pollOnce();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockDestinationRegistry)
                .putIfAbsent(Mockito.eq("x-dest1-x"), this.matchDestinationState("x-dest1-x", "x-broker-id-x"));
        Mockito.verify(this.mockDestinationRegistry)
                .putIfAbsent(Mockito.eq("x-dest2-x"), this.matchDestinationState("x-dest2-x", "x-broker-id-x"));
    }

    @Test
    public void testPollDetectsDestinationRemoval() throws Exception {
        //
        // SETUP
        //
        this.preparePoll();
        Mockito.when(this.mockDestinationRegistry.keys())
                .thenReturn(new HashSet<String>(Arrays.asList("x-dest1-x", "x-dest2-x")));
        Mockito.when(this.mockAccessConnection.queryNames(this.searchDestinationPattern, null))
                .thenReturn(this.destObjectNameSet002);
        Mockito.when(this.mockState2.existsAnyBroker()).thenReturn(false);

        //
        // EXECUTE
        //
        this.discoverer.pollOnce();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockDestinationRegistry).remove("x-dest2-x");
    }

    @Test
    public void testDestinationUnseenFromRegistryDuringPoll() throws Exception {
        //
        // SETUP
        //
        this.preparePoll();
        Mockito.when(this.mockDestinationRegistry.keys())
                .thenReturn(new HashSet<String>(Arrays.asList("x-dest1-x", "x-dest2-x")));
        Mockito.when(this.mockAccessConnection.queryNames(this.searchDestinationPattern, null))
                .thenReturn(this.destObjectNameSet002);
        Mockito.when(this.mockState1.existsAnyBroker()).thenReturn(false);
        Mockito.when(this.mockState2.existsAnyBroker()).thenReturn(true);


        //
        // EXECUTE
        //
        this.discoverer.pollOnce();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockState2).putBrokerInfo("x-broker-id-x", false);
    }

    @Test
    public void testDestinationLostFromRegistryDuringPoll() throws Exception {
        //
        // SETUP
        //
        this.preparePoll();
        Mockito.when(this.mockDestinationRegistry.keys())
                .thenReturn(new HashSet<String>(Arrays.asList("x-dest1-x", "x-dest2-x")));
        Mockito.when(this.mockAccessConnection.queryNames(this.searchDestinationPattern, null))
                .thenReturn(this.destObjectNameSet002);
        Mockito.when(this.mockDestinationRegistry.get("x-dest2-x")).thenReturn(null);


        //
        // EXECUTE
        //
        this.discoverer.pollOnce();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockDestinationRegistry, Mockito.times(0)).remove("x-dest2-x");
        Mockito.verify(this.mockState2, Mockito.times(0)).putBrokerInfo("x-broker-id-x", false);
    }

    @Test
    public void testPollDetectsDestinationReturn() throws Exception {
        //
        // SETUP
        //
        this.preparePoll();
        Mockito.when(this.mockDestinationRegistry.keys())
                .thenReturn(new HashSet<String>(Arrays.asList("x-dest1-x")));
        Mockito.when(this.mockAccessConnection.queryNames(this.searchDestinationPattern, null))
                .thenReturn(this.destObjectNameSet002);
        Mockito.when(this.mockDestinationRegistry
                .putIfAbsent(Mockito.eq("x-dest1-x"), this.matchDestinationState("x-dest1-x", "x-broker-id-x")))
                .thenReturn(this.mockState1);

        //
        // EXECUTE
        //
        this.discoverer.pollOnce();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockState1).putBrokerInfo("x-broker-id-x", true);
    }

    @Test
    public void testInvalidObjectNameOnPoll() throws Exception {
        //
        // SETUP
        //
        this.preparePoll();
        // Use an invalid broker name
        this.discoverer.setBrokerName(":");


        //
        // EXECUTE
        //
        try {
            this.discoverer.pollOnce();
            fail("missing expected exception");
        } catch (RuntimeException rtExc) {
            //
            // VALIDATE
            //
            assertTrue(rtExc.getCause() instanceof MalformedObjectNameException);
        }
    }

    @Test
    public void testExceptionOnSafeClose() throws Exception {
        //
        // SETUP
        //
        this.preparePoll();
        this.discoverer.setLog(this.mockLogger);
        IOException ioExc = new IOException("x-io-exc-x");
        Mockito.doThrow(ioExc).when(mockAccessConnection).close();


        //
        // EXECUTE
        //
        this.discoverer.pollOnce();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockLogger).warn("failed to close mbean access connection cleanly", ioExc);
    }

    @Test
    public void testNullDestinationNameOnPoll() throws Exception {
        //
        // SETUP
        //
        this.preparePoll();
        Mockito.when(this.mockAccessConnection.queryNames(this.searchDestinationPattern, null))
                .thenReturn(new HashSet<ObjectName>(Arrays.asList(new ObjectName("x-domain-x:type=Broker"))));


        //
        // EXECUTE
        //
        this.discoverer.pollOnce();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockDestinationRegistry, Mockito.times(0))
                .putIfAbsent(Mockito.anyString(), Mockito.any(DestinationState.class));
    }

    @Test
    public void testEmptyDestinationNameOnPoll() throws Exception {
        //
        // SETUP
        //
        this.preparePoll();
        Mockito.when(this.mockAccessConnection.queryNames(this.searchDestinationPattern, null))
                .thenReturn(new HashSet<ObjectName>(Arrays.asList(new ObjectName("x-domain-x:destinationName="))));


        //
        // EXECUTE
        //
        this.discoverer.pollOnce();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockDestinationRegistry, Mockito.times(0))
                .putIfAbsent(Mockito.anyString(), Mockito.any(DestinationState.class));
    }


                                                 ////             ////
                                                 ////  INTERNALS  ////
                                                 ////             ////

    protected void preparePoll() throws Exception {
        this.discoverer.setmBeanAccessConnectionFactory(this.mockAccessConnectionFactory);
        this.discoverer.setRegistry(this.mockDestinationRegistry);

        Mockito.when(this.mockAccessConnectionFactory.createConnection()).thenReturn(this.mockAccessConnection);
        Mockito.when(this.mockDestinationRegistry.get("x-dest1-x")).thenReturn(this.mockState1);
        Mockito.when(this.mockDestinationRegistry.get("x-dest2-x")).thenReturn(this.mockState2);
    }

    protected DestinationState matchDestinationState(final String destName, final String brokerId) {
        ArgumentMatcher<DestinationState> argumentMatcher;
        argumentMatcher = new ArgumentMatcher<DestinationState>() {
            @Override
            public boolean matches(Object obj) {
                if (obj instanceof DestinationState) {
                    DestinationState actual = (DestinationState) obj;

                    if (destName.equals(actual.getName())) {
                        if (actual.existsOnBroker(brokerId)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        };

        return Mockito.argThat(argumentMatcher);
    }
}
