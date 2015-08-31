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
 * Created by art on 8/30/15.
 */
public class JMXHostPortConnectionFactorySupplierTest {

    private JMXHostPortConnectionFactorySupplier supplier;

    @Before
    public void setupTest() throws Exception {
        this.supplier = new JMXHostPortConnectionFactorySupplier();
    }

    @Test
    public void testHandlesLocation() throws Exception {
        assertTrue(this.supplier.handlesLocation("localhost:1099"));
        assertTrue(this.supplier.handlesLocation("serverx.domain.com:5000"));

        assertFalse(this.supplier.handlesLocation("jolokia:http://localhost:8080/api/jolokia"));
        assertFalse(this.supplier.handlesLocation("localhost:"));
        assertFalse(this.supplier.handlesLocation("localhost:abc"));
        assertFalse(this.supplier.handlesLocation("jvmId=123"));
    }

    @Test
    public void testCreateFactory() throws Exception {
        assertTrue(this.supplier.createFactory("localhost:1099") instanceof JMXRemoteUrlConnectionFactory);
    }

    @Test
    public void testCreateFactoryFailOnInvalidPort() throws Exception {
        try {
            this.supplier.createFactory("localhost:xyz");
            fail("missing expected exception");
        } catch ( NumberFormatException nfExc ) {
            // Good
        }
    }

    @Test
    public void testCreateFactoryFailOnInvalidSpec() throws Exception {
        try {
            this.supplier.createFactory("invalid-spec");
            fail("missing expected exception");
        } catch ( Exception exc) {
            assertEquals("invalid location: invalid-spec", exc.getMessage());
        }
    }
}