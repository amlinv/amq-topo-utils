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

import com.amlinv.jmxutil.connection.impl.JMXJvmIdConnectionFactory;
import com.amlinv.jmxutil.connection.impl.JolokiaConnectionFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by art on 8/31/15.
 */
public class JolokiaConnectionFactorySupplierTest {

    private JolokiaConnectionFactorySupplier supplier;

    @Before
    public void setupTest() throws Exception {
        this.supplier = new JolokiaConnectionFactorySupplier();
    }

    @Test
    public void testHandlesLocation() throws Exception {
        assertTrue(this.supplier.handlesLocation("jolokia:http://localhost:8080/api/jolokia"));
        assertTrue(this.supplier.handlesLocation("jolokia:x"));

        assertFalse(this.supplier.handlesLocation("localhost:1099"));
        assertFalse(this.supplier.handlesLocation("jvmId=1355"));
        assertFalse(this.supplier.handlesLocation("pid=1355"));
    }

    @Test
    public void testCreateFactory() throws Exception {
        assertTrue(this.supplier.createFactory("jvmId=1355") instanceof JolokiaConnectionFactory);
    }
}