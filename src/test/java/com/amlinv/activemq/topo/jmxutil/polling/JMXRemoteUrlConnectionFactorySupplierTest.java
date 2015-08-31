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

import com.amlinv.jmxutil.connection.impl.JMXRemoteUrlConnectionFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by art on 8/31/15.
 */
public class JMXRemoteUrlConnectionFactorySupplierTest {

    private JMXRemoteUrlConnectionFactorySupplier supplier;

    @Before
    public void setupTest() throws Exception {
        this.supplier = new JMXRemoteUrlConnectionFactorySupplier();
    }

    @Test
    public void testHandlesLocation() throws Exception {
        assertTrue(this.supplier.handlesLocation("service:x"));
        assertTrue(this.supplier.handlesLocation("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"));

        assertFalse(this.supplier.handlesLocation("localhost:1099"));
        assertFalse(this.supplier.handlesLocation("jolokia:http://localhost:8080/api/jolokia"));
        assertFalse(this.supplier.handlesLocation("localhost:"));
        assertFalse(this.supplier.handlesLocation("localhost:abc"));
        assertFalse(this.supplier.handlesLocation("jvmId=123"));
    }

    @Test
    public void testCreateFactory() throws Exception {
        assertTrue(this.supplier.createFactory("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi")
                instanceof JMXRemoteUrlConnectionFactory);
    }
}
