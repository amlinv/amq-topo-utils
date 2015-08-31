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

import com.amlinv.jmxutil.connection.MBeanAccessConnectionFactory;
import com.amlinv.jmxutil.connection.impl.JMXJvmIdConnectionFactory;
import com.amlinv.jmxutil.connection.impl.JMXRemoteUrlConnectionFactory;
import com.amlinv.jmxutil.connection.impl.JolokiaConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by art on 8/30/15.
 */
public class MBeanAccessConnectionFactoryUtilTest {

    private MBeanAccessConnectionFactoryUtil util;

    private MBeanAccessConnectionFactorySupplier mockSupplier;
    private MBeanAccessConnectionFactory mockFactory;

    @Before
    public void setupTest() throws Exception {
        this.util = new MBeanAccessConnectionFactoryUtil();

        this.mockSupplier = Mockito.mock(MBeanAccessConnectionFactorySupplier.class);
        this.mockFactory = Mockito.mock(MBeanAccessConnectionFactory.class);
    }

    @Test
    public void testFormatJmxUrl() throws Exception {
        String url = MBeanAccessConnectionFactoryUtil.formatJmxUrl("x-hostname-x", 27);
        assertEquals("service:jmx:rmi:///jndi/rmi://x-hostname-x:27/jmxrmi", url);
    }

    @Test
    public void testRegisterMBeanAccessConnectionFactorySupplier() throws Exception {
        Mockito.when(this.mockSupplier.handlesLocation("x-location-x")).thenReturn(true);
        Mockito.when(this.mockSupplier.createFactory("x-location-x")).thenReturn(this.mockFactory);

        this.util.registerMBeanAccessConnectionFactorySupplier(this.mockSupplier);

        MBeanAccessConnectionFactory result = this.util.getLocationConnectionFactory("x-location-x");

        assertSame(this.mockFactory, result);
    }

    @Test
    public void testGetLocationConnectionFactory() throws Exception {
        MBeanAccessConnectionFactory factory;

        factory = this.util.getLocationConnectionFactory("jolokia:x-url-x");
        assertTrue(factory instanceof JolokiaConnectionFactory);

        factory = this.util.getLocationConnectionFactory("localhost:1099");
        assertTrue(factory instanceof JMXRemoteUrlConnectionFactory);

        factory = this.util.getLocationConnectionFactory("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
        assertTrue(factory instanceof JMXRemoteUrlConnectionFactory);

        factory = this.util.getLocationConnectionFactory("jvmId=40");
        assertTrue(factory instanceof JMXJvmIdConnectionFactory);
    }

    @Test
    public void testInvalidLocation() throws Exception {
        try {
            this.util.getLocationConnectionFactory("x-invalid-location-x");
            fail("missing expected exception");
        } catch ( Exception exc ) {
            assertEquals("invalid location: x-invalid-location-x", exc.getMessage());
        }
    }
}