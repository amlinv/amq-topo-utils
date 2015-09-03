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

package com.amlinv.activemq.topo.jmxutil.polling;

import com.amlinv.jmxutil.connection.MBeanAccessConnection;
import com.amlinv.jmxutil.connection.MBeanAccessConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.management.ObjectName;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by art on 8/30/15.
 */
public class JmxActiveMQUtilTest2 {

    public static final String TEST_JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";

    private JmxActiveMQUtil2 util;

    private MBeanAccessConnectionFactoryUtil mockFactoryUtil;
    private MBeanAccessConnectionFactory mockConnectionFactory;
    private MBeanAccessConnection mockConnection;

    @Before
    public void setupTest() throws Exception {
        this.util = new JmxActiveMQUtil2();

        this.mockFactoryUtil = Mockito.mock(MBeanAccessConnectionFactoryUtil.class);
        this.mockConnectionFactory = Mockito.mock(MBeanAccessConnectionFactory.class);
        this.mockConnection = Mockito.mock(MBeanAccessConnection.class);

        Mockito.when(this.mockFactoryUtil.getLocationConnectionFactory(TEST_JMX_URL))
                .thenReturn(this.mockConnectionFactory);
        Mockito.when(this.mockConnectionFactory.createConnection()).thenReturn(this.mockConnection);

        this.util.setConnectionFactoryUtil(this.mockFactoryUtil);
    }

    @After
    public void cleanupTest() throws Exception {
    }

    @Test
    public void testFormatJmxUrl() throws Exception {
        String result = this.util.formatJmxUrl("x-host-x", 11);

        assertEquals("service:jmx:rmi:///jndi/rmi://x-host-x:11/jmxrmi", result);
    }

    @Test
    public void testGetLocationConnectionFactoryService() throws Exception {
        MBeanAccessConnectionFactory result = this.util.getLocationConnectionFactory(TEST_JMX_URL);

        assertSame(this.mockConnectionFactory, result);
    }

    @Test
    public void testQueryBrokerNames() throws Exception {
        Mockito.when(this.mockConnection.queryNames(new ObjectName(this.util.AMQ_BROKER_QUERY), null))
                .thenReturn(new HashSet<>(Arrays.asList(
                        new ObjectName("x-domain-x:" + this.util.AMQ_BROKER_NAME_KEY + "=broker1"),
                        new ObjectName("x-domain-x:" + this.util.AMQ_BROKER_NAME_KEY + "=broker2")
                )));

        String[] names = this.util.queryBrokerNames(TEST_JMX_URL);
        Set<String> nameSet = new HashSet<>(Arrays.asList(names));

        assertEquals(2, nameSet.size());
        assertTrue(nameSet.contains("broker1"));
        assertTrue(nameSet.contains("broker2"));
    }

    @Test
    public void testQueryQueueNames() throws Exception {
        // Don't let the lack of actual pattern create confusion; the mocking "hides" that.
        String pattern = "org.apache.activemq:type=Broker,brokerName=x-broker-x,destinationType=Queue," +
                "destinationName=x-queue-pattern-x";

        Mockito.when(this.mockConnection.queryNames(new ObjectName(pattern), null))
                .thenReturn(new HashSet<>(Arrays.asList(
                        new ObjectName("x-domain-x:" + this.util.AMQ_QUEUE_NAME_KEY + "=queue1"),
                        new ObjectName("x-domain-x:" + this.util.AMQ_QUEUE_NAME_KEY + "=queue2")
                )));

        String[] names = this.util.queryQueueNames(TEST_JMX_URL, "x-broker-x", "x-queue-pattern-x");
        Set<String> nameSet = new HashSet<>(Arrays.asList(names));

        assertEquals(2, nameSet.size());
        assertTrue(nameSet.contains("queue1"));
        assertTrue(nameSet.contains("queue2"));
    }

    @Test
    public void testGetDestinationObjectName() throws Exception {
        ObjectName result = this.util.getDestinationObjectName("x-broker-x", "x-dest-name-x", "x-dest-type-x");

        assertEquals(
                new ObjectName("org.apache.activemq:type=Broker,brokerName=x-broker-x," +
                        "destinationType=x-dest-type-x,destinationName=x-dest-name-x"), result);
    }

    @Test
    public void testExtractDestinationName() throws Exception {
        ObjectName destObjectName = new ObjectName("org.apache.activemq:type=Broker,brokerName=x-broker-name-x," +
                "destinationType=Queue,destinationName=x-dest-name-x");

        assertEquals("x-dest-name-x", this.util.extractDestinationName(destObjectName));
    }
}
